package guru.sfg.brewery.order.service.services;

import guru.sfg.brewery.model.BeerOrderDto;
import guru.sfg.brewery.order.service.domain.BeerOrder;

import java.util.UUID;

/**
 * Created by jt on 2019-09-08.
 */
public interface BeerOrderManager {

    BeerOrder newBeerOrder(BeerOrder beerOrder);

    void beerOrderPassedValidation(UUID beerOrderId);

    void beerOrderFailedValidation(UUID beerOrderId);

    void beerOrderAllocationPassed(BeerOrderDto beerOrder);

    void beerOrderAllocationPendingInventory(BeerOrderDto beerOrder);

    void beerOrderAllocationFailed(BeerOrderDto beerOrder);

    void pickupBeerOrder(UUID beerOrderId);

    void cancelOrder(UUID beerOrderId);
}
