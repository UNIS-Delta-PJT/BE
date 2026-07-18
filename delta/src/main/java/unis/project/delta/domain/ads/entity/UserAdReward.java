package unis.project.delta.domain.ads.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import unis.project.delta.domain.user.entity.User;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "user_ad_rewards",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "reward_type", "target_date"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAdReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdRewardType rewardType;

    @Column(nullable = false)
    private LocalDate targetDate;

    @Builder
    public UserAdReward(User user, AdRewardType rewardType, LocalDate targetDate) {
        this.user = user;
        this.rewardType = rewardType;
        this.targetDate = targetDate;
    }
}
