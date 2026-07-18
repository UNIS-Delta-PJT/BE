package unis.project.delta.domain.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.domain.item.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
