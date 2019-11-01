#!/bin/bash
mvn clean package
docker build -f Dockerfile -t spring-boot-camel-ibmmq .
docker run --net host --rm spring-boot-camel-ibmmq:latest
