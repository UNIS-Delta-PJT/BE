package unis.project.delta.domain.shop.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "coin_packages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoinPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 기본 제공 코인 수 */
    @Column(nullable = false)
    private Integer coinAmount;

    /** 보너스 코인 수 */
    @Column(nullable = false)
    private Integer bonusCoin;

    /** 결제 금액 (원) */
    @Column(nullable = false)
    private Integer price;

    @Builder
    public CoinPackage(Integer coinAmount, Integer bonusCoin, Integer price) {
        this.coinAmount = coinAmount;
        this.bonusCoin = bonusCoin;
        this.price = price;
    }
}
