package guru.sfg.brewery.beer_service.services.inventory;

import guru.sfg.brewery.model.BeerInventoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by jt on 3/4/20.
 */
@Slf4j
@RequiredArgsConstructor
@Profile({"local-discovery", "digitalocean"})
@Service
public class BeerInventoryServiceFeign implements BeerInventoryService {
    private final InventoryServiceFeignClient inventoryServiceFeignClient;

    @Override
    public Integer getOnhandInventory(UUID beerId) {
        log.debug("Calling Inventory Service w/Feign - BeerId: " + beerId);

        int onHand = 0;

        try {
            ResponseEntity<List<BeerInventoryDto>> responseEntity = inventoryServiceFeignClient.getOnhandInventory(beerId);

            if (responseEntity.getBody() != null && responseEntity.getBody().size() > 0) {
                log.debug("Inventory found, summing inventory");

                onHand = Objects.requireNonNull(responseEntity.getBody())
                        .stream()
                        .mapToInt(BeerInventoryDto::getQuantityOnHand)
                        .sum();
            }
        } catch (Exception e) {
            log.error("Exception thrown calling inventory service", e);
            throw e;
        }

        log.debug("BeerId: " + beerId + " On hand is: " + onHand);

        return onHand;
    }
}
