package io.fabric8.quickstarts.camel;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import org.apache.camel.component.jms.JmsComponent;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.JmsPoolConnectionFactoryProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

@Configuration
public class IBMMQConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(IBMMQConfig.class);

    private JmsPoolConnectionFactoryProperties pool = new JmsPoolConnectionFactoryProperties();

    @Autowired
    ApplicationConfigBean appConfig;

    @Primary
    @Bean(name = "mqQueueConnectionFactory")
    public JmsPoolConnectionFactory mqQueueConnectionFactory() {
        System.setProperty("user.name", appConfig.getMqUsername());
        MQConnectionFactory mqQueueConnectionFactory = new MQConnectionFactory();

        pool.setMaxConnections(appConfig.getMqPoolMaxConnections());
        JmsPoolConnectionFactory pooledConnectionFactory = create(mqQueueConnectionFactory, pool);
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

        //return mqQueueConnectionFactory;
        return pooledConnectionFactory;
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

    private JmsPoolConnectionFactory create(ConnectionFactory connectionFactory, JmsPoolConnectionFactoryProperties poolProperties) {
        JmsPoolConnectionFactory pooledConnectionFactory = new JmsPoolConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(connectionFactory);

        pooledConnectionFactory.setBlockIfSessionPoolIsFull(poolProperties.isBlockIfFull());

        if (poolProperties.getBlockIfFullTimeout() != null) {
            pooledConnectionFactory.setBlockIfSessionPoolIsFullTimeout(poolProperties.getBlockIfFullTimeout().toMillis());
        }

        if (poolProperties.getIdleTimeout() != null) {
            pooledConnectionFactory.setConnectionIdleTimeout((int) poolProperties.getIdleTimeout().toMillis());
        }

        pooledConnectionFactory.setMaxConnections(poolProperties.getMaxConnections());
        pooledConnectionFactory.setMaxSessionsPerConnection(poolProperties.getMaxSessionsPerConnection());

        if (poolProperties.getTimeBetweenExpirationCheck() != null) {
            pooledConnectionFactory.setConnectionCheckInterval(poolProperties.getTimeBetweenExpirationCheck().toMillis());
        }

        pooledConnectionFactory.setUseAnonymousProducers(poolProperties.isUseAnonymousProducers());
        return pooledConnectionFactory;
    }

}
