import os

BASE_CONFIG = {
    'BASE_DIR': f'{os.environ.get("HOME")}/workspace/fashion_data',
    'SOURCE': 'musinsa'
}

S3_CONFIG = {
    'BUCKET_NAME': 'fashion-search',
    'AWS_ACCESS_KEY': os.environ.get("YACO_AWS_ADMIN_ACCESS_KEY"),
    'AWS_SECRET_KEY': os.environ.get("YACO_AWS_ADMIN_SECRET_KEY")
}