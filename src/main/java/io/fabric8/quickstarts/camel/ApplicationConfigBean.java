package io.fabric8.quickstarts.camel;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(prefix = "helloservice")
public class ApplicationConfigBean {

    private String message;
    private String time;
    private String mqHosts;
    private int mqPort;
    private String mqQueueManager;
    private String mqChannel;
    private String mqUsername;
    private String mqPassword;
    private String mqQueueType;
    private int mqConsumerCount;
    private String mqQueueNameIn;
    private int mqPoolMaxConnections;

    public ApplicationConfigBean() {
        // empty
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMqHosts() {
        return mqHosts;
    }

    public void setMqHosts(String mqHosts) {
        this.mqHosts = mqHosts;
    }

    public int getMqPort() {
        return mqPort;
    }

    public void setMqPort(int mqPort) {
        this.mqPort = mqPort;
    }

    public String getMqQueueManager() {
        return mqQueueManager;
    }

    public void setMqQueueManager(String mqQueueManager) {
        this.mqQueueManager = mqQueueManager;
    }

    public String getMqChannel() {
        return mqChannel;
    }

    public void setMqChannel(String mqChannel) {
        this.mqChannel = mqChannel;
    }

    public String getMqUsername() {
        return mqUsername;
    }

    public void setMqUsername(String mqUsername) {
        this.mqUsername = mqUsername;
    }

    public String getMqPassword() {
        return mqPassword;
    }

    public void setMqPassword(String mqPassword) {
        this.mqPassword = mqPassword;
    }

    public String getMqQueueType() {
        return mqQueueType;
    }

    public void setMqQueueType(String mqQueueType) {
        this.mqQueueType = mqQueueType;
    }

    public int getMqConsumerCount() {
        return mqConsumerCount;
    }

    public void setMqConsumerCount(int mqConsumerCount) {
        this.mqConsumerCount = mqConsumerCount;
    }

    public String getMqQueueNameIn() {
        return mqQueueNameIn;
    }

    public void setMqQueueNameIn(String mqQueueNameIn) {
        this.mqQueueNameIn = mqQueueNameIn;
    }

    public int getMqPoolMaxConnections() {
        return mqPoolMaxConnections;
    }

    public void setMqPoolMaxConnections(int mqPoolMaxConnections) {
        this.mqPoolMaxConnections = mqPoolMaxConnections;
    }

}
