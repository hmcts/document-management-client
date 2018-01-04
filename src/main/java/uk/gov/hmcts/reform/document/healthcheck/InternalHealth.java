package uk.gov.hmcts.reform.document.healthcheck;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.boot.actuate.health.Status;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalHealth {
    private final Status status;

    @JsonCreator
    public InternalHealth(
        String status
    ) {
        this.status = new Status(status);
    }

    public Status getStatus() {
        return status;
    }
}
