from datetime import datetime
import os
from pytz import timezone
import pymysql
from requests_aws4auth import AWS4Auth
#from elasticsearch import Elasticsearch, helpers, NotFoundError
from opensearchpy import OpenSearch, RequestsHttpConnection, helpers, NotFoundError
from tqdm import tqdm

# mysql 연결
mysql_conn = pymysql.connect(
    host=os.environ.get("YACO_DB_HOST"),
    user=os.environ.get("YACO_DB_USER"),
    password=os.environ.get("YACO_DB_PASSWORD"),
    charset='utf8'
)
cursor = mysql_conn.cursor()

# elasticsearch 연결
REGION = "ap-northeast-2"
AWS_ACCESS_KEY = os.environ.get("YACO_AWS_ADMIN_ACCESS_KEY")
AWS_SECRET_KEY = os.environ.get("YACO_AWS_ADMIN_SECRET_KEY")

awsauth = AWS4Auth(AWS_ACCESS_KEY, AWS_SECRET_KEY, REGION, "es")
es = OpenSearch(
    connection_class=RequestsHttpConnection,
    hosts=f"{os.environ.get('YACO_ES_HOST')}:{os.environ.get('YACO_ES_PORT')}",
    http_auth=awsauth,
)

alias = 'goods'
index_name = 'musinsa_goods'

def create_index():

    settings = {
        "number_of_shards": 1,
        "number_of_replicas": 0,
        "analysis": {
            "char_filter": {
                "clean_filter": {
                    "pattern": """[^\p{L}\p{Nd}\p{Blank}]""",
                    "type": "pattern_replace",
                    "replacement": " "
                }
            },
            "tokenizer": {
                "kor_tokenizer": {
                    "type": "seunjeon_tokenizer",
                    # "user_dictionary": "user_dictionary.txt"
                }
            },
            "filter": {
                "synonym_filter": {
                    "type": "synonym_graph",
                    # "synonyms_path": "synonyms.txt"
                    "synonyms": ["사탕 => 캔디"]
                }
            },
            "analyzer": {
                "index_analyzer": {
                    "type": "custom",
                    "char_filter": [
                        "clean_filter"
                    ],
                    "tokenizer": "kor_tokenizer",
                    "filter": [
                        "lowercase"
                    ]
                },
                "search_analyzer": {
                    "type": "custom",
                    "char_filter": [
                        "clean_filter"
                    ],
                    "tokenizer": "kor_tokenizer",
                    "filter": [
                        "lowercase",
                        "synonym_filter"
                    ]
                }
            }
        }
    }

    mappings = {
        "properties": {
            "id": {
                "type": "integer"
            },
            "title": {
                "type": "text",
                "analyzer": "index_analyzer",
                "search_analyzer": "search_analyzer"
            },
            "category_id": {
                "type": "integer"
            },
            "category_name": {
                "type": "text",
                "fields": {
                    "keyword": {
                        "type": "keyword"
                    }
                }
            },
            "parent_category_id": {
                "type": "integer"
            },
            "parent_category_name": {
                "type": "text",
                "fields": {
                    "keyword": {
                        "type": "keyword"
                    }
                }
            },
            "image_url": {
                "type": "keyword"
            },
            "click_count": {
                "type": "integer"
            },
            "sell_count": {
                "type": "integer"
            },
            "like_count": {
                "type": "integer"
            },
            "gender": {
                "type": "keyword"
            },
            "hash_tags": {
                "type": "text",
                "analyzer": "index_analyzer",
                "search_analyzer": "search_analyzer"
            },
            "price": {
                "type": "integer"
            },
            "link": {
                "type": "keyword"
            }
        }
    }

    body = {
        "settings": settings,
        "mappings": mappings,
    }

    tz = timezone('Asia/Seoul')
    now = datetime.now(tz).strftime('%Y%m%d%H%M%S')
    new_index_name = f'{index_name}_{now}'

    result = es.indices.create(
        index=new_index_name,
        body=body,
    )

    if result['acknowledged']:
        print(f'index {new_index_name}이 생성되었습니다.')
        return new_index_name


def index_data(new_index_name):
    cursor.execute('USE musinsa;')
    cursor.execute(
        'SELECT id, title, category_id, image_url, click_count, sell_count, like_count, gender, hash_tags, price, link FROM goods;'
    )
    result = cursor.fetchall()
    docs = []
    for row in tqdm(result):
        id, title, category_id, image_url, click_count, sell_count, like_count, gender, hash_tags, price, link = row
        hash_tags = [hash_tag.strip() for hash_tag in hash_tags.split('#') if hash_tag.strip()] if hash_tags else None
        cursor.execute('SELECT name, parent_id FROM category WHERE id=%s;', (category_id))
        category_rows = cursor.fetchall()
        category_name, parent_category_id = category_rows[0]
        cursor.execute('SELECT name FROM category WHERE id=%s;', (parent_category_id))
        parent_category_rows = cursor.fetchall()
        parent_category_name = parent_category_rows[0][0]

        doc = {
            '_op_type': 'index',
            '_index': new_index_name,
            '_id': id,
            '_source': {
                'id': id,
                'title': title,
                'category_id': category_id,
                'parent_category_id': parent_category_id,
                'category_name': category_name,
                'parent_category_name': parent_category_name,
                'image_url': image_url,
                'click_count': click_count,
                'sell_count': sell_count,
                'like_count': like_count,
                'gender': gender,
                'hash_tags': hash_tags,
                'price': price,
                'link': link
            }
        }

        docs.append(doc)
        if len(docs) >= 100:
            helpers.bulk(es, docs)
            docs = []
        if docs:
            helpers.bulk(es, docs)
            docs = []

def update_alias(new_index_name):
    try:
        old_index_names = es.indices.get_alias(name=alias)
        es.indices.put_alias(name=alias, index=new_index_name)
        if old_index_names:
            for index in [i for i in old_index_names if i.startswith(index_name)]:
                es.indices.delete_alias(name=alias, index=index)
                es.indices.delete(index=index)
    except NotFoundError:
        es.indices.put_alias(name=alias, index=new_index_name)

new_index_name = create_index()
index_data(new_index_name)
update_alias(new_index_name)
