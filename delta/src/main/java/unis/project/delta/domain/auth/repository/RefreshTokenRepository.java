package unis.project.delta.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.domain.auth.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
