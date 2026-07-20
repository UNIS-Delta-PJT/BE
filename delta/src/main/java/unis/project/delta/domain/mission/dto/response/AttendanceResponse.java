package unis.project.delta.domain.mission.dto.response;

import java.util.List;

public record AttendanceResponse(
        Integer continuousAttendance,
        List<AttendanceDetailResponse> attendances
) {
    public static AttendanceResponse of(Integer continuousAttendance,
                                        List<AttendanceDetailResponse> attendances) {
        return new AttendanceResponse(continuousAttendance, attendances);
    }
}
