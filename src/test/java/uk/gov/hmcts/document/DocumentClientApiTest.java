package uk.gov.hmcts.document;

import feign.Feign;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.document.domain.UploadResponse;

@RunWith(SpringRunner.class)
@Ignore
public class DocumentClientApiTest {

    @Test
    public void clientConnects() {
        UploadResponse response = Feign.builder()
            .target(DocumentClientApi.class,
                "https://api-gateway.test.evidence.reform.hmcts.net/documents")
            .upload("token here", null);

    }

}
