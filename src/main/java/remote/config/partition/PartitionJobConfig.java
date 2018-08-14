//package remote.config.partition;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.explore.JobExplorer;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.core.partition.PartitionHandler;
//import org.springframework.batch.integration.partition.BeanFactoryStepLocator;
//import org.springframework.batch.integration.partition.MessageChannelPartitionHandler;
//import org.springframework.batch.integration.partition.StepExecutionRequestHandler;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
//import org.springframework.batch.item.database.JdbcBatchItemWriter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.integration.annotation.ServiceActivator;
//import org.springframework.integration.core.MessagingTemplate;
//import org.springframework.integration.scheduling.PollerMetadata;
//import org.springframework.scheduling.support.PeriodicTrigger;
//import remote.listener.ChunkJobExecutionListener;
//import remote.partition.ColumnRangePartitioner;
//import remote.pojo.KohlsUser;
//import remote.processor.KohlsBarcodeProcessor;
//import remote.reader.KohlsJdbcPagingUserReader;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class PartitionJobConfiguration implements ApplicationContextAware {
//
//    @Autowired
//    private JobBuilderFactory jobBuilderFactory;
//
//    @Autowired
//    private StepBuilderFactory stepBuilderFactory;
//
//    @Autowired
//    private DataSource dataSource;
//
//    @Autowired
//    private KohlsJdbcPagingUserReader kohlsJdbcPagingUserReader;
//
//    @Autowired
//    public JobExplorer jobExplorer;
//
//    private static final int GRID_SIZE = 10;
//
//    private ApplicationContext applicationContext;
//
//    @Bean
//    public PartitionHandler partitionHandler(MessagingTemplate messagingTemplate) throws Exception {
//
//        MessageChannelPartitionHandler partitionHandler = new MessageChannelPartitionHandler();
//
//        partitionHandler.setStepName("slaveStep");
//        partitionHandler.setGridSize(GRID_SIZE);
//        partitionHandler.setMessagingOperations(messagingTemplate);
//        partitionHandler.setPollInterval(5000l);
//        partitionHandler.setJobExplorer(this.jobExplorer);
//
//        partitionHandler.afterPropertiesSet();
//
//        return partitionHandler;
//    }
//
//    @Bean
//    @Profile("slave")
//    @ServiceActivator(inputChannel = "requests", outputChannel = "replies")
//    public StepExecutionRequestHandler stepExecutionRequestHandler() {
//        StepExecutionRequestHandler stepExecutionRequestHandler =
//                new StepExecutionRequestHandler();
//
//        BeanFactoryStepLocator stepLocator = new BeanFactoryStepLocator();
//        stepLocator.setBeanFactory(this.applicationContext);
//        stepExecutionRequestHandler.setStepLocator(stepLocator);
//        stepExecutionRequestHandler.setJobExplorer(this.jobExplorer);
//
//        return stepExecutionRequestHandler;
//    }
//
//    @Bean(name = PollerMetadata.DEFAULT_POLLER)
//    public PollerMetadata defaultPoller() {
//
//        PollerMetadata pollerMetadata = new PollerMetadata();
//        pollerMetadata.setTrigger(new PeriodicTrigger(10));
//        return pollerMetadata;
//    }
//
//    @Bean
//    public ColumnRangePartitioner partitioner() {
//        ColumnRangePartitioner columnRangePartitioner = new ColumnRangePartitioner();
//
//        columnRangePartitioner.setColumn("ID");
//        columnRangePartitioner.setDataSource(this.dataSource);
//        columnRangePartitioner.setTable("KOHLS_TEST");
//
//        return columnRangePartitioner;
//    }
//
//    @Bean
//    public KohlsBarcodeProcessor testProcessor() {
//        return new KohlsBarcodeProcessor();
//    }
//
//    @Bean
//    public ItemWriter<KohlsUser> customerItemWriter(DataSource dataSource) {
//
//        JdbcBatchItemWriter<KohlsUser> itemWriter = new JdbcBatchItemWriter<>();
//        itemWriter.setDataSource(dataSource);
//        itemWriter.setSql("INSERT INTO KOHLS.KOHLS_BARCODE VALUES (:id, :name, :barcode)");
//        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
//        itemWriter.afterPropertiesSet();
//        return  itemWriter;
//    }
//
//    @Bean(name = "remotePartitioningJob")
//    @Profile("master")
//    public Job remoteChunkingJob() throws Exception {
//
//        return this.jobBuilderFactory.get("remotePartitioningJob")
//                .incrementer(new RunIdIncrementer())
//                .start(masterStep())
//                .listener(new ChunkJobExecutionListener())
//                .build();
//    }
//
//    @Bean
//    public Step masterStep() throws Exception  {
//
//        return this.stepBuilderFactory.get("masterStep")
//                .partitioner(slaveStep().getName(), partitioner())
//                .step(slaveStep())
//                .partitionHandler(partitionHandler(null))
//                .build();
//    }
//
//    @Bean
//    public Step slaveStep() {
//
//        return stepBuilderFactory.get("slaveStep")
//                .<KohlsUser, KohlsUser>chunk(1000)
//                .reader(kohlsJdbcPagingUserReader.pagingItemReader(null, null))
//                .processor(testProcessor())
//                .writer(customerItemWriter(this.dataSource))
//                .build();
//    }
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) {
//        this.applicationContext = applicationContext;
//    }
//}
