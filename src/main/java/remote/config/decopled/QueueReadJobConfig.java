package remote.config.decopled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.jms.JmsItemReader;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import remote.listener.ChunkCountListener;
import remote.pojo.KohlsUser;
import remote.processor.KohlsBarcodeProcessor1;
import remote.setter.KohlsBarcodePreparedStmSetter;
import remote.setter.KohlsUserUpdatePreparedStmSetter;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableBatchProcessing
@PropertySource("classpath:application.yml")
@Import(value = {DecopledCommonConfig.class, KohlsBarcodeProcessor1.class})
public class QueueReadJobConfig {

    @Value("${spring.batch.database.insert.statement}")
    private String insertStatement;

    @Value("${spring.batch.database.update.statement}")
    private String updateStatement;

    @Value("${chunk.size}")
    private String chunkSize;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private KohlsBarcodeProcessor1 kohlsBarcodeProcessor1;

    @Autowired
    private TaskExecutor taskExecutor;

    private static final Logger log = LoggerFactory.getLogger(ChunkCountListener.class);

    @Bean
    public JmsItemReader kohlsJmsUserReader() {

        JmsItemReader<KohlsUser> kohlsUserJmsItemReader = new JmsItemReader<>();
        kohlsUserJmsItemReader.setItemType(KohlsUser.class);
        kohlsUserJmsItemReader.setJmsTemplate(jmsTemplate);
        return kohlsUserJmsItemReader;

    }

    @Bean
    public ItemWriter<KohlsUser> dbUpdater(DataSource dataSource) {
        JdbcBatchItemWriter<KohlsUser> databaseItemWriter = new JdbcBatchItemWriter<>();
        databaseItemWriter.setDataSource(dataSource);
        databaseItemWriter.setSql(updateStatement);
        databaseItemWriter.setItemPreparedStatementSetter(new KohlsUserUpdatePreparedStmSetter());
        return databaseItemWriter;
    }

    @Bean
    public ItemWriter<KohlsUser> kohlsBarcodeWriter(DataSource dataSource) {

        JdbcBatchItemWriter<KohlsUser> itemWriter = new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql(insertStatement);
        itemWriter.setItemPreparedStatementSetter(new KohlsBarcodePreparedStmSetter());
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();
        return  itemWriter;
    }

    @Bean
    public CompositeItemWriter<KohlsUser> updateStatusInDBandWrite() {
        CompositeItemWriter writer = new CompositeItemWriter();
        writer.setDelegates(Arrays.asList(dbUpdater(dataSource), kohlsBarcodeWriter(dataSource)));
        return writer;
    }

    @Bean(name = "receiveProcessAndWrite")
    public TaskletStep receiveProcessAndWrite() {

        return this.stepBuilderFactory.get("receiveProcessAndWrite")
                .transactionManager(transactionManager).<KohlsUser, KohlsUser>chunk(Integer.parseInt(chunkSize))
                .reader(kohlsJmsUserReader())
                .processor(kohlsBarcodeProcessor1)
                .writer(updateStatusInDBandWrite())
                .taskExecutor(taskExecutor)
                .listener(new ChunkCountListener())
                .build();

    }

    @Bean(name = "receiveFromQueueProcessAndWrite")
    public Job readFromDbAndSendToQueue() {
        return this.jobBuilderFactory.get("receiveFromQueueProcessAndWrite")
                .start(receiveProcessAndWrite())
                .incrementer(new RunIdIncrementer())
                .build();
    }

}
