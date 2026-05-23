package unis.project.delta.dto.home;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class HomeCharacterResponse {

    private Long characterId;

    private String name;

    private Integer feelingXp;

    private Integer coin;
}