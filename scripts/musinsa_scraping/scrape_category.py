import csv
import os
import boto3
from bs4 import BeautifulSoup
from urllib.request import urlopen
import time
import pandas as pd
from tqdm import tqdm
from settings import BASE_CONFIG, S3_CONFIG

HOST = "https://www.musinsa.com"

category_map = {
    "001": "상의", 
    "002": "아우터",
    "003": "바지",
    "020": "원피스",
    "022": "스커트",
    "026": "속옷",
    "006": "시계",
    "005": "신발"
}

def get_child_categories(parent_category_code: str):
    url = f"{HOST}/category/{parent_category_code}"
    html = urlopen(url)
    bsObject = BeautifulSoup(html, "html.parser")
    items = bsObject.find('div', attrs={'id': 'category_2depth_list'})\
                    .find('dl', attrs={'class': 'list_division_brand'})\
                    .find('dd')\
                    .findAll('a')
            
    child_categories = []
    for item in items:
        code:str = item['data-code']
        name:str = item['data-value']

        child_categories.append((code, parent_category_code ,name))

    return child_categories


def scrape_category_data(save_path):
    categories = []
    for parent_code, name in tqdm(category_map.items()):
        categories.append((parent_code, None, name))
        child_categories = get_child_categories(parent_code)
        categories.extend(child_categories)
        time.sleep(1)
    
    df = pd.DataFrame(
        columns=['code', 'parent_code', 'name'],
        data=categories,
    )
    df.to_csv(
        save_path,
        sep=',',
        header=True,
        index=False,
        quoting=csv.QUOTE_NONE
    )

def upload_to_s3(save_path, key):
    s3 = boto3.resource(
        service_name='s3',
        region_name='ap-northeast-2',
        aws_access_key_id=S3_CONFIG['AWS_ACCESS_KEY'],
        aws_secret_access_key=S3_CONFIG['AWS_SECRET_KEY']
    )

    bucket = s3.Bucket(name=S3_CONFIG['BUCKET_NAME'])
    bucket.upload_file(save_path, key)


if __name__ == "__main__":
    base_dir = BASE_CONFIG['BASE_DIR']
    sub_dir = f'{BASE_CONFIG["SOURCE"]}/category'
    if not os.path.isdir(f'{base_dir}/{sub_dir}'):
        os.makedirs(f'{base_dir}/{sub_dir}')

    file_name = 'category.csv'
    key = f'{sub_dir}/{file_name}'
    save_path = f'{base_dir}/{key}'
    scrape_category_data(save_path)
    upload_to_s3(save_path, key)