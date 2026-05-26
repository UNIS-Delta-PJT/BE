package unis.project.delta.character.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.character.domain.Character;
import unis.project.delta.user.domain.User;

import java.util.Optional;

public interface CharacterRepository extends JpaRepository<Character, Long> {

    Optional<Character> findByUser(User user);
}
