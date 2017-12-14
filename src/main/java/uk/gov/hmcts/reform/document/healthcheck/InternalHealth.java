package uk.gov.hmcts.reform.document.healthcheck;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.actuate.health.Status;

import java.util.Map;

public class InternalHealth {
    private final Status status;
    private final Map<String, Object> details;

    @JsonCreator
    public InternalHealth(
        @JsonProperty("status") Status status,
        @JsonProperty("details") Map<String, Object> details
    ) {
        this.status = status;
        this.details = details;
    }

    public Status getStatus() {
        return status;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
