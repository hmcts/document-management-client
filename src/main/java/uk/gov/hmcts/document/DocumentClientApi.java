package uk.gov.hmcts.document;

import feign.Headers;
import feign.Param;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.document.domain.UploadResponse;

import java.util.List;

@FeignClient(name = "document.management.gateway.api", url = "${document.management.gateway.api.url}")
public interface DocumentClientApi {

    @RequestMapping(method = RequestMethod.POST, value = "/documents")
    @Headers( {"Content-Type: multipart/form-data",
        "Authorization: {token}",
        "accept: application/vnd.uk.gov.hmcts.dm.document-collection.v1+hal+json;charset=UTF-8"
    })
    UploadResponse upload(@Param("token") String token,
                          @Param("files") List<MultipartFile> files);

}
