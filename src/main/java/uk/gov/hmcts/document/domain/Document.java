package uk.gov.hmcts.document.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Document {

    String classification;

    long size;

    String mimeType;

    String originalDocumentName;

    String createdBy;

    String lastModifiedBy;

    Date modifiedOn;

    Date createdOn;

    @JsonProperty("_links")
    Links links;


    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Links {

        Link self;
        Link binary;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Link {
        String href;
    }

}
