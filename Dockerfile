FROM registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift:latest
COPY target/helloservice-1.0-SNAPSHOT.jar /deployments/

