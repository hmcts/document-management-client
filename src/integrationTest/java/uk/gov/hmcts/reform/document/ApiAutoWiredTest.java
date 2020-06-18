package uk.gov.hmcts.reform.document;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.document.config.HttpConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = {
        DocumentManagementClientAutoConfiguration.class,
        HttpConfiguration.class
    },
    properties = {
        "document_management.url=localhost"
    }
)
class ApiAutoWiredTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void apis_are_auto_wired() {
        assertThat(context.getBeanNamesForType(DocumentDeleteApi.class)).hasSize(1);
        assertThat(context.getBeanNamesForType(DocumentDownloadClientApi.class)).hasSize(1);
        assertThat(context.getBeanNamesForType(DocumentMetadataDownloadClientApi.class)).hasSize(1);
    }
}
