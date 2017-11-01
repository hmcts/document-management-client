package uk.gov.hmcts.document.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadResponse {

    @JsonProperty("_embedded")
    public Embedded embedded;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Embedded {
        public List<Document> documents;
    }

}
