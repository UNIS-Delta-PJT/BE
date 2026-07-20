package unis.project.delta.domain.group.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import unis.project.delta.domain.group.dto.request.JoinGroupRequest;
import unis.project.delta.domain.group.dto.response.GroupCreateResponse;
import unis.project.delta.domain.group.dto.response.GroupListResponse;
import unis.project.delta.domain.group.dto.response.JoinGroupResponse;
import unis.project.delta.domain.group.service.GroupService;
import unis.project.delta.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
public class GroupController {

    private final GroupService groupService;

    /**
     * 신규 그룹 생성.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<GroupCreateResponse>> createGroup(
            @AuthenticationPrincipal Long userId) {

        GroupCreateResponse response = groupService.createGroup(userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "그룹 생성 성공"));
    }

    /**
     * 초대 코드로 그룹 가입.
     */
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<JoinGroupResponse>> joinGroup(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody JoinGroupRequest request) {

        JoinGroupResponse response = groupService.joinGroup(userId, request.getInviteCode());
        return ResponseEntity.ok(ApiResponse.success(response, "그룹 가입 성공"));
    }

    /**
     * 내가 속한 그룹 내 구성원 현황 조회.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<GroupListResponse>> getMyGroups(
            @AuthenticationPrincipal Long userId) {

        GroupListResponse response = groupService.getMyGroups(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "그룹 현황 조회 성공"));
    }

    /**
     * 그룹 탈퇴.
     */
    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveGroup(
            @AuthenticationPrincipal Long userId,
            @PathVariable("groupId") Long groupId) {

        groupService.leaveGroup(userId, groupId);
        return ResponseEntity.ok(ApiResponse.success("그룹 탈퇴 성공"));
    }
}
