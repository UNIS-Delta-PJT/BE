package unis.project.delta.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
