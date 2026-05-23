package unis.project.delta.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import unis.project.delta.global.exception.dto.ApiResponse;
import unis.project.delta.user.dto.response.UserResponse;
import unis.project.delta.user.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/auth/temp-login")
    public ResponseEntity<ApiResponse<UserResponse>> createUser() {
        UserResponse response = userService.createUser();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "임시 로그인 성공"));
    }
}
