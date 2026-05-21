package unis.project.delta.category.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import unis.project.delta.budget.domain.CategoryBudget;
import unis.project.delta.global.domain.BaseEntity;
import unis.project.delta.user.domain.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = true)
    private String name;

    @Column(nullable = false)
    private boolean isDefault;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryBudget> categoryBudgetList = new ArrayList<>();

    @Builder
    public Category(User user, String name, boolean isDefault) {
        this.user = user;
        this.name = name;
        this.isDefault = isDefault;
    }
}
