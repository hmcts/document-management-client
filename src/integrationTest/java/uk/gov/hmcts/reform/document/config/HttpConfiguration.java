package uk.gov.hmcts.reform.document.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpConfiguration {

    @Bean
    public RestTemplate dummyRestTemplate() {
        return new RestTemplate();
    }
}
