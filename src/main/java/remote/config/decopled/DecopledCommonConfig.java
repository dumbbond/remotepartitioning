package remote.config.decopled;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@Import(value = {ActiveMQConnectionFactory.class})
@PropertySource("classpath:application.yml")
public class DecopledCommonConfig {

    @Value("${broker.url}")
    private String brokerUrl;

    @Value("${kohls.users.queue}")
    private String usersQ;

    @Value("${spring.batch.databaseType}")
    private String databaseType;

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

    @Value("${spring.datasource.username}")
    private String dataSourceUser;

    @Value("${spring.datasource.password}")
    private String dataSourcePwd;

    @Value("${jms.receive.timeout}")
    private String jmsReceiveTimeout;

    @Value("${max.threads}")
    private String maxThreads;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        factory.setTrustAllPackages(true);
        return factory;
    }

    @Bean
    public ActiveMQQueue queue() {
        return new ActiveMQQueue(usersQ);
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(activeMQConnectionFactory());
        jmsTemplate.setDefaultDestinationName(usersQ);
        jmsTemplate.setDefaultDestination(queue());
        jmsTemplate.setReceiveTimeout(Long.valueOf(jmsReceiveTimeout));
        return jmsTemplate;
    }

    @Bean
    public DataSource dataSource() {

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(dataSourceUrl);
        dataSource.setUser(dataSourceUser);
        dataSource.setPassword(dataSourcePwd);
        return dataSource;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(Integer.valueOf(maxThreads));
        return taskExecutor;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource());
        return jdbcTemplate;
    }

}
