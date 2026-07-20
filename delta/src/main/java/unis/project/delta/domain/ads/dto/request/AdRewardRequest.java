package unis.project.delta.domain.ads.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdRewardRequest {

    @NotNull(message = "보상 유형은 필수입니다.")
    private String rewardType;

    @NotBlank(message = "광고 ID는 필수입니다.")
    private String adId;
}
