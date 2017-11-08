package uk.gov.hmcts.document;

import feign.Headers;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.document.domain.UploadResponse;

import java.util.List;

@FeignClient(name = "document-management-gateway-api", url = "${document_management.api_gateway.url}")
public interface DocumentClientApi {

    @RequestMapping(method = RequestMethod.POST, value = "/documents", consumes = "multipart/form-data",
        headers = {"Content-Type=" + MediaType.MULTIPART_FORM_DATA_VALUE,
            "accept=application/vnd.uk.gov.hmcts.dm.document-collection.v1+hal+json;charset=utf-8",
            "classification=PRIVATE"
        })
    @Headers("Content-Type: multipart/form-data")
    UploadResponse upload(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                          @RequestPart("file") MultipartFile files);

    @RequestMapping(method = RequestMethod.GET, value = "/documents/{documentId}/binary")
    ResponseEntity<Resource> downloadBinary(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                                            @PathVariable("documentId") String documentId);
}
