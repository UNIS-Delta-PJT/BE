package unis.project.delta.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unis.project.delta.category.domain.Category;
import unis.project.delta.category.dto.request.CreateCategoryRequest;
import unis.project.delta.category.dto.response.CreateCategoryResponse;
import unis.project.delta.category.dto.response.GetCategoryListResponse;
import unis.project.delta.category.repository.CategoryRepository;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;
import unis.project.delta.global.exception.dto.ApiResponse;
import unis.project.delta.user.domain.User;
import unis.project.delta.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateCategoryResponse createCategory(String uuid, CreateCategoryRequest request) {
        User user = findByUuid(uuid);

        // 카테고리 중복 검증
        validateDuplicateCategory(user, request.getName());

        Category newCategory = request.toEntity(user);
        Category savedCategory = categoryRepository.save(newCategory);
        return CreateCategoryResponse.from(savedCategory);
    }

    @Transactional(readOnly = true)
    public GetCategoryListResponse getAllCategories(String uuid) {
        User user = findByUuid(uuid);
        List<Category> categories = categoryRepository.findAllDefaultOrMyCategories(user);

        return GetCategoryListResponse.from(categories);
    }

    private User findByUuid(String uuid) {
        return userRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Category findByCategoryId(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    // 카테고리 중복 검증 메소드
    private void validateDuplicateCategory(User user, String name) {
        boolean isDuplicate = categoryRepository.existsByUserIsNullAndNameOrUserAndName(name, user, name);

        if (isDuplicate) {
            throw new CustomException(ErrorCode.DUPLICATE_CATEGORY);
        }
    }
}
