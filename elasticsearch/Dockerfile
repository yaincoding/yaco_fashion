ARG ELASTIC_VERSION

# https://www.docker.elastic.co/
FROM docker.elastic.co/elasticsearch/elasticsearch:${ELASTIC_VERSION}

#nori
RUN elasticsearch-plugin install analysis-nori

COPY hanhinsam.zip /plugins/hanhinsam.zip
RUN elasticsearch-plugin install file:///plugins/hanhinsam.zip
