package unis.project.delta.domain.item.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EquipItemRequest {

    @NotNull(message = "장착 여부는 필수입니다.")
    @JsonProperty("equip")
    private Boolean equip;
}
