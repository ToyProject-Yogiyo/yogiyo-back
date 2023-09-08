package toy.yogiyo.core.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy.yogiyo.core.shop.Shop;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    boolean existsByName(String name);
}
