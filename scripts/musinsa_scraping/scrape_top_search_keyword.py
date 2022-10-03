import csv
import os
import boto3
from bs4 import BeautifulSoup
from urllib.request import urlopen
import time
import pandas as pd
from tqdm import tqdm

HOST = "https://www.musinsa.com"
BUCKET_NAME = 'fashion-search'

def scrape_top_search_keywords():
    url = f'{HOST}/ranking/keyword'
    html = urlopen(url)
    bsObject = BeautifulSoup(html, "html.parser")

    titles = []
    ranking_columns = bsObject.findAll('ol', attrs={'class': 'sranking_list'})
    for column in ranking_columns:
        lis = column.findAll('li')
        for li in lis:
            titles.append(li.find('a')['title'])
    
    return titles

def upload_to_s3(keywords: list):
    df = pd.DataFrame(
        columns=['keyword'],
        data=keywords,
    )

    save_dir = f'{os.environ.get("HOME")}/workspace/fashion-search/musinsa/top_keyword'
    if not os.path.isdir(save_dir):
        os.makedirs(save_dir)

    fname = 'top_keyword.csv'
    save_path = f'{save_dir}/{fname}'
    df.to_csv(
        save_path,
        sep=',',
        header=True,
        index=False,
        quoting=csv.QUOTE_NONE
    )

    s3 = boto3.resource(
        service_name='s3',
        region_name='ap-northeast-2',
        aws_access_key_id=os.environ.get("AWS_ACCESS_KEY_ID"),
        aws_secret_access_key=os.environ.get("AWS_SECRET_ACCESS_KEY")
    )

    bucket = s3.Bucket(name=BUCKET_NAME)
    bucket.upload_file(save_path, 'musinsa/top_keyword/top_keyword.csv')

if __name__ == "__main__":
    top_keywords = scrape_top_search_keywords()
    upload_to_s3(top_keywords)