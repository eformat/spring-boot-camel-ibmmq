## spring-boot-camel-ibmmq

#### Send Message

Run IBM MQ
```
docker volume create mqm
docker run -p1414:1414 -p9443:9443 --env LICENSE=accept --env MQ_QMGR_NAME=QMGR1 --env MQ_APP_PASSWORD=app --volume mqm:/mnt/mqm ibmcom/mq:9
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

#### Request / Response

There is an `InOut` (Request/Response) example that can be run by setting

```
helloservice:
    oneway: false
```

This makes Requests on QMGR1 / DEV.QUEUE.1 with ReplyTo on QMGR1 / DEV.QUEUE.2


## Multi-instance Queue Managers

Active, Standby Queue Managers with the same Queue Manager Name.

See: https://www.ibm.com/support/pages/using-websphere-mq-automatic-client-reconnection-websphere-mq-classes-jms

Set `mqHosts: hostname1(port1), hostname2(port2)`

Results in a call to:
```
connectionFactory.setConnectionNameList("serverA.internal.company.address(1414),"
                                      + "serverB.internal.company.address(1414)")
```

## Cluster Queue Managers / Mutiple Connection Factories

`To Be Done`

For Load Balancing across different Queue Managers via Pods, there are many more considerations.

see https://github.com/ibm-messaging/mq-jms-spring/issues/7

MQ Side - Running AMQSCLM


## OpenShift

Create Project
```
oc new-project ibmmq-spring
```

IBM MQ
```
git clone git@github.com:IBM/charts.git ibm-charts

oc create -f - <<EOF
apiVersion: v1
kind: Secret
metadata:  
    name: mq-secret
    namespace: ibmmq-spring
type: Opaque
data:  
    adminPassword: cGFzc3cwcmQ=
EOF

-- storage does not work in ocp4 for now
-- ~/git/ibm-charts/stable/ibm-mqadvanced-server-dev/values.yaml
# persistence section specifies persistence settings which apply to the whole chart
persistence:
  # enabled is whether to use Persistent Volumes or not
  enabled: false

-- use helm chart to install
helm install --name mqm ibm-mqadvanced-server-dev --set license=accept --set queueManager.dev.secret.name=mq-secret --set queueManager.dev.secret.adminPasswordKey=adminPassword --set queueManager.name=QMGR1 -n ibmmq-spring

-- create route to ibm.mq web console
oc expose svc ibmmq-tomcat-ibm-mq --port=9443
oc patch route/ibmmq-tomcat-ibm-mq --type=json -p '[{"op":"add", "path":"/spec/tls", "value":{"termination":"passthrough"}}]'
```

Application

Update `context.xml` so that `HOST` maps to OpenShift Service

```
  HOST="ibmmq-spring-ibm-mq"
```

Deploy (using s2i build)

```
oc new-app registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift~https://github.com/eformat/spring-boot-camel-ibmmq --strategy=source --build-env MAVEN_MIRROR_URL=http://nexus.nexus.svc.cluster.local:8081/repository/maven-public/
```

Deploy (using docker build and binary app)

```
oc new-build --binary --name=spring-camel
oc start-build spring-camel --from-dir=. --follow
oc new-app spring-camel
```

## Links

IBM SpringBoot example including pooling

- git clone git@github.com:ibm-messaging/mq-jms-spring.git
 
