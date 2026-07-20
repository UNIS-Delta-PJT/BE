package unis.project.delta.domain.group.dto.response;

public record JoinGroupResponse(
        Long groupId
) {
    public static JoinGroupResponse from(Long groupId) {
        return new JoinGroupResponse(groupId);
    }
}
