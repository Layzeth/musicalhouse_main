package ec.edu.espe.main.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.espe.main.exceptions.ClientException;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

@Log4j2
@Configuration
public class AppConfig {

    private static final ObjectMapper mapper = new ObjectMapper();


    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        var messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:lang/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600);
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        var localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }

    @Bean
    public RestTemplate restTemplate() {
        var client = new RestTemplate();
        client.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(@NotNull ClientHttpResponse response) throws IOException {
                var status = response.getStatusCode();
                return status.isError() || status.is5xxServerError() || status.is4xxClientError();
            }

            @Override
            public void handleError(@NotNull ClientHttpResponse response) throws IOException {
                var bodyStream = response.getBody();
                var body = mapper.readValue(bodyStream, new TypeReference<HashMap<String, Object>>() {
                });

                log.info("Error en la respuesta del servidor: {}", body);

                throw ClientException
                        .status(response.getStatusCode())
                        .headers(response.getHeaders())
                        .body(body);
            }
        });
        return client;
    }
}
