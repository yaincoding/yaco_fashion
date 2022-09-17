import os
import pymysql

mysql_conn = pymysql.connect(
    host=os.environ.get("YACO_DB_HOST"),
    user=os.environ.get("YACO_DB_USER"),
    password=os.environ.get("YACO_DB_PASSWORD"),
    charset='utf8'
)

cursor = mysql_conn.cursor()

cursor.execute('USE yaco_fashion;')

def create_word_table():
    cursor.execute('DROP TABLE IF EXISTS `es_word`;')
    sql = '''
        CREATE TABLE `es_word` (
            `id` int NOT NULL AUTO_INCREMENT,
            `word` varchar(31) NOT NULL,
            `expression` varchar(31),
            `active` boolean NOT NULL DEFAULT 1,
            `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
            `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            PRIMARY KEY (`id`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
    '''
    cursor.execute(sql)

def create_synonym_table():
    cursor.execute('DROP TABLE IF EXISTS `es_synonym`;')
    sql = '''
        CREATE TABLE `es_synonym` (
            `id` int NOT NULL AUTO_INCREMENT,
            `word` varchar(31) NOT NULL,
            `synonym` varchar(31) NOT NULL,
            `active` boolean NOT NULL DEFAULT 1,
            `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
            `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            PRIMARY KEY (`id`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
    '''
    cursor.execute(sql)

create_word_table()
create_synonym_table()

mysql_conn.commit()
cursor.close()
mysql_conn.close()
