package unis.project.delta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.domain.CharacterProfile;
import unis.project.delta.domain.User;

import java.util.Optional;

public interface CharacterProfileRepository extends JpaRepository<CharacterProfile, Long> {

    Optional<CharacterProfile> findByUser(User user);
}