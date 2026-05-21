package unis.project.delta.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unis.project.delta.category.dto.request.CreateCategoryRequest;
import unis.project.delta.category.dto.response.CategoryResponse;
import unis.project.delta.category.service.CategoryService;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;
import unis.project.delta.global.exception.dto.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    // 카테고리 생성
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @Valid @RequestBody CreateCategoryRequest request) {

        String uuid = extractUuid(authorizationHeader);
        CategoryResponse response = categoryService.createCategory(uuid, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "카테고리 추가 성공"));
    }


    // TODO: 카테고리 목록 조회


    // 인증 헤더가 제대로 들어왔는지 확인 후 uuid 36자만 추출하는 메소드
    private String extractUuid(String header) {
        if(header == null || !header.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.MISSING_AUTHORIZATION_HEADER);
        }
        return header.substring(7); // "Bearer "가 7글자이므로 그 뒤만 잘라내기
    }
}
