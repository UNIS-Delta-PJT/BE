package unis.project.delta.dto.home;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class HomeUserResponse {

    private Long userId;

    private String nickname;
}