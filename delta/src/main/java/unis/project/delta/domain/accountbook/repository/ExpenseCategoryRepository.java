package unis.project.delta.domain.accountbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.domain.accountbook.entity.ExpenseCategory;
import unis.project.delta.domain.user.entity.User;

import java.util.List;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {

    /** 기본 카테고리(user IS NULL) + 해당 사용자의 커스텀 카테고리 조회 */
    List<ExpenseCategory> findByUserIsNullOrUser(User user);

    boolean existsByNameAndUserIsNull(String name);

    boolean existsByNameAndUser(String name, User user);
}
