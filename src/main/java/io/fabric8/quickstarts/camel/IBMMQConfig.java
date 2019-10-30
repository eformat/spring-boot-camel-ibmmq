package io.fabric8.quickstarts.camel;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import org.apache.camel.component.jms.JmsComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.JMSException;

@Configuration
public class IBMMQConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(IBMMQConfig.class);

    @Autowired
    ApplicationConfigBean appConfig;

    @Primary
    @Bean(name = "mqQueueConnectionFactory")
    public MQConnectionFactory mqQueueConnectionFactory() {
        System.setProperty("user.name", appConfig.getMqUsername());
        MQConnectionFactory mqQueueConnectionFactory = new MQConnectionFactory();
        try {
            mqQueueConnectionFactory.setConnectionNameList(appConfig.getMqHosts());
            mqQueueConnectionFactory.setClientReconnectOptions(WMQConstants.WMQ_CLIENT_RECONNECT_Q_MGR);
            mqQueueConnectionFactory.setClientReconnectTimeout(WMQConstants.WMQ_CLIENT_RECONNECT_TIMEOUT_DEFAULT);
            mqQueueConnectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
            mqQueueConnectionFactory.setChannel(appConfig.getMqChannel());
            mqQueueConnectionFactory.setPort(appConfig.getMqPort());
            mqQueueConnectionFactory.setQueueManager(appConfig.getMqQueueManager());

        } catch (JMSException jmsEx) {
            LOGGER.error("Problem setting up connections hosts in IBM MQ !!: {}", jmsEx);
            throw new IBMMQException("Problem setting up connections hosts in IBM MQ !!: %s", jmsEx);
        } catch (Exception e) {
            LOGGER.error("Problem configuring IBM MQ Client !!", e);
            throw new IBMMQException("Problem configuring IBM MQ Client !!", e);
        }

        return mqQueueConnectionFactory;
    }

    @Bean(name = "ibmMq")
    public JmsComponent jmsComponent() {
        JmsComponent jmsComponent = new JmsComponent();
        jmsComponent.setConnectionFactory(mqQueueConnectionFactory());
        jmsComponent.setUsername(appConfig.getMqUsername());
        jmsComponent.setPassword(appConfig.getMqPassword());
        jmsComponent.setConcurrentConsumers(appConfig.getMqConsumerCount());

        return jmsComponent;
    }

}
