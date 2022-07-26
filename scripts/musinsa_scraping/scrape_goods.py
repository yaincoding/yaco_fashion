import csv
import os

import boto3
from botocore.errorfactory import ClientError
from bs4 import BeautifulSoup
 
from selenium.webdriver.common.by import By

from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from webdriver_manager.chrome import ChromeDriverManager # Mac M1

from urllib.request import urlopen, Request
import pandas as pd
from tqdm import tqdm
from settings import BASE_CONFIG ,S3_CONFIG

HOST = "https://www.musinsa.com"

done_category_codes = [

]

s3 = boto3.client(
    service_name='s3',
    region_name='ap-northeast-2',
    aws_access_key_id=S3_CONFIG['AWS_ACCESS_KEY'],
    aws_secret_access_key=S3_CONFIG['AWS_SECRET_KEY']
)

KEY_PREFIX = "goods"

def download_category_file():
    s3.download_file(
        Bucket=S3_CONFIG['BUCKET_NAME'],
        Key=f'{BASE_CONFIG["SOURCE"]}/category/category.csv',
        Filename='category.csv',
    )

def read_category_codes():
    category_df = pd.read_csv(
        f'{os.environ.get("HOME")}/workspace/fashion_search/scripts/musinsa_scraping/category.csv',
        dtype={'code': str, 'parent_code': str, 'name': str}
    )

    parent_category_map = {}
    for _, row in category_df.iterrows():
        if pd.isna(row['parent_code']):
            parent_category_map[str(row['code'])] = row['name']

    categories = []
    for _, row in category_df.iterrows():
        if row['code'] in done_category_codes:
            continue
        if pd.isna(row['parent_code']):
            continue
        categories.append((str(row['code']), parent_category_map[str(row['parent_code'])], row['name']))
    return categories

def get_goods_links(category_code: str, page: int):
    url = f"{HOST}/category/{category_code}"
    url_params = []
    url_params.append(f'd_cat_cd={category_code}')
    url_params.append('page_kind=search')
    url_params.append('list_kind=small')
    url_params.append('sort=sale_high')
    url_params.append('sub_sort=1m')
    url_params.append(f'page={page}')
    url_params.append('display_cnt=10')
    url_params.append('kids=N')
    url = url + "?" + "&".join(url_params)

    req = Request(url=url, headers={'User-Agent': 'Mozilla/5.0'})
    html = urlopen(req)
    # html = requests.get(url=url, headers={'User-Agent': 'Mozilla/5.0'}).text

    bsObject = BeautifulSoup(html, "html.parser")
    goods_list = bsObject\
                    .find('div', attrs={'id': 'goods_list'})\
                    .find('div', attrs={'class': 'list-box box'})\
                    .findAll('li', attrs={'class': 'li_box'})

    goods_links = []
    for goods in goods_list:
        link = goods.find('a', attrs={'name': 'goods_link'})['href']
        goods_links.append(link)

    return goods_links

options = webdriver.ChromeOptions()
options.add_argument('--no-sandbox')
options.add_argument('--disable-dev-shm-suage')
#brwoser = webdriver.Chrome(options=options)
brwoser = webdriver.Chrome(ChromeDriverManager().install())


