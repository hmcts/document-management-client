package uk.gov.hmcts.reform.document;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import uk.gov.hmcts.reform.document.healthcheck.DocumentManagementHealthIndicator;

@AutoConfiguration
@ConditionalOnProperty(prefix = "document_management", name = "url")
@EnableFeignClients(basePackages = "uk.gov.hmcts.reform.document")
@Import(DocumentUploadClientApi.class)
public class DocumentManagementClientAutoConfiguration {

    @Bean
    public DocumentManagementHealthIndicator documentManagement(
        DocumentMetadataDownloadClientApi documentMetadataDownloadClientApi
    ) {
        return new DocumentManagementHealthIndicator(documentMetadataDownloadClientApi);
    }
}
