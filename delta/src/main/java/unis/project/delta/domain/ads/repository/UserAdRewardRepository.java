package unis.project.delta.domain.ads.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.domain.ads.entity.AdRewardType;
import unis.project.delta.domain.ads.entity.UserAdReward;
import unis.project.delta.domain.user.entity.User;

import java.time.LocalDate;

public interface UserAdRewardRepository extends JpaRepository<UserAdReward, Long> {

    boolean existsByUserAndRewardTypeAndTargetDate(User user, AdRewardType rewardType,
                                                   LocalDate targetDate);
}
