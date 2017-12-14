package uk.gov.hmcts.reform.document.healthcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.document.DocumentDownloadClientApi;

@Component
public class DocumentManagementHealthIndicator implements HealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentManagementHealthIndicator.class);

    private final DocumentDownloadClientApi documentDownloadClientApi;

    @Autowired
    public DocumentManagementHealthIndicator(final DocumentDownloadClientApi documentDownloadClientApi) {
        this.documentDownloadClientApi = documentDownloadClientApi;
    }

    @Override
    public Health health() {
        try {
            InternalHealth internalHealth = this.documentDownloadClientApi.health();
            return new Health.Builder(internalHealth.getStatus(), internalHealth.getDetails())
                .build();
        } catch (Exception ex) {
            LOGGER.error("Error on core case data store app healthcheck", ex);
            return Health.down(ex)
                .build();
        }
    }
}
