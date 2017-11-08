package uk.gov.hmcts.document;

import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.feign.support.ResponseEntityDecoder;
import org.springframework.cloud.netflix.feign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.document.domain.MultiValueMapEncoder;
import uk.gov.hmcts.document.domain.UploadResponse;
import uk.gov.hmcts.document.utils.InMemoryMultipartFile;

import java.util.Collection;

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

    @RequestMapping(method = RequestMethod.GET, value = "/documents/{documentId}/binary")
    ResponseEntity<Resource> downloadBinary(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                                            @PathVariable("documentId") String documentId);

    class MultiPartSupportConfig {
        @Autowired
        ObjectFactory<HttpMessageConverters> messageConverters;

        @Bean
        @Primary
        @Scope("prototype")
        public Encoder feignFormEncoder() {
            return new MultiValueMapEncoder();
        }

        @Bean
        @Primary
        @Scope("prototype")
        public Decoder decoder() {
            Decoder decoder = (response, type) -> {
                if (type instanceof Class && MultipartFile.class.isAssignableFrom((Class) type)) {
                    Collection<String> contentTypes = response.headers().get("content-type");
                    String contentType = "application/octet-stream";
                    if (contentTypes.size() > 0) {
                        String[] temp = new String[contentTypes.size()];
                        contentTypes.toArray(temp);
                        contentType = temp[0];
                    }


                    byte[] bytes = StreamUtils.copyToByteArray(response.body().asInputStream());
                    InMemoryMultipartFile inMemoryMultipartFile = new InMemoryMultipartFile("files", "", contentType, bytes);
                    return inMemoryMultipartFile;
                }
                return new SpringDecoder(messageConverters).decode(response, type);
            };
            return new ResponseEntityDecoder(decoder);
        }

        @Bean
        public feign.Logger.Level multipartLoggerLevel() {
            return feign.Logger.Level.FULL;
        }
    }
}