def get_goods_data(link: str):

    brwoser.get(link)

    try:
        WebDriverWait(brwoser, 10).until(EC.element_to_be_clickable((By.ID, 'li_pageview_1m')))
        WebDriverWait(brwoser, 10).until(EC.element_to_be_clickable((By.ID, 'li_sales_1y')))
        WebDriverWait(brwoser, 10).until(EC.element_to_be_clickable((By.ID, 'product-top-like')))
    except Exception:
        pass

    html = brwoser.page_source

    bsObject = BeautifulSoup(html, "html.parser")
    title = bsObject\
                    .find('span', attrs={'class': 'product_title'})\
                    .find('em').get_text()

    image_url = "https:" + bsObject\
                    .find('div', attrs={'class': 'product_left'})\
                    .find('div', attrs={'id': 'detail_bigimg'})\
                    .find('div', attrs={'class': 'product-img'})\
                    .find('img')['src']

    product_info = bsObject\
        			.find('div', attrs={'id': 'product_order_info'})\
                    .find('div', attrs={'class': 'explan_product product_info_section'})\
                    .find('ul', attrs={'class': 'product_article'})

    try:
        click_count = product_info\
                        .find('li', attrs={'id': 'li_pageview_1m'})\
                        .find('strong', attrs={'id': 'pageview_1m'})\
                        .get_text().replace('회 이상', '').replace('회 미만', '').strip()
                        
        if click_count.endswith('만'):
            click_count = int(float(click_count[:-1]) * 10000)
        elif click_count.endswith('천'):
            click_count = int(float(click_count[:-1]) * 1000)
        else:
            click_count = int(click_count)
    except Exception as e:
        print('click_count', e)
        click_count = 0
    
    try:
        sell_count = product_info\
                        .find('li', attrs={'id': 'li_sales_1y'})\
                        .find('strong', attrs={'id': 'sales_1y_qty'})\
                        .get_text().replace('개 이상', '').replace('개 미만', '').strip()

        if sell_count.endswith('만'):
            sell_count= int(float(sell_count[:-1]) * 10000)
        elif sell_count.endswith('천'):
            sell_count = int(float(sell_count[:-1]) * 1000)
        else:
            sell_count = int(sell_count)
    except Exception as e:
        sell_count = 0
        print('sell_count', e)

    try:
        like_count = product_info\
                        .find('li', attrs={'class': 'product_section_like'})\
                        .find('span', attrs={'class': 'prd_like_cnt'})\
                        .get_text().replace(',', '').strip()

        if like_count.endswith('만'):
            like_count= int(float(like_count[:-1]) * 10000)
        elif like_count.endswith('천'):
            like_count = int(float(like_count[:-1]) * 1000)
        else:
            like_count = int(like_count)
    
    except Exception as e:
        like_count = 0
        print('like_count', e)
        
    gender_tags = product_info\
                .find('span', attrs={'class': 'txt_gender'})\
                .findAll('span')

    genders = [gender_tag.get_text() for gender_tag in gender_tags]
    gender = ",".join(genders)

    try:
        hash_tags = product_info\
                                .find('li', attrs={'class': 'article-tag-list'})\
                                .find('p', attrs={'class': 'product_article_contents'})\
                                .findAll('a', attrs={'class': 'listItem'})
        hash_tags = " ".join([h.get_text() for h in hash_tags])
    except Exception as e:
        print('hash_tags', e)
        hash_tags = ""

    price = bsObject\
                            .find('div', attrs={'id': 'product_order_info'})\
                            .find('div', attrs={'class': 'explan_product price_info_section'})\
                            .find('ul', attrs={'class': 'product_article'})\
                            .find('li', attrs={'id': 'normal_price'})\
                            .get_text()
    goods_data = {
        'title': title,
        'image_url': image_url,
        'click_count': click_count,
        'sell_count': sell_count,
        'like_count': like_count,
        'gender': gender,
        'hash_tags': hash_tags,
        'price': price,
        'link': link,
    }
    print(list(goods_data.values()))

    return goods_data

def scrape(pages_per_category: int):
    categories = read_category_codes()
    for category_code, parent_category_name, category_name in tqdm(categories):
        for page in range(1, pages_per_category + 1):
            key = f'{KEY_PREFIX}/{category_code}/{page}.csv'
            try:
                if s3.head_object(Bucket=S3_CONFIG['BUCKET_NAME'], Key=key):
                    continue
            except ClientError:
                pass

            goods_list = []
            links = get_goods_links(category_code, page)
            for link in links:
                goods_data = get_goods_data(link)
                goods_data['parent_category'] = parent_category_name
                goods_data['category'] = category_name
                goods_list.append(goods_data)
            df = pd.DataFrame(
                columns=['title', 'parent_category', 'category', 'image_url', 'click_count', 'sell_count', 'like_count', 'gender', 'hash_tags', 'price', 'link'],
                data=goods_list
            )
            save_dir = f'{BASE_CONFIG["BASE_DIR"]}/{BASE_CONFIG["SOURCE"]}/{KEY_PREFIX}/{category_code}'
            if not os.path.isdir(save_dir):
                os.makedirs(save_dir)
            save_path = f'{save_dir}/{page}.csv'
            df.to_csv(
                    save_path,
                    sep=',',
                    header=True,
                    index=False,
                    quoting=csv.QUOTE_ALL
                )

            key = f'{BASE_CONFIG["SOURCE"]}/{KEY_PREFIX}/{category_code}/{page}.csv'
            s3.upload_file(save_path, S3_CONFIG['BUCKET_NAME'], key)
            
if __name__ == "__main__":
    pages_per_category = 1
    download_category_file()
    scrape(pages_per_category)