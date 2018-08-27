package remote.config.decopled;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.jms.JmsItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import remote.incrementer.KohlsJobIncrementer;
import remote.listener.ChunkCountListener;
import remote.listener.ChunkJobExecutionListener;
import remote.map.KohlsUserMapper;
import remote.pojo.KohlsUser;
import remote.setter.KohlsUserUpdatePreparedStmSetter;
import remote.utils.KohlsUserStatus;
import remote.writer.TransactionalCompositeItemWriter;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableBatchProcessing
@PropertySource("classpath:application.yml")
@Import(value = {DecopledCommonConfig.class})
public class DBReadJobConfig {

    @Value("${chunk.size}")
    private String chunkSize;

    @Value("${spring.batch.database.update.statement}")
    private String updateStatement;

    @Value("${spring.batch.database.read.statement}")
    private String readStatement;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TaskExecutor taskExecutor;

    @Bean
    public ItemReader<KohlsUser> kohlsJdbcUserReader(DataSource dataSource) {

        JdbcPagingItemReader<KohlsUser> databaseReader = new JdbcPagingItemReader<>();
        databaseReader.setName("kohlsUserItemReader");
        databaseReader.setDataSource(dataSource);
        databaseReader.setQueryProvider(mySqlPagingQueryProvider());
       // databaseReader.setSql(readStatement);
        databaseReader.setRowMapper(new KohlsUserMapper());

        return databaseReader;
    }

    @Bean
    public MySqlPagingQueryProvider mySqlPagingQueryProvider() {
        MySqlPagingQueryProvider mySqlPagingQueryProvider = new MySqlPagingQueryProvider();
        mySqlPagingQueryProvider.setSelectClause("ID, EVENT_ID, LOYALTY_ACCOUNT_EMAIL, REWARD_DOLLAR_AMOUNT, REWARD_VALUE, STATUS");
        mySqlPagingQueryProvider.setFromClause("LOYALTY_ACCOUNTS");
        mySqlPagingQueryProvider.setWhereClause("ID < 100001 AND STATUS = 'NEW'");
        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("ID", Order.ASCENDING);
        mySqlPagingQueryProvider.setSortKeys(sortKeys);
       return mySqlPagingQueryProvider;
    }

    @Bean
    public ItemProcessor<KohlsUser, KohlsUser> statusUpdateProcessor() {

        return user -> {
            user.setStatus(KohlsUserStatus.PROCESSING);
            return user;
        };

    }

    @Bean
    public ItemWriter<KohlsUser> queueWriter() {
        JmsItemWriter itemWriter = new JmsItemWriter();
        itemWriter.setJmsTemplate(jmsTemplate);
        return itemWriter;
    }

    public CompositeItemWriter<KohlsUser> updateStatusInDBandSend() {
        CompositeItemWriter writer = new CompositeItemWriter();
        writer.setDelegates(Arrays.asList(dbUpdater(dataSource), queueWriter()));
        return writer;
    }

    @Bean
    public ItemWriter<KohlsUser> dbUpdater(DataSource dataSource) {
        JdbcBatchItemWriter<KohlsUser> databaseItemWriter = new JdbcBatchItemWriter<>();
        databaseItemWriter.setDataSource(dataSource);
        databaseItemWriter.setSql(updateStatement);
        databaseItemWriter.setItemPreparedStatementSetter(new KohlsUserUpdatePreparedStmSetter());
        return databaseItemWriter;
    }

    @Bean(name = "readAndSend")
    public TaskletStep readAndSend() {

        return this.stepBuilderFactory.get("readAndSend")
                .transactionManager(transactionManager).<KohlsUser, KohlsUser>chunk(Integer.parseInt(chunkSize))
                .reader(kohlsJdbcUserReader(dataSource))
                .processor(statusUpdateProcessor())
                //.writer(updateStatusInDBandSend())
                .writer(transactionalCompositeItemWriter())
                .taskExecutor(taskExecutor)
                .build();

    }

    @Bean(name = "readFromDbAndSendToQueue")
    public Job readFromDbAndSendToQueue() {
        return this.jobBuilderFactory.get("readFromDbAndSendToQueue")
                .start(readAndSend())
                .incrementer(new KohlsJobIncrementer())
                .listener(new ChunkJobExecutionListener())
                .build();
    }

    @Bean
    public TransactionalCompositeItemWriter transactionalCompositeItemWriter() {
        TransactionalCompositeItemWriter transactionalCompositeItemWriter = new TransactionalCompositeItemWriter();
        transactionalCompositeItemWriter.setJdbcTemplate(jdbcTemplate);
        transactionalCompositeItemWriter.setTransactionManager(transactionManager);
        transactionalCompositeItemWriter.setDelegates(Arrays.asList(dbUpdater(dataSource), queueWriter()));
        return transactionalCompositeItemWriter;
    }

}
