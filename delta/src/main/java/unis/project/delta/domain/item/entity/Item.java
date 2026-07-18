package unis.project.delta.domain.item.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 아이템 이름
    @Column(nullable = false)
    private String name;

    // 구매에 필요한 코인 가격
    @Column(nullable = false)
    private Integer price;

    // 아이템 종류
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType type;

    @Builder
    public Item(String name, Integer price, ItemType type) {
        this.name = name;
        this.price = price;
        this.type = type;
    }
}
