package unis.project.delta.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unis.project.delta.character.domain.Character;
import unis.project.delta.user.domain.User;
import unis.project.delta.user.dto.response.UserResponse;
import unis.project.delta.user.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponse createUser() {
        User newUser = User.builder()
                .uuid(UUID.randomUUID().toString())
                .nickname("임시사용자")
                .build();

        Character newCharacter = Character.builder()
                .name("구름이") // 일단 기본 이름은 대충 지었어용
                .user(newUser)
                .build();

        // 유저와 캐릭터 연결
        newUser.assignCharacter(newCharacter);
        User savedUser = userRepository.save(newUser);

        return UserResponse.from(savedUser);
    }
}
