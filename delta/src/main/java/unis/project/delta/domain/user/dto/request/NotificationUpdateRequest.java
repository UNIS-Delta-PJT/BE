package unis.project.delta.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationUpdateRequest {

    @NotNull(message = "isPushEnabled는 boolean 타입으로 전달해주세요.")
    @JsonProperty("isPushEnabled")
    private Boolean isPushEnabled;

    @NotNull(message = "isNightPushDisabled는 boolean 타입으로 전달해주세요.")
    @JsonProperty("isNightPushDisabled")
    private Boolean isNightPushDisabled;
}
