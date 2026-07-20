package unis.project.delta.domain.group.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinGroupRequest {

    @NotBlank(message = "초대 코드는 필수입니다.")
    private String inviteCode;
}
