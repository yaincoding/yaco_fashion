import os
import boto3
import pandas as pd
import pymysql

BUCKET_NAME = 'fashion-search'
CATEGORY_PREFIX = 'musinsa/category'
GOODS_PREFIX = 'musinsa/goods'
TOP_KEYWORD_PREFIX = 'musinsa/top_keyword'
DB_NAME = os.environ.get("YACO_DB_NAME")

def download_category_data():

    base_dir = f'{os.environ.get("HOME")}/workspace/fashion-search/{CATEGORY_PREFIX}'
    if not os.path.isdir(base_dir):
        os.makedirs(base_dir)

    if os.path.isfile(f'{base_dir}/category.csv'):
        return

    s3 = boto3.client(
        service_name='s3',
        region_name='ap-northeast-2',
        aws_access_key_id=os.environ.get("AWS_ACCESS_KEY_ID"),
        aws_secret_access_key=os.environ.get("AWS_SECRET_ACCESS_KEY")
    )

    response = s3.list_objects_v2(
        Bucket=BUCKET_NAME,
        Prefix=CATEGORY_PREFIX,
    )

    contents = response['Contents']
    key = [content['Key'] for content in contents][0]

    s3.download_file(
        Bucket=BUCKET_NAME,
        Key=key,
        Filename=f'{base_dir}/category.csv'
    )
    s3.close()

def download_goods_data():

    base_dir = f'{os.environ.get("HOME")}/workspace'

    s3 = boto3.client(
        service_name='s3',
        region_name='ap-northeast-2',
        aws_access_key_id=os.environ.get("AWS_ACCESS_KEY_ID"),
        aws_secret_access_key=os.environ.get("AWS_SECRET_ACCESS_KEY")
    )

    response = s3.list_objects_v2(
        Bucket=BUCKET_NAME,
        Prefix=GOODS_PREFIX,
    )

    keys = [content['Key'] for content in response['Contents']]

    for key in keys:
        if os.path.isfile(f'{base_dir}/{BUCKET_NAME}/{key}'):
            continue
        sub_dir = '/'.join(key.split('/')[:-1])
        if not os.path.isdir(f'{base_dir}/{sub_dir}'):
            os.makedirs(f'{base_dir}/{BUCKET_NAME}/{sub_dir}')
        s3.download_file(
            Bucket=BUCKET_NAME,
            Key=key,
            Filename=f'{base_dir}/{BUCKET_NAME}/{key}'
        )
    s3.close()

def download_top_keyword():
    base_dir = f'{os.environ.get("HOME")}/workspace/fashion-search/{TOP_KEYWORD_PREFIX}'
    if not os.path.isdir(base_dir):
        os.makedirs(base_dir)

    if os.path.isfile(f'{base_dir}/top_keyword.csv'):
        return

    s3 = boto3.client(
        service_name='s3',
        region_name='ap-northeast-2',
        aws_access_key_id=os.environ.get("AWS_ACCESS_KEY_ID"),
        aws_secret_access_key=os.environ.get("AWS_SECRET_ACCESS_KEY")
    )

    response = s3.list_objects_v2(
        Bucket=BUCKET_NAME,
        Prefix=TOP_KEYWORD_PREFIX,
    )

    contents = response['Contents']
    key = [content['Key'] for content in contents][0]

    s3.download_file(
        Bucket=BUCKET_NAME,
        Key=key,
        Filename=f'{base_dir}/top_keyword.csv'
    )
    s3.close()

download_category_data()
download_goods_data()
download_top_keyword()

mysql_conn = pymysql.connect(
    host=os.environ.get("YACO_DB_HOST"),
    user=os.environ.get("YACO_DB_USER"),
    password=os.environ.get("YACO_DB_PASSWORD"),
    charset='utf8'
)

cursor = mysql_conn.cursor()

# db 생성
def create_database():
    create_database_sql = f'CREATE DATABASE IF NOT EXISTS `{DB_NAME}`;'
    cursor.execute(create_database_sql)
    cursor.execute(f'USE {DB_NAME};')

create_database()

def drop_tables():
    cursor.execute('DROP TABLE IF EXISTS `goods`;')
    cursor.execute('DROP TABLE IF EXISTS `category`;')
    cursor.execute('DROP TABLE IF EXISTS `top_keyword`;')

drop_tables()

# table 생성
def create_category_table():
    create_category_table_sql = '''
        CREATE TABLE `category` (
            `id` bigint NOT NULL AUTO_INCREMENT,
            `name` varchar(255) NOT NULL,
            `parent_id` bigint DEFAULT NULL,
            `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
            `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            PRIMARY KEY (`id`)
        ) ENGINE=InnoDB AUTO_INCREMENT=45002 DEFAULT CHARSET=utf8mb3;
    '''
    cursor.execute(create_category_table_sql)

