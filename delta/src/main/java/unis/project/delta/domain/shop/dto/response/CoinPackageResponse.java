package unis.project.delta.domain.shop.dto.response;

import unis.project.delta.domain.shop.entity.CoinPackage;

public record CoinPackageResponse(
        Long packageId,
        Integer coinAmount,
        Integer bonusCoin,
        Integer price
) {
    public static CoinPackageResponse from(CoinPackage coinPackage) {
        return new CoinPackageResponse(
                coinPackage.getId(),
                coinPackage.getCoinAmount(),
                coinPackage.getBonusCoin(),
                coinPackage.getPrice()
        );
    }
}
