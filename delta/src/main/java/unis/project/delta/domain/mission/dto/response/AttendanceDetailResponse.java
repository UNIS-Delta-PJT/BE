package unis.project.delta.domain.mission.dto.response;

import java.time.LocalDate;

public record AttendanceDetailResponse(
        String date,
        boolean isAttended
) {
    public static AttendanceDetailResponse of(LocalDate date, boolean isAttended) {
        return new AttendanceDetailResponse(date.toString(), isAttended);
    }
}
