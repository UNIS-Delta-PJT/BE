package unis.project.delta.domain.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.domain.item.entity.Item;
import unis.project.delta.domain.item.entity.ItemType;
import unis.project.delta.domain.item.entity.UserItem;
import unis.project.delta.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {

    List<UserItem> findByUserAndIsEquippedTrue(User user);

    List<UserItem> findByUser(User user);

    Optional<UserItem> findByUserAndItem(User user, Item item);

    boolean existsByUserAndItem(User user, Item item);

    /** 같은 종류(TOP, HAT 등)의 장착 중인 아이템 조회 — 착용 시 기존 해제용 */
    List<UserItem> findByUserAndIsEquippedTrueAndItem_Type(User user, ItemType type);
}
