package guru.sfg.brewery.beer_service.services.order;

import guru.sfg.brewery.beer_service.repositories.BeerRepository;
import guru.sfg.brewery.model.BeerOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jt on 2019-09-08.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderValidator {

    private final BeerRepository beerRepository;

    public Boolean validateOrder(BeerOrderDto beerOrderDto){

        AtomicInteger beersNotFound = new AtomicInteger();

        beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
            if (beerRepository.findByUpc(beerOrderLineDto.getUpc()) == null){
                beersNotFound.incrementAndGet();
            }
        });

        //fail order if UPC not found
        return beersNotFound.get() == 0;
    }
}
