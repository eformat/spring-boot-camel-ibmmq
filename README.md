## spring-boot-camel-ibmmq

Run IBM MQ
```
docker volume create mqm
docker run -p1414:1414 -p9443:9443 --env LICENSE=accept --env MQ_QMGR_NAME=QMGR1 --volume mqm:/mnt/mqm ibmcom/mq:9
```

Login IBM MQ WebConsole
```
https://localhost:9443/ibmmq/console/
admin / passw0rd
```

Clone this repo and build, run application
```
mvn spring-boot:run
``` 

You should see messages appearing on QMGR1 / DEV.QUEUE.1 
