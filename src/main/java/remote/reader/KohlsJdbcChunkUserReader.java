package remote.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import remote.map.KohlsUserMapper;
import remote.pojo.KohlsUser;

import javax.sql.DataSource;

@Configuration
public class KohlsJdbcChunkUserReader {


    @Value("${spring.batch.database.read.statement}")
    private String readStatement;

    @Bean
    public ItemReader<KohlsUser> kohlsUserItemReader(DataSource dataSource) {

        JdbcCursorItemReader<KohlsUser> databaseReader = new JdbcCursorItemReader<>();
        databaseReader.setName("kohlsUserItemReader");
        databaseReader.setDataSource(dataSource);
        databaseReader.setSql(readStatement);
        databaseReader.setRowMapper(new KohlsUserMapper());

        return databaseReader;
    }

}
