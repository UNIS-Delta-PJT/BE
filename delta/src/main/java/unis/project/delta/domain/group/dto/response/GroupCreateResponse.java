package unis.project.delta.domain.group.dto.response;

import unis.project.delta.domain.group.entity.Group;

public record GroupCreateResponse(
        Long groupId,
        String inviteCode
) {
    public static GroupCreateResponse from(Group group) {
        return new GroupCreateResponse(group.getId(), group.getInviteCode());
    }
}
