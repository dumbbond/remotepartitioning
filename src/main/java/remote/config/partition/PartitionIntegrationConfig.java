//package remote.config.partition;
//
//import org.apache.activemq.ActiveMQConnectionFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.integration.channel.DirectChannel;
//import org.springframework.integration.channel.QueueChannel;
//import org.springframework.integration.core.MessagingTemplate;
//import org.springframework.integration.dsl.IntegrationFlow;
//import org.springframework.integration.dsl.IntegrationFlows;
//import org.springframework.integration.jms.dsl.Jms;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.PollableChannel;
//
//@Configuration
//public class IntegrationConfiguration {
//
//    @Value("${broker.url}")
//    private String brokerUrl;
//
//    @Bean
//    public ActiveMQConnectionFactory jmsConnectionFactory() {
//        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
//        connectionFactory.setBrokerURL(this.brokerUrl);
//        connectionFactory.setTrustAllPackages(true);
//        return connectionFactory;
//    }
//
//    @Bean
//    public MessagingTemplate messageTemplate() {
//
//        MessagingTemplate messagingTemplate = new MessagingTemplate(requests());
//        messagingTemplate.setReceiveTimeout(60000000l);
//
//        return messagingTemplate;
//    }
//
//    /*
//     * Configure outbound flow (requests going to workers)
//     */
//    @Bean
//    public MessageChannel requests() {
//        return new DirectChannel();
//    }
//
//
//    @Bean
//    @Profile("master")
//    public IntegrationFlow outboundFlow(ActiveMQConnectionFactory jmsConnectionFactory) {
//        return IntegrationFlows
//                .from(requests())
//                .handle(Jms.outboundAdapter(jmsConnectionFactory).destination("requests"))
//                .get();
//    }
//
//    /*
//     * Configure inbound flow (replies coming from workers)
//     */
//    @Bean
//    public PollableChannel replies() {
//        return new QueueChannel();
//    }
//
//    @Bean
//    @Profile("master")
//    public IntegrationFlow inboundFlow(ActiveMQConnectionFactory jmsConnectionFactory) {
//        return IntegrationFlows
//                .from(Jms.messageDrivenChannelAdapter(jmsConnectionFactory).destination("replies"))
//                .channel(replies())
//                .get();
//    }
//
//    /*
//     * Configure inbound flow (requests coming from the master)
//     */
//    @Bean
//    @Profile("slave")
//    public IntegrationFlow incomingRequests(ActiveMQConnectionFactory jmsConnectionFactory) {
//        return IntegrationFlows
//                .from(Jms.messageDrivenChannelAdapter(jmsConnectionFactory).destination("requests"))
//                .channel(requests())
//                .get();
//    }
//
//    /*
//     * Configure outbound flow (replies going to the master)
//     */
//    @Bean
//    @Profile("slave")
//    public IntegrationFlow outgoingReplies(ActiveMQConnectionFactory jmsConnectionFactory) {
//        return IntegrationFlows
//                .from(replies())
//                .handle(Jms.outboundAdapter(jmsConnectionFactory).destination("replies"))
//                .get();
//    }
//}
