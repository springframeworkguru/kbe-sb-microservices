package guru.sfg.brewery.inventory_service.repositories;

import guru.sfg.brewery.inventory_service.domain.BeerInventory;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by jt on 2019-05-31.
 */
public interface BeerInventoryRepository extends PagingAndSortingRepository<BeerInventory, UUID> {

    List<BeerInventory> findAllByBeerId(UUID beerId);

    List<BeerInventory> findAllByUpc(String upc);
}
