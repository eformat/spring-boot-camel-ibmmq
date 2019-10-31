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

import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Component;

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

@Component
class HelloRouter extends RouteBuilder {

    @Autowired
    ApplicationConfigBean appConfig;

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("timer://foo?period=" + appConfig.getTime())
            .threads(appConfig.getMqPoolMaxConnections())
            .setBody()
            .constant(appConfig.getMessage())
            .setExchangePattern(ExchangePattern.InOnly) // set to InOut for Request/Response
            //.setHeader("CamelJmsDestinationName", constant(replyQueueName)) // and set reply queue here
            .to(appConfig.getMqQueueNameIn())
            .log(">>> ${body}");
        // @formatter:on
    }

}
