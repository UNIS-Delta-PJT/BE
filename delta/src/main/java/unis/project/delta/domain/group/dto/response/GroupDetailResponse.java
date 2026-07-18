package unis.project.delta.domain.group.dto.response;

import java.util.List;

public record GroupDetailResponse(
        Long groupId,
        String inviteCode,
        List<GroupMemberResponse> members
) {
    public static GroupDetailResponse of(Long groupId, String inviteCode,
                                         List<GroupMemberResponse> members) {
        return new GroupDetailResponse(groupId, inviteCode, members);
    }
}
