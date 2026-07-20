package unis.project.delta.domain.mission.dto.response;

import java.time.LocalDate;

public record AttendanceCheckResponse(
        Integer continuousAttendance,
        String targetDate
) {
    public static AttendanceCheckResponse of(Integer continuousAttendance, LocalDate targetDate) {
        return new AttendanceCheckResponse(continuousAttendance, targetDate.toString());
    }
}
