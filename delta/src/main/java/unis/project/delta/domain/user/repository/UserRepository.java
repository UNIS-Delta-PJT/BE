package unis.project.delta.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByOauthId(String oauthId);
}
