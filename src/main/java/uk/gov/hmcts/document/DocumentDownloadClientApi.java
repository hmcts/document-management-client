package uk.gov.hmcts.document;

    import org.springframework.cloud.netflix.feign.FeignClient;
    import org.springframework.core.io.Resource;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.RequestHeader;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "document-management-download-gateway-api", url = "${document_management.api_gateway.url}")
public interface DocumentDownloadClientApi {

    @RequestMapping(method = RequestMethod.GET, value = "{document_download_uri}")
    ResponseEntity<Resource> downloadBinary(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                                            @PathVariable("document_download_uri") String documentDownloadUri);

}