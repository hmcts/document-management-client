package uk.gov.hmcts.reform.document;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "document-management-document-delete-api", url = "${document_management.url}")
public interface DocumentDeleteApi {

    @RequestMapping(method = RequestMethod.DELETE, value = "{document_delete_uri}")
    void deleteDocument(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
        @RequestHeader("ServiceAuthorization") String serviceAuth,
        @RequestHeader("user-roles") String userRoles,
        @RequestHeader("user-id") String userId,
        @PathVariable("document_delete_uri") String documentDownloadUri,
        @RequestParam("permanent") boolean permanent
    );
}
