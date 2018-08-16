package remote.config.chunk;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.chunk.ChunkMessageChannelItemWriter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.messaging.PollableChannel;
import org.springframework.transaction.PlatformTransactionManager;
import remote.incrementer.KohlsJobIncrementer;
import remote.listener.ChunkCountListener;
import remote.listener.ChunkJobExecutionListener;
import remote.pojo.KohlsUser;
import remote.reader.KohlsJdbcChunkUserReader;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class ChunkMasterConfiguration {

    @Value("${broker.url}")
    private String brokerUrl;

    @Value("${chunk.size}")
    private int chunkSize;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private JobRegistry jobRegistry;

    @Autowired
    private JobLauncher launcher;

    @Autowired
    private KohlsJdbcChunkUserReader kohlsJdbcUserReader;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private DataSource dataSource;

    @Bean
    public ActiveMQConnectionFactory jmsConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(this.brokerUrl);
        connectionFactory.setTrustAllPackages(true);
        return connectionFactory;
    }

    /*
     * Configure outbound flow (requests going to workers)
     */
    @Bean
    public DirectChannel requests() {
        return new DirectChannel();
    }


    @Bean
    @Profile("master")
    public IntegrationFlow outboundFlow(ActiveMQConnectionFactory jmsConnectionFactory) {
        return IntegrationFlows
                .from(requests())
                .handle(Jms.outboundAdapter(jmsConnectionFactory).destination("requests"))
                .get();
    }


    /*
     * Configure inbound flow (replies coming from workers)
     */
    @Bean
    public PollableChannel replies() {
        return new QueueChannel();
    }

    @Bean
    @Profile("master")
    public IntegrationFlow inboundFlow(ActiveMQConnectionFactory jmsConnectionFactory) {
        return IntegrationFlows
                .from(Jms.messageDrivenChannelAdapter(jmsConnectionFactory).destination("replies"))
                .channel(replies())
                .get();
    }

    /*
     * Configure master step components
	 */
    @Bean
    public ItemWriter<KohlsUser> itemWriter() {
        MessagingTemplate messagingTemplate = new MessagingTemplate();
        messagingTemplate.setDefaultChannel(requests());
        ChunkMessageChannelItemWriter<KohlsUser> chunkMessageChannelItemWriter = new ChunkMessageChannelItemWriter<>();
        chunkMessageChannelItemWriter.setMessagingOperations(messagingTemplate);
        chunkMessageChannelItemWriter.setReplyChannel(replies());
        return chunkMessageChannelItemWriter;
    }


    @Bean
    public TaskletStep masterStep() {
        return this.stepBuilderFactory.get("masterStep")
                .transactionManager(transactionManager).<KohlsUser, KohlsUser>chunk(chunkSize)
                .reader(kohlsJdbcUserReader.kohlsUserItemReader(this.dataSource))
                .writer(itemWriter())
                .listener(new ChunkJobExecutionListener())
                .listener(new ChunkCountListener())
                .build();
    }

    @Bean(name = "remoteChunkingJob")
    @Profile("master")
    public Job remoteChunkingJob() {

        return this.jobBuilderFactory.get("remoteChunkingJob")
                .listener(new ChunkJobExecutionListener())
                .incrementer(new KohlsJobIncrementer())
                .start(masterStep())
                .build();
    }

}