def create_goods_table():
    create_goods_table_sql = '''
        CREATE TABLE `goods` (
            `id` bigint NOT NULL AUTO_INCREMENT,
            `title` varchar(255) DEFAULT NULL,
            `category_id` bigint NOT NULL DEFAULT 0,
            `image_url` text,
            `click_count` int NOT NULL DEFAULT 0,
            `sell_count` int NOT NULL DEFAULT 0,
            `like_count` int NOT NULL DEFAULT 0,
            `gender` varchar(10) DEFAULT NULL,
            `hash_tags` varchar(255) DEFAULT NULL,
            `price` int NOT NULL,
            `link` varchar(511) DEFAULT NULL,
            `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
            `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            PRIMARY KEY (`id`),
            FOREIGN KEY (`category_id`) REFERENCES category(`id`)
        ) ENGINE=InnoDB AUTO_INCREMENT=45002 DEFAULT CHARSET=utf8mb3;
    '''
    cursor.execute(create_goods_table_sql)

def create_top_keyword_table():
    create_top_keyword_table_sql = '''
        CREATE TABLE `top_keyword` (
            `id` bigint NOT NULL AUTO_INCREMENT,
            `keyword` varchar(255) NOT NULL,
            `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
            `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            PRIMARY KEY (`id`)
        ) ENGINE=InnoDB AUTO_INCREMENT=45002 DEFAULT CHARSET=utf8mb3;
    '''
    cursor.execute(create_top_keyword_table_sql)

create_category_table()
create_goods_table()
create_top_keyword_table()

def insert_category_data():
    fname = f'{os.environ.get("HOME")}/workspace/{BUCKET_NAME}/{CATEGORY_PREFIX}/category.csv'
    category_df = pd.read_csv(
        fname,
        dtype={'code': str, 'parent_code': str, 'name': str}
    )
    parent_category_map = {}
    for _, row in category_df.iterrows():
        if pd.isna(row['parent_code']):
            insert_sql = 'INSERT INTO category (name) VALUES (%s);'
            cursor.execute(insert_sql, (row['name']))
            id = cursor.lastrowid
            parent_category_map[str(row['code'])] = id

    for _, row in category_df.iterrows():
        if pd.isna(row['parent_code']):
            continue
        parent_id = parent_category_map[str(row['parent_code'])]
        name = row['name']
        insert_sql = 'INSERT INTO category (name, parent_id) VALUES (%s, %s);'
        cursor.execute(insert_sql, (name, parent_id))
    mysql_conn.commit()


def insert_goods_data():
    
    cursor.execute('SELECT id, name FROM category;')
    result = cursor.fetchall()

    category_map = {}
    for row in result:
        id, name = row
        category_map[name] = id

    goods_dir = f'{os.environ.get("HOME")}/workspace/{BUCKET_NAME}/{GOODS_PREFIX}'
    insert_sql = '''
    INSERT INTO goods
        (title, category_id, image_url, click_count, sell_count, like_count, gender, hash_tags, price, link)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s);
    '''

    for category_dir in os.listdir(goods_dir):
        fnames = os.listdir(f'{goods_dir}/{category_dir}')
        for fname in fnames:
            goods_df = pd.read_csv(f'{goods_dir}/{category_dir}/{fname}')
            for _, row in goods_df.iterrows():
                title = row['title']
                category_id = category_map[row['category']]
                image_url = row['image_url']
                click_count = row['click_count']
                sell_count = row['sell_count']
                like_count = row['like_count']
                gender = row['gender']
                hash_tags = row['hash_tags']
                if pd.isna(hash_tags):
                    hash_tags = None
                price = row['price']
                link = row['link']
                cursor.execute(insert_sql, (title, category_id, image_url, click_count, sell_count, like_count, gender, hash_tags, price, link))
    mysql_conn.commit()

def insert_top_keyword_data():
    fname = f'{os.environ.get("HOME")}/workspace/{BUCKET_NAME}/{TOP_KEYWORD_PREFIX}/top_keyword.csv'
    df = pd.read_csv(
        fname,
        dtype={'keyword': str}
    )

    for _, row in df.iterrows():
        keyword = row['keyword']
        insert_sql = 'INSERT INTO top_keyword (keyword) VALUES (%s);'
        cursor.execute(insert_sql, (keyword))
    mysql_conn.commit() 

insert_category_data()
insert_goods_data()
insert_top_keyword_data()

mysql_conn.close()