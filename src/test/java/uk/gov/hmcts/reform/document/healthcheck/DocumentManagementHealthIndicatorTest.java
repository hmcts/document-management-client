package uk.gov.hmcts.reform.document.healthcheck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import uk.gov.hmcts.reform.document.DocumentMetadataDownloadClientApi;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
class DocumentManagementHealthIndicatorTest {

    @Mock
    private DocumentMetadataDownloadClientApi metadataApi;

    private DocumentManagementHealthIndicator healthIndicator;

    @BeforeEach
    void setUp() {
        healthIndicator = new DocumentManagementHealthIndicator(metadataApi);
    }

    private static InternalHealth[] healthStatuses() {
        return Stream.of(
            Status.DOWN,
            Status.OUT_OF_SERVICE,
            Status.UNKNOWN,
            Status.UP
        ).map(status -> new InternalHealth(status.getCode()))
            .toArray(InternalHealth[]::new);
    }

    @ParameterizedTest
    @MethodSource("healthStatuses")
    void should_return_health_status_when_api_returns_internal_health_object_accordingly(InternalHealth health) {
        // given
        given(metadataApi.health()).willReturn(health);

        // when
        Health actualHealth = healthIndicator.health();

        // then
        assertThat(actualHealth)
            .extracting(Health::getStatus)
            .isEqualTo(health.getStatus());
    }

    @Test
    void should_return_status_down_with_exception_details_when_api_throws_an_error() {
        // given
        String message = "oh no";
        willThrow(new RuntimeException(message)).given(metadataApi).health();

        // when
        Health health = healthIndicator.health();

        // then
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails())
            .containsKey("error")
            .containsValue(RuntimeException.class.getName() + ": " + message);
    }
}
