package unis.project.delta.domain.user.dto.response;

import unis.project.delta.domain.user.entity.User;

public record NotificationResponse(
        boolean isPushEnabled,
        boolean isNightPushDisabled,
        String fcmToken
) {
    public static NotificationResponse from(User user) {
        return new NotificationResponse(
                user.getIsPushEnabled(),
                user.getIsNightPushDisabled(),
                user.getFcmToken()
        );
    }
}
