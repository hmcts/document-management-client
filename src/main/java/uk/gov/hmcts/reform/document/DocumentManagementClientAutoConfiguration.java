package uk.gov.hmcts.reform.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.document.healthcheck.DocumentManagementHealthIndicator;

@AutoConfiguration
@ConditionalOnProperty(prefix = "document_management", name = "url")
@EnableFeignClients(basePackages = "uk.gov.hmcts.reform.document")
public class DocumentManagementClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DocumentUploadClientApi documentUploadClientApi(
        @Value("${document_management.url}") String dmUri,
        RestTemplate restTemplate,
        ObjectMapper objectMapper
    ) {
        return new DocumentUploadClientApi(dmUri, restTemplate, objectMapper);
    }

    @Bean
    public DocumentManagementHealthIndicator documentManagement(
        DocumentMetadataDownloadClientApi documentMetadataDownloadClientApi
    ) {
        return new DocumentManagementHealthIndicator(documentMetadataDownloadClientApi);
    }
}
