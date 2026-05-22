package unis.project.delta.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import unis.project.delta.category.domain.Category;
import unis.project.delta.user.domain.User;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByUserIsNullAndNameOrUserAndName(String name1, User user, String name2);

    @Query("SELECT c FROM Category c WHERE c.user IS NULL OR c.user = :user")
    List<Category> findAllDefaultOrMyCategories(@Param("user") User user);
}
