package remote.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import remote.map.KohlsUserMapper;
import remote.pojo.KohlsUser;

import javax.sql.DataSource;

@Configuration
public class KohlsJdbcChunkUserReader {

    @Bean
    public ItemReader<KohlsUser> kohlsUserItemReader(DataSource dataSource) {

        JdbcCursorItemReader<KohlsUser> databaseReader = new JdbcCursorItemReader<>();
        databaseReader.setName("kohlsUserItemReader");
        databaseReader.setDataSource(dataSource);
        databaseReader.setSql("Select id, name, email, created_ts, updated_ts, status from KOHLS.KOHLS_TEST where ID < 100001");
        databaseReader.setRowMapper(new KohlsUserMapper());

        return databaseReader;
    }

}
