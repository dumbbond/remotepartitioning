package remote.reader;

import remote.map.KohlsUserMapper;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import remote.pojo.KohlsUser;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

    @Configuration
    public class KohlsJdbcPagingUserReader {

        @Autowired
        public DataSource dataSource;

        @Bean
        @StepScope
        public JdbcPagingItemReader<KohlsUser> pagingItemReader(@Value("#{stepExecutionContext['minValue']}")Long minValue, @Value("#{stepExecutionContext['maxValue']}")Long maxValue) {

            System.out.println("Thread " + Thread.currentThread().getId() + " reading from " + minValue + " to " + maxValue);

            JdbcPagingItemReader<KohlsUser> reader = new JdbcPagingItemReader<>();

            reader.setName("pagingItemReader");
            reader.setDataSource(dataSource);
            reader.setFetchSize(1000);
            reader.setRowMapper(new KohlsUserMapper());
            reader.setQueryProvider(queryProvider(minValue, maxValue));

            return reader;

        }

        public MySqlPagingQueryProvider queryProvider(Long minValue, Long maxValue) {

            MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
            queryProvider.setSelectClause("id, name, email, created_ts, updated_ts, status");
            queryProvider.setFromClause("from KOHLS.KOHLS_TEST");
            queryProvider.setWhereClause("where id >= " + minValue + " and id <= " + maxValue);

            Map<String, Order> sortKeys = new HashMap<>(1);
            sortKeys.put("id", Order.ASCENDING);

            queryProvider.setSortKeys(sortKeys);
            return queryProvider;

        }
}
