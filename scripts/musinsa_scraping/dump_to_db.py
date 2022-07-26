import os
import boto3
import pandas as pd
import pymysql
from settings import S3_CONFIG, DB_CONFIG, BASE_CONFIG

def download_goods_data():
    s3 = boto3.client(
        service_name='s3',
        region_name='ap-northeast-2',
        aws_access_key_id=S3_CONFIG['AWS_ACCESS_KEY'],
        aws_secret_access_key=S3_CONFIG['AWS_SECRET_KEY']
    )

    response = s3.list_objects_v2(
        Bucket=S3_CONFIG['BUCKET_NAME']
    )
    contents = response['Contents']
    keys = [content['Key'] for content in contents]
    keys = [key for key in keys if key.startswith('goods')]

    base_dir = f'{os.environ.get("HOME")}/workspace/musinsa_data'
    for key in keys:
        if os.path.isfile(f'{base_dir}/{key}'):
            continue
        sub_dir = '/'.join(key.split('/')[:-1])
        os.makedirs(f'{base_dir}/{sub_dir}')
        s3.download_file(
            Bucket=S3_CONFIG['BUCKET_NAME'],
            Key=key,
            Filename=f'{base_dir}/{key}'
        )
    s3.close()

download_goods_data()

mysql_conn = pymysql.connect(
    host=DB_CONFIG["HOST"],
    user=DB_CONFIG['USER'],
    password=DB_CONFIG['PASSWORD'],
    charset='utf8'
)

cursor = mysql_conn.cursor()

# db 생성
def create_database():
    create_database_sql = 'CREATE DATABASE IF NOT EXISTS `musinsa`;'
    cursor.execute(create_database_sql)
    cursor.execute('USE musinsa;')

create_database()

# table 생성
def create_category_table():
    cursor.execute('DROP TABLE IF EXISTS `category`;')
    create_category_table_sql = '''
        CREATE TABLE `category` (
            `id` int NOT NULL AUTO_INCREMENT,
            `name` varchar(255) NOT NULL,
            `parent_id` int DEFAULT NULL,
            `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
            `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            PRIMARY KEY (`id`)
        ) ENGINE=InnoDB AUTO_INCREMENT=45002 DEFAULT CHARSET=utf8mb3;
    '''
    cursor.execute(create_category_table_sql)

def create_goods_table():
    cursor.execute('DROP TABLE IF EXISTS `goods`;')
    create_goods_table_sql = '''
        CREATE TABLE `goods` (
            `id` int NOT NULL AUTO_INCREMENT,
            `title` varchar(255) DEFAULT NULL,
            `category_id` int NOT NULL DEFAULT 0,
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
            PRIMARY KEY (`id`)
        ) ENGINE=InnoDB AUTO_INCREMENT=45002 DEFAULT CHARSET=utf8mb3;
    '''
    cursor.execute(create_goods_table_sql)

create_category_table()
create_goods_table()

def insert_category_data():
    fname = f'{BASE_CONFIG["BASE_DIR"]}/{BASE_CONFIG["SOURCE"]}/category/category.csv'
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

    goods_dir = f'{BASE_CONFIG["BASE_DIR"]}/{BASE_CONFIG["SOURCE"]}/goods'
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

insert_category_data()
insert_goods_data()

mysql_conn.close()