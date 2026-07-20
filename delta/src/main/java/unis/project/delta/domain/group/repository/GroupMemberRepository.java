package unis.project.delta.domain.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.domain.group.entity.Group;
import unis.project.delta.domain.group.entity.GroupMember;
import unis.project.delta.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    List<GroupMember> findByUser(User user);

    List<GroupMember> findByGroup(Group group);

    Optional<GroupMember> findByGroupAndUser(Group group, User user);

    boolean existsByGroupAndUser(Group group, User user);

    long countByUser(User user);
}
