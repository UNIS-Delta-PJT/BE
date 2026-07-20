package unis.project.delta.domain.group.dto.response;

import java.util.List;

public record GroupListResponse(
        List<GroupDetailResponse> groups
) {
    public static GroupListResponse from(List<GroupDetailResponse> groups) {
        return new GroupListResponse(groups);
    }
}
