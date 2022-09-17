import os

BASE_CONFIG = {
    'BASE_DIR': f'{os.environ.get("HOME")}/workspace/fashion_data',
    'SOURCE': 'musinsa'
}

S3_CONFIG = {
    'BUCKET_NAME': 'fashion-search',
}