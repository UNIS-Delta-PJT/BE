package unis.project.delta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUuid(String uuid);
}