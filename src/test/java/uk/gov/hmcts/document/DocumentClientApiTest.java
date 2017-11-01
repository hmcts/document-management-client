package uk.gov.hmcts.document;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.document.domain.UploadResponse;
import uk.gov.hmcts.document.utils.FeignSpringFormEncoder;
import uk.gov.hmcts.document.utils.InMemoryMultipartFile;
import uk.gov.hmcts.document.utils.ResourceReader;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

public class DocumentClientApiTest {
    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(8083);

    @Test
    public void clientConnects() throws IOException {
        stubFor(post(urlEqualTo("/documents"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(new ResourceReader().read("/fileuploadresponse.json"))));


        final MultipartFile file = new InMemoryMultipartFile("filename.tmp", new byte[]{65, 66, 67, 68});

        final MultipartFile file2
                = new InMemoryMultipartFile("filename2.tmp", new byte[]{69, 70, 71, 72, 73, 68});

        final UploadResponse response = Feign.builder()
                .encoder(new FeignSpringFormEncoder())
                .decoder(new JacksonDecoder())
                .target(DocumentClientApi.class,
                        "http://localhost:8083")
                .upload("token here", new MultipartFile[]{file, file2});

        assertThat(response.embedded.documents.get(0).links.self.href)
                .isEqualTo("http://localhost:8085/documents/ee1636a5-b5c0-4376-bd68-522d1b68658f");

    }

}
