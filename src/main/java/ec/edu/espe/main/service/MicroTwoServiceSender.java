package ec.edu.espe.main.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class MicroTwoServiceSender {

    private final String microserviceTwoUrl;
    private final RestTemplate httpCiente;

    public MicroTwoServiceSender(@Value("${microservice.two.url}") String microserviceTwoUrl, RestTemplate httpCiente) {
        this.microserviceTwoUrl = microserviceTwoUrl;
        this.httpCiente = httpCiente;
    }

    private <T> T post(String path, MultiValueMap<String, Object> body, HttpHeaders headers, Class<T> responseType) {
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        return httpCiente.postForEntity(microserviceTwoUrl + path, requestEntity, responseType).getBody();
    }

    public <T> T post(String path, MultiValueMap<String, Object> body, Class<T> responseType) {
        return post(path, body, new HttpHeaders(), responseType);
    }

    public void post(String path, MultiValueMap<String, Object> body) {
        post(path, body, new HttpHeaders(), Void.class);
    }

    public void post(String path, MultiValueMap<String, Object> body, HttpHeaders headers) {
        post(path, body, headers, Void.class);
    }

}
