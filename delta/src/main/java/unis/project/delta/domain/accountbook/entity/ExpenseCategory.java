package unis.project.delta.domain.accountbook.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import unis.project.delta.domain.user.entity.User;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;

@Entity
@Getter
@Table(name = "expense_categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpenseCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 지출 카테고리명
    @Column(nullable = false)
    private String name;

    // 기본 제공 카테고리 여부(True면 고정, False면 유저 커스텀)
    @Column(nullable = false)
    private Boolean isDefault;

    @Builder
    public ExpenseCategory(User user, String name, Boolean isDefault) {
        this.user = user;
        this.name = name;
        this.isDefault = isDefault;
    }

    public void updateName(String newName) {
        // 기본 제공 카테고리는 이름 수정 불가하게 막기
        if (this.isDefault) {
            throw new CustomException(ErrorCode.DEFAULT_CATEGORY_NOT_MODIFIABLE);
        }
        this.name = newName;
    }
}
