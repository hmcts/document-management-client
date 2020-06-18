package uk.gov.hmcts.reform.document;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.common.io.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.reform.document.config.HttpConfiguration;
import uk.gov.hmcts.reform.document.domain.Classification;
import uk.gov.hmcts.reform.document.domain.UploadResponse;
import uk.gov.hmcts.reform.document.utils.InMemoryMultipartFile;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@AutoConfigureWireMock(port = 0)
@EnableAutoConfiguration
@SpringBootTest(
    classes = HttpConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class DocumentUploadClientApiTest {

    @Autowired
    private DocumentUploadClientApi uploadApi;

    @BeforeEach
    void before() {
        reset();
    }

    @Test
    void should_handle_invalid_json_response_with_illegal_state_exception_throw() {
        // given
        MultipartFile file = new InMemoryMultipartFile(
            "files",
            "display name",
            MediaType.APPLICATION_PDF_VALUE,
            new byte[] {}
        );
        givenThat(post("/documents").willReturn(aResponse().withBody("{not valid json}")));

        assertThatCode(() ->
            // when
            uploadApi.upload("auth", "service auth", "user id", singletonList(file))
        // then
        ).isInstanceOf(IllegalStateException.class)
            .hasCauseInstanceOf(JsonParseException.class)
            .hasMessageContaining("not valid json");
    }

    @Test
    void should_upload_response_when_upload_is_complete() throws IOException {
        // given
        MultipartFile file = new InMemoryMultipartFile(
            "files",
            "display name",
            MediaType.APPLICATION_PDF_VALUE,
            new byte[] {}
        );
        givenThat(
            post("/documents")
                .willReturn(
                    aResponse()
                        .withBody(Resources.toByteArray(Resources.getResource("upload-response-sample.json")))
                )
        );

        // when
        UploadResponse response = uploadApi.upload(
            "auth",
            "service auth",
            "user id",
            singletonList("some role"),
            Classification.PRIVATE,
            singletonList(file)
        );

        // then
        assertThat(response.getEmbedded().getDocuments())
            .satisfies(documents -> assertThat(documents)
                .hasSize(1)
                .first()
                .satisfies(document -> {
                    // see json file in resources
                    assertThat(document.classification).isEqualToIgnoringCase(Classification.PRIVATE.name());
                    assertThat(document.size).isEqualTo(0);
                    assertThat(document.mimeType).isEqualTo("application/octet-stream");
                    assertThat(document.originalDocumentName).isEqualTo("original-name");
                    assertThat(document.createdBy).isEqualTo("integration-test");
                    assertThat(document.links.binary.href).isEqualTo("binary_value");
                    assertThat(document.links.self.href).isEqualTo("self_value");
                })
            );
    }
}
