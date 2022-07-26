ARG ELASTIC_VERSION

# https://www.docker.elastic.co/
FROM docker.elastic.co/elasticsearch/elasticsearch:${ELASTIC_VERSION}

#nori
RUN elasticsearch-plugin install analysis-nori

# Example: RUN elasticsearch-plugin install analysis-icu
# RUN elasticsearch-plugin install https://github.com/likejazz/seunjeon-elasticsearch-7/releases/download/7.9.1/analysis-seunjeon-7.9.1.zip

