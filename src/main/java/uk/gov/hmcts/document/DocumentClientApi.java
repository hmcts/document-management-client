package uk.gov.hmcts.document;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.document.domain.UploadResponse;

@FeignClient(name = "document-management-gateway-api", url = "${document.management.gateway.api.url}")
public interface DocumentClientApi {

    @RequestMapping(method = RequestMethod.POST, value = "/documents",
        headers = {"Content-Type: multipart/form-data",
            "accept: application/vnd.uk.gov.hmcts.dm.document-collection.v1+hal+json;charset=UTF-8"
        })
    UploadResponse upload(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                          @RequestParam("files") MultipartFile[] files,
                          @RequestParam("classification") String classification);

    @RequestMapping(method = RequestMethod.GET, value = "/documents/{documentId}/binary")
    ResponseEntity<Resource> downloadBinary(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                                            @PathVariable("documentId") String documentId);
}
