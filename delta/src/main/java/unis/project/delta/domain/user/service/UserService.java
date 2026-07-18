package unis.project.delta.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unis.project.delta.domain.item.entity.UserItem;
import unis.project.delta.domain.item.repository.UserItemRepository;
import unis.project.delta.domain.user.dto.request.CharacterUpdateRequest;
import unis.project.delta.domain.user.dto.request.NotificationUpdateRequest;
import unis.project.delta.domain.user.dto.response.UserResponse;
import unis.project.delta.domain.user.entity.BodyColor;
import unis.project.delta.domain.user.entity.EyeShape;
import unis.project.delta.domain.user.entity.User;
import unis.project.delta.domain.user.repository.UserRepository;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserItemRepository userItemRepository;

    /**
     * 사용자의 모든 정보를 조회한다.
     * 캐릭터, 알림 설정, 장착 아이템 정보를 포함한다.
     */
    @Transactional(readOnly = true)
    public UserResponse getMyInfo(Long userId) {
        User user = findByUserId(userId);

        List<UserItem> equippedItems = userItemRepository.findByUserAndIsEquippedTrue(user);

        return UserResponse.from(user, equippedItems);
    }

    /**
     * 캐릭터의 닉네임, 몸통 색상, 눈 모양을 설정한다.
     * 전달된 필드만 업데이트하며, null인 필드는 변경하지 않는다.
     */
    @Transactional
    public void updateCharacter(Long userId, CharacterUpdateRequest request) {
        User user = findByUserId(userId);

        if (request.getNickname() != null) {
            user.updateNickname(request.getNickname());
        }

        if (request.getBodyColor() != null) {
            BodyColor bodyColor = parseBodyColor(request.getBodyColor());
            user.updateBodyColor(bodyColor);
        }

        if (request.getEyeShape() != null) {
            EyeShape eyeShape = parseEyeShape(request.getEyeShape());
            user.updateEyeShape(eyeShape);
        }
    }

    /**
     * 전체 푸시 알림 및 야간 알림 방해금지 설정을 변경한다.
     */
    @Transactional
    public void updateNotification(Long userId, NotificationUpdateRequest request) {
        User user = findByUserId(userId);

        user.switchPush(request.getIsPushEnabled());
        user.switchNightPush(request.getIsNightPushDisabled());
    }

    // ── private helpers ──

    private User findByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private BodyColor parseBodyColor(String value) {
        try {
            return BodyColor.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private EyeShape parseEyeShape(String value) {
        try {
            return EyeShape.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }
}
