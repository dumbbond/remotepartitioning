package remote.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;
import org.springframework.web.client.RestTemplate;
import remote.pojo.KohlsBarcode;
import remote.pojo.KohlsUser;


public class KohlsBarcodeProcessor implements ItemProcessor<KohlsUser, KohlsUser> {

    @Value("${barcode.service.url}")
    private String barcodeServiceUrl;

    @Autowired
    private  RestTemplate restTemplate;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Recover
    public KohlsUser fallback(KohlsUser user) {

        user.setBarcode("ERROR");
        user.setEmail("ERROR");

        return user;
    }

    @CircuitBreaker(maxAttempts = 2, openTimeout = 5000)
    @Override
    public KohlsUser process(KohlsUser user) throws Exception {


        ResponseEntity<KohlsBarcode> responseEntity = this.restTemplate.exchange(
                barcodeServiceUrl,
                HttpMethod.GET,
                null,
                KohlsBarcode.class,
                user.getEmail()
        );

        user.setBarcode(responseEntity.getBody().getBarcode());
        return user;
    }

}
