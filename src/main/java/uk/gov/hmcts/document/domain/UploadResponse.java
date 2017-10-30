package uk.gov.hmcts.document.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadResponse {

    @JsonProperty("_embedded")
    Embedded embedded;

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Embedded {
        List<Document> documents;
    }

}
