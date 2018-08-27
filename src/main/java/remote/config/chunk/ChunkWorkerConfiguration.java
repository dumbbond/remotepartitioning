//package remote.config.chunk;
//
//import org.apache.activemq.ActiveMQConnectionFactory;
//import org.springframework.batch.core.step.item.ChunkProcessor;
//import org.springframework.batch.core.step.item.SimpleChunkProcessor;
//import org.springframework.batch.integration.chunk.ChunkProcessorChunkHandler;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
//import org.springframework.batch.item.database.JdbcBatchItemWriter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.integration.annotation.ServiceActivator;
//import org.springframework.integration.channel.DirectChannel;
//import org.springframework.integration.channel.QueueChannel;
//import org.springframework.integration.dsl.IntegrationFlow;
//import org.springframework.integration.dsl.IntegrationFlows;
//import org.springframework.integration.jms.dsl.Jms;
//import org.springframework.integration.scheduling.PollerMetadata;
//import org.springframework.messaging.PollableChannel;
//import org.springframework.scheduling.support.PeriodicTrigger;
//import remote.pojo.KohlsUser;
//import remote.processor.KohlsBarcodeProcessor;
//import remote.setter.KohlsBarcodePreparedStmSetter;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class ChunkWorkerConfiguration {
//
//    @Value("${broker.url}")
//    private String brokerUrl;
//
//    @Value("${spring.batch.database.insert.statement}")
//    private String insertStatement;
//
//    @Autowired
//    private DataSource dataSource;
//
//    @Bean
//    public ActiveMQConnectionFactory jmsConnectionFactory() {
//        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
//        connectionFactory.setBrokerURL(this.brokerUrl);
//        connectionFactory.setTrustAllPackages(true);
//        return connectionFactory;
//    }
//
//    /*
//     * Configure inbound flow (requests coming from the master)
//     */
//
//    @Bean
//    public DirectChannel requests() {
//        return new DirectChannel();
//    }
//
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
//
//    @Bean
//    public PollableChannel replies() {
//        return new QueueChannel ();
//    }
//
//    @Bean
//    @Profile("slave")
//    public IntegrationFlow outgoingReplies(ActiveMQConnectionFactory jmsConnectionFactory) {
//        return IntegrationFlows
//                .from(replies())
//                .handle(Jms.outboundAdapter(jmsConnectionFactory).destination("replies"))
//                .get();
//    }
//
//    /*
//     * Configure worker components
//     */
//
//    @Bean
//    public ItemWriter<KohlsUser> customerItemWriter(DataSource dataSource) {
//
//        JdbcBatchItemWriter<KohlsUser> itemWriter = new JdbcBatchItemWriter<>();
//        itemWriter.setDataSource(dataSource);
//        itemWriter.setSql(insertStatement);
//        itemWriter.setItemPreparedStatementSetter(new KohlsBarcodePreparedStmSetter());
//        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
//        itemWriter.afterPropertiesSet();
//        return  itemWriter;
//    }
//
//
//    @Bean
//    @Profile("slave")
//    @ServiceActivator(inputChannel = "requests", outputChannel = "replies")
//    public ChunkProcessorChunkHandler<KohlsUser> chunkProcessorChunkHandler() {
//        ChunkProcessor<KohlsUser> chunkProcessor = new SimpleChunkProcessor<>(kohlsBarcodeProcessor(), customerItemWriter(this.dataSource));
//        ChunkProcessorChunkHandler<KohlsUser> chunkProcessorChunkHandler = new ChunkProcessorChunkHandler<>();
//        chunkProcessorChunkHandler.setChunkProcessor(chunkProcessor);
//        return chunkProcessorChunkHandler;
//    }
//
//
//    @Bean(name = PollerMetadata.DEFAULT_POLLER)
//    public PollerMetadata defaultPoller() {
//
//        PollerMetadata pollerMetadata = new PollerMetadata();
//
//        // wait 10 ms between each poll
//        pollerMetadata.setTrigger(new PeriodicTrigger(10));
//        return pollerMetadata;
//    }
//
//    @Bean
//    public KohlsBarcodeProcessor kohlsBarcodeProcessor() {
//        return new KohlsBarcodeProcessor();
//    }
//
//}