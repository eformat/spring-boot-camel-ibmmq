/**
 * Copyright 2005-2016 Red Hat, Inc.
 * <p>
 * Red Hat licenses this file to you under the Apache License, version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.fabric8.quickstarts.camel;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A spring-boot application that includes a Camel route builder to setup the Camel routes
 */
@SpringBootApplication
@EnableDiscoveryClient
// @ImportResource({"classpath:spring/camel-context.xml"})
public class Application {

    // must have a main method spring-boot can run
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@ConditionalOnProperty(prefix = "helloservice", name = "oneway", havingValue = "true", matchIfMissing = true)
@Component
class HelloRouterInOnly extends RouteBuilder {

    @Autowired
    ApplicationConfigBean appConfig;

    public void configure() throws Exception {
        // @formatter:off
        from("timer://foo?period=" + appConfig.getTime())
            .threads(appConfig.getMqPoolMaxConnections())
            .setBody()
            .constant(appConfig.getMessage())
            .setExchangePattern(ExchangePattern.InOnly)
            .to(appConfig.getMqQueueNameIn())
            .log(">>> ${body}");
        // @formatter:on
    }

}

@ConditionalOnProperty(prefix = "helloservice", name = "oneway", havingValue = "false", matchIfMissing = false)
@Component
class HelloRouterInOut extends RouteBuilder {

    @Autowired
    ApplicationConfigBean appConfig;

    @Override
    public void configure() throws Exception {

        from(appConfig.getMqQueueNameIn())
            .log("Received Request: ${header.JMSCorrelationID} >>> ${body}")
            .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setBody("<<< hi from processor!");
                    }
            })
            .end();

        // @formatter:off
        from("timer://foo?period=" + appConfig.getTime())
            .threads(appConfig.getMqPoolMaxConnections())
            .setBody()
            .constant(appConfig.getMessage())
            .setExchangePattern(ExchangePattern.InOut)
            .log("Request: >>> ${body}")
            .to(appConfig.getMqQueueNameInOut())
            .log("Received Response: ${header.JMSCorrelationID} ${body}")
            .end();
        // @formatter:on
    }

}
