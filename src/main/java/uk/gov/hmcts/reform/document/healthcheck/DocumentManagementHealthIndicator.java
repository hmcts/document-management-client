package uk.gov.hmcts.reform.document.healthcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.document.DocumentMetadataDownloadClientApi;

@Component
public class DocumentManagementHealthIndicator implements HealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentManagementHealthIndicator.class);

    private final DocumentMetadataDownloadClientApi documentMetadataDownloadClientApi;

    @Autowired
    public DocumentManagementHealthIndicator(
        final DocumentMetadataDownloadClientApi documentMetadataDownloadClientApi) {
        this.documentMetadataDownloadClientApi = documentMetadataDownloadClientApi;
    }

    @Override
    public Health health() {
        try {
            InternalHealth internalHealth = this.documentMetadataDownloadClientApi.health();
            return new Health.Builder(internalHealth.getStatus()).build();
        } catch (Exception ex) {
            LOGGER.error("Error on document management app healthcheck", ex);
            return Health.down(ex).build();
        }
    }
}
