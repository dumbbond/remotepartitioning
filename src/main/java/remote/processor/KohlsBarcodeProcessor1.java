package remote.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;
import org.springframework.web.client.RestTemplate;
import remote.pojo.KohlsBarcodeRequest;
import remote.pojo.KohlsBarcodeResponse;
import remote.pojo.KohlsUser;
import remote.utils.KohlsUserStatus;

@Configuration
@Import(value = {RestTemplate.class})
public class KohlsBarcodeProcessor1 implements ItemProcessor<KohlsUser, KohlsUser> {

    @Value("${barcode.service.url}")
    private String barcodeServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private KohlsBarcodeRequest barcodeRequest = new KohlsBarcodeRequest();

    private HttpHeaders headers = new HttpHeaders();

    private HttpEntity<KohlsBarcodeRequest> requestEntity;

    private ResponseEntity<KohlsBarcodeResponse> barcodeResponse;

    @Recover
    public KohlsUser fallback(KohlsUser user) {

        user.setBarcode("ERROR fallback called");
        user.setPin(0000);
        user.setStatus(KohlsUserStatus.FAILED);

        return user;
    }

    @CircuitBreaker(maxAttempts = 2, openTimeout = 5000)
    @Override
    public KohlsUser process(KohlsUser user) throws Exception {

        headers.setContentType(MediaType.APPLICATION_JSON);
        barcodeRequest.setEventId(user.getEventId());
        barcodeRequest.setKcAmount(user.getRewardValue());
        requestEntity = new HttpEntity(barcodeRequest, headers);
        //barcodeResponse = restTemplate.postForEntity(barcodeServiceUrl, requestEntity, KohlsBarcodeResponse.class);


        KohlsBarcodeResponse dummyResponse = kohlsBarcodeResponse(barcodeRequest);


        user.setStatus(KohlsUserStatus.COMPLETED);

        //user.setBarcode(barcodeResponse.getBody().getBarcode());
        //user.setPin(barcodeResponse.getBody().getPin());

        user.setBarcode(dummyResponse.getBarcode());
        user.setPin(dummyResponse.getPin());

        return user;
    }

    private KohlsBarcodeResponse kohlsBarcodeResponse(KohlsBarcodeRequest request) throws  Exception {
        int pin = (int)(Math.random()*9000)+1000;
        KohlsBarcodeResponse response = new KohlsBarcodeResponse();
        response.setPin(pin);
        response.setBarcode(request.getEventId() + pin + request.getEventId());
        //Thread.sleep(20);
        return response;
    }
}
