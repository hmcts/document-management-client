package uk.gov.hmcts.document;

import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.gov.hmcts.document.domain.Document;

@FeignClient(name = "document-management-download-metadata-gateway-api", url = "${document_management.api_gateway.url}",
    configuration = DocumentDownloadMetadataApi.DownloadConfiguration.class)
public interface DocumentDownloadMetadataApi {

    @RequestMapping(method = RequestMethod.GET, value = "{document_metadata_uri}")
    Document getDocumentMetadata(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                                 @PathVariable("document_metadata_uri") String documentMetadataUri);

    class DownloadConfiguration {
        @Bean
        @Primary
        @Scope("prototype")
        Decoder feignDecoder() {
            return new JacksonDecoder();
        }
    }

}
