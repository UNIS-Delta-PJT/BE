package unis.project.delta.domain.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.domain.mission.entity.DailyMission;
import unis.project.delta.domain.user.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyMissionRepository extends JpaRepository<DailyMission, Long> {

    Optional<DailyMission> findByUserAndTargetDate(User user, LocalDate targetDate);

    List<DailyMission> findByUserAndTargetDateBetween(User user, LocalDate startDate, LocalDate endDate);
}
