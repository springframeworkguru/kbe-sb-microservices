package guru.sfg.brewery.beer_service.services.inventory;

import guru.sfg.brewery.model.BeerInventoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Created by jt on 3/14/20.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class InventoryFailoverService implements InventoryServiceFeignClient {
    private final InventoryFailoverFeignClient inventoryFailoverFeignClient;

    @Override
    public ResponseEntity<List<BeerInventoryDto>> getOnhandInventory(UUID beerId) {
        log.debug("Calling Inventory Failover for Beer Id: " + beerId);
        return inventoryFailoverFeignClient.getOnhandInventory();
    }
}
