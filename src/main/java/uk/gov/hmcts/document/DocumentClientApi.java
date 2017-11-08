package uk.gov.hmcts.document;

import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import uk.gov.hmcts.document.domain.MultiValueMapEncoder;
import uk.gov.hmcts.document.domain.UploadResponse;

@FeignClient(name = "document-management-gateway-api", url = "${document_management.api_gateway.url}",
    configuration = DocumentClientApi.MultiPartSupportConfig.class)
public interface DocumentClientApi {

    @RequestMapping(method = RequestMethod.POST, value = "/documents", consumes = "multipart/form-data",
        headers = {"Content-Type=" + MediaType.MULTIPART_FORM_DATA_VALUE,
            "accept=application/vnd.uk.gov.hmcts.dm.document-collection.v1+hal+json;charset=utf-8",
            "classification=PRIVATE"
        })
    UploadResponse upload(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                          @RequestPart MultiValueMap parameters);

    @RequestMapping(method = RequestMethod.GET, value = "{uri}")
    ResponseEntity<Resource> downloadBinary(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                                            @PathVariable("uri") String uri);

    class MultiPartSupportConfig {

        @Bean
        @Primary
        @Scope("prototype")
        public Encoder feignFormEncoder() {
            return new MultiValueMapEncoder();
        }

        @Bean
        public feign.Logger.Level multipartLoggerLevel() {
            return feign.Logger.Level.FULL;
        }
    }
}
