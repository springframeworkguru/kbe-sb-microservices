package guru.sfg.brewery.beer_service.services.inventory;

import guru.sfg.brewery.beer_service.config.FeignClientConfig;
import guru.sfg.brewery.model.BeerInventoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;

/**
 * Created by jt on 3/4/20.
 */
@Profile({"local-discovery", "digitalocean"})
@FeignClient(name = "inventory-service", fallback = InventoryFailoverService.class, configuration = FeignClientConfig.class)
public interface InventoryServiceFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = BeerInventoryServiceRestTemplateImpl.INVENTORY_PATH)
    ResponseEntity<List<BeerInventoryDto>> getOnhandInventory(@PathVariable UUID beerId);
}
