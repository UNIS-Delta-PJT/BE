package unis.project.delta.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.category.domain.Category;
import unis.project.delta.user.domain.User;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByUserIsNullAndNameOrUserAndName(String name1, User user, String name2);
}
