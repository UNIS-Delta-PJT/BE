package unis.project.delta.domain.group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unis.project.delta.domain.group.dto.response.*;
import unis.project.delta.domain.group.entity.Group;
import unis.project.delta.domain.group.entity.GroupMember;
import unis.project.delta.domain.group.repository.GroupMemberRepository;
import unis.project.delta.domain.group.repository.GroupRepository;
import unis.project.delta.domain.item.entity.UserItem;
import unis.project.delta.domain.item.repository.UserItemRepository;
import unis.project.delta.domain.user.entity.User;
import unis.project.delta.domain.user.repository.UserRepository;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupService {

    private static final int MAX_GROUP_COUNT = 4;

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final UserItemRepository userItemRepository;

    /**
     * 새 그룹을 생성하고 생성자를 멤버로 추가한다.
     */
    @Transactional
    public GroupCreateResponse createGroup(Long userId) {
        User user = findByUserId(userId);
        validateGroupLimit(user);

        String inviteCode = generateUniqueInviteCode();

        Group group = Group.builder()
                .inviteCode(inviteCode)
                .build();
        groupRepository.save(group);

        GroupMember member = GroupMember.builder()
                .group(group)
                .user(user)
                .build();
        groupMemberRepository.save(member);

        return GroupCreateResponse.from(group);
    }

    /**
     * 초대 코드로 그룹에 가입한다.
     */
    @Transactional
    public JoinGroupResponse joinGroup(Long userId, String inviteCode) {
        User user = findByUserId(userId);

        Group group = groupRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));

        if (groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new CustomException(ErrorCode.ALREADY_JOINED);
        }

        validateGroupLimit(user);

        GroupMember member = GroupMember.builder()
                .group(group)
                .user(user)
                .build();
        groupMemberRepository.save(member);

        return JoinGroupResponse.from(group.getId());
    }

    /**
     * 내가 속한 그룹들과 각 구성원의 캐릭터·맵 위치·장착 아이템을 조회한다.
     */
    @Transactional(readOnly = true)
    public GroupListResponse getMyGroups(Long userId) {
        User user = findByUserId(userId);

        List<GroupMember> myMemberships = groupMemberRepository.findByUser(user);

        List<GroupDetailResponse> groupDetails = myMemberships.stream()
                .map(membership -> {
                    Group group = membership.getGroup();
                    List<GroupMember> allMembers = groupMemberRepository.findByGroup(group);

                    List<GroupMemberResponse> memberResponses = allMembers.stream()
                            .map(gm -> {
                                User memberUser = gm.getUser();
                                List<UserItem> equipped = userItemRepository
                                        .findByUserAndIsEquippedTrue(memberUser);
                                return GroupMemberResponse.from(memberUser, equipped);
                            })
                            .toList();

                    return GroupDetailResponse.of(
                            group.getId(), group.getInviteCode(), memberResponses);
                })
                .toList();

        return GroupListResponse.from(groupDetails);
    }

    /**
     * 해당 그룹에서 탈퇴한다.
     */
    @Transactional
    public void leaveGroup(Long userId, Long groupId) {
        User user = findByUserId(userId);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));

        GroupMember member = groupMemberRepository.findByGroupAndUser(group, user)
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_MEMBER_NOT_FOUND));

        groupMemberRepository.delete(member);
    }

    // ── private helpers ──

    private User findByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateGroupLimit(User user) {
        long count = groupMemberRepository.countByUser(user);
        if (count >= MAX_GROUP_COUNT) {
            throw new CustomException(ErrorCode.GROUP_LIMIT_EXCEEDED);
        }
    }

    private String generateUniqueInviteCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (groupRepository.existsByInviteCode(code));
        return code;
    }
}
