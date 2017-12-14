package uk.gov.hmcts.reform.document.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Document {

    public String classification;

    public long size;

    public String mimeType;

    public String originalDocumentName;

    public String createdBy;

    String lastModifiedBy;

    public Date modifiedOn;

    public Date createdOn;

    @JsonProperty("_links")
    public Links links;


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Links {

        public Link self;
        public Link binary;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Link {
        public String href;
    }

}
