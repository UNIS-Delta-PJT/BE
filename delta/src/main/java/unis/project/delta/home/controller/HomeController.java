package unis.project.delta.home.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import unis.project.delta.home.dto.HomeResponse;
import unis.project.delta.global.exception.dto.ApiResponse;
import unis.project.delta.home.service.HomeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
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