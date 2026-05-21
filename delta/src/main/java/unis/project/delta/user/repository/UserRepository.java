package unis.project.delta.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.user.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUuid(String uuid);
}
