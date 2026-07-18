package unis.project.delta.domain.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unis.project.delta.domain.shop.dto.response.CoinPackageListResponse;
import unis.project.delta.domain.shop.entity.CoinPackage;
import unis.project.delta.domain.shop.repository.CoinPackageRepository;
import unis.project.delta.domain.user.entity.User;
import unis.project.delta.domain.user.repository.UserRepository;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final CoinPackageRepository coinPackageRepository;
    private final UserRepository userRepository;

    /**
     * 현금으로 구매할 수 있는 코인 패키지 리스트를 조회한다.
     */
    @Transactional(readOnly = true)
    public CoinPackageListResponse getCoinPackages(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<CoinPackage> packages = coinPackageRepository.findAllByOrderByPriceAsc();

        return CoinPackageListResponse.of(user.getCoinBalance(), packages);
    }
}
