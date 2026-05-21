package unis.project.delta.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import unis.project.delta.dto.home.HomeResponse;
import unis.project.delta.global.exception.dto.ApiResponse;
import unis.project.delta.service.HomeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    public ApiResponse<HomeResponse> getHome(
            @RequestHeader("Authorization") String uuid,
            @RequestParam String yearMonth
    ) {

        HomeResponse response =
                homeService.getHome(uuid, yearMonth);

        return ApiResponse.success(response, "홈 조회 성공");
    }
}