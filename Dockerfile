FROM openjdk:17.0.2-jdk-slim

RUN apt-get update -y
RUN apt-get upgrade -y
RUN apt-get install curl --yes
RUN curl -sL https://deb.nodesource.com/setup_18.x | bash -
RUN apt-get -y install nodejs --yes

COPY . /yaco_fashion

WORKDIR /yaco_fashion

RUN ./gradlew clean build

ENTRYPOINT ["java", "-jar", "/yaco_fashion/build/libs/yaco_fashion-0.0.1.jar"]