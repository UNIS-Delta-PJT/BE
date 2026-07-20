package unis.project.delta.domain.shop.dto.response;

import unis.project.delta.domain.shop.entity.CoinPackage;

import java.util.List;

public record CoinPackageListResponse(
        Integer coinBalance,
        List<CoinPackageResponse> packages
) {
    public static CoinPackageListResponse of(Integer coinBalance, List<CoinPackage> packages) {
        List<CoinPackageResponse> list = packages.stream()
                .map(CoinPackageResponse::from)
                .toList();
        return new CoinPackageListResponse(coinBalance, list);
    }
}
