package unis.project.delta.domain.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unis.project.delta.domain.shop.entity.CoinPackage;

import java.util.List;

public interface CoinPackageRepository extends JpaRepository<CoinPackage, Long> {

    List<CoinPackage> findAllByOrderByPriceAsc();
}
