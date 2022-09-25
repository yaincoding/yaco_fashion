ARG ELASTIC_VERSION

# https://www.docker.elastic.co/
FROM docker.elastic.co/elasticsearch/elasticsearch:${ELASTIC_VERSION}

#nori
RUN elasticsearch-plugin install analysis-nori

RUN wget https://github.com/yaincoding/hanhinsam/raw/master/zip/elasticsearch-8.3.3/hanhinsam-0.1.jar -O /plugins/hanhinsam.zip

RUN elasticsearch-plugin install file:///plugins/hanhinsam.zip
