package unis.project.delta.character.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.character.domain.Character;

public interface CharacterRepository extends JpaRepository<Character, Long> {
}
