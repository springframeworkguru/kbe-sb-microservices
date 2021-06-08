package guru.sfg.brewery.inventory_service.services;

import guru.sfg.brewery.model.BeerOrderDto;

/**
 * Created by jt on 2019-09-09.
 */
public interface AllocationService {

    Boolean allocateOrder(BeerOrderDto beerOrderDto);
}
