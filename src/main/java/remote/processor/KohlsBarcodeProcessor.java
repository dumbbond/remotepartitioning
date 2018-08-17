package remote.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;
import org.springframework.web.client.RestTemplate;
import remote.pojo.KohlsBarcodeRequest;
import remote.pojo.KohlsBarcodeResponse;
import remote.pojo.KohlsUser;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public class KohlsBarcodeProcessor implements ItemProcessor<KohlsUser, KohlsUser> {

    @Value("${barcode.service.url}")
    private String barcodeServiceUrl;

    @Value("${spring.batch.database.update.statement}")
    private String updateStatement;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private KohlsBarcodeRequest barcodeRequest = new KohlsBarcodeRequest();

    private HttpHeaders headers = new HttpHeaders();

    private HttpEntity<KohlsBarcodeRequest> requestEntity;

    private ResponseEntity<KohlsBarcodeResponse> barcodeResponse;

    @Recover
    public KohlsUser fallback(KohlsUser user) {

        user.setBarcode("ERROR");
        user.setLoyaltyAccountEmail("ERROR");

        user.setStatus("FAILED");

        updateStatus(user);

        return user;
    }

    @CircuitBreaker(maxAttempts = 2, openTimeout = 5000)
    @Override
    public KohlsUser process(KohlsUser user) throws Exception {

        headers.setContentType(MediaType.APPLICATION_JSON);
        barcodeRequest.setEventId(user.getEventId());
        barcodeRequest.setKcAmount(user.getRewardValue());
        requestEntity = new HttpEntity(barcodeRequest, headers);
        barcodeResponse = restTemplate.postForEntity(barcodeServiceUrl, requestEntity, KohlsBarcodeResponse.class);

        user.setStatus("COMPLETED");

        user.setBarcode(barcodeResponse.getBody().getBarcode());
        user.setPin(barcodeResponse.getBody().getPin());
        updateStatus(user);

        return user;
    }


    private void updateStatus(KohlsUser user) {

        jdbcTemplate.batchUpdate(updateStatement, new BatchPreparedStatementSetter() {

            public void setValues(PreparedStatement ps, int i) throws SQLException {

                ps.setString(1, user.getStatus());
                ps.setLong(2, user.getId());
            }

            public int getBatchSize() {
                return 1;
            }
        });

    }

}
