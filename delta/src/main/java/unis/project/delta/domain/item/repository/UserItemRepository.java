package unis.project.delta.domain.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.domain.item.entity.UserItem;
import unis.project.delta.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {

    List<UserItem> findByUserAndIsEquippedTrue(User user);

    List<UserItem> findByUser(User user);

    Optional<UserItem> findByUserAndItemId(User user, Long itemId);

    boolean existsByUserAndItemId(User user, Long itemId);
}
