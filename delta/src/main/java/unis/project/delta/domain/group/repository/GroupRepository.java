package unis.project.delta.domain.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.domain.group.entity.Group;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findByInviteCode(String inviteCode);

    boolean existsByInviteCode(String inviteCode);
}
