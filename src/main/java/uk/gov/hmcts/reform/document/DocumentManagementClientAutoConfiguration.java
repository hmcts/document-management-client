package uk.gov.hmcts.reform.document;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.document.healthcheck.DocumentManagementHealthIndicator;

@Configuration
@ConditionalOnProperty(prefix = "document_management", name = "api_gateway.url")
@EnableFeignClients(basePackages = "uk.gov.hmcts.reform.document")
public class DocumentManagementClientAutoConfiguration {

    @Bean
    public DocumentManagementHealthIndicator documentManagement(
        DocumentMetadataDownloadClientApi documentMetadataDownloadClientApi
    ) {
        return new DocumentManagementHealthIndicator(documentMetadataDownloadClientApi);
    }
}
