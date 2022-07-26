import os

BASE_CONFIG = {
    'BASE_DIR': f'{os.environ.get("HOME")}/workspace/fashion_data',
    'SOURCE': 'musinsa'
}

S3_CONFIG = {
    'BUCKET_NAME': 'fashion-search',
    'AWS_ACCESS_KEY': os.environ.get("YAIN_AWS_S3_ACCESS_KEY"),
    'AWS_SECRET_KEY': os.environ.get("YAIN_AWS_S3_SECRET_KEY")
}

DB_CONFIG = {
    'HOST': 'localhost',
    'USER': 'root',
    'PASSWORD': 'root',
}