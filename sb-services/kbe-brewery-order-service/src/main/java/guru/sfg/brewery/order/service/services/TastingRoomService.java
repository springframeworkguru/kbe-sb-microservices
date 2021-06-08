package guru.sfg.brewery.order.service.services;

import guru.sfg.brewery.model.BeerDto;
import guru.sfg.brewery.model.BeerPagedList;
import guru.sfg.brewery.order.service.bootstrap.OrderServiceBootstrap;
import guru.sfg.brewery.order.service.domain.BeerOrder;
import guru.sfg.brewery.order.service.domain.BeerOrderLine;
import guru.sfg.brewery.order.service.domain.Customer;
import guru.sfg.brewery.order.service.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by jt on 2019-09-29.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TastingRoomService {
    private final BeerOrderManager beerOrderManager;
    private final CustomerRepository customerRepository;
    private final BeerService beerService;

    @Scheduled(fixedRateString = "${sfg.tasting.room.rate}")
    public void createTastingRoomOrder(){

        getRandomBeer().ifPresent(beerId -> {

            Customer customer = customerRepository.findByCustomerName(OrderServiceBootstrap.CUSTOMER_NAME).orElseThrow();

            BeerOrder beerOrder = BeerOrder.builder().customer(customer).build();

            BeerOrderLine line = BeerOrderLine.builder()
                    .beerId(beerId)
                    .beerOrder(beerOrder)
                    .orderQuantity(new Random().nextInt(5) + 1) //zero based
                    .build();

            Set<BeerOrderLine> lines = new HashSet<>(1);
            lines.add(line);

            beerOrder.setBeerOrderLines(lines);

            beerOrderManager.newBeerOrder(beerOrder);
        });
    }

    private Optional<UUID> getRandomBeer(){

        Optional<BeerPagedList> listOptional = beerService.getListofBeers();

        if (listOptional.isPresent()) {
            BeerPagedList beerPagedList = listOptional.get();

            if (beerPagedList.getContent() != null && beerPagedList.getContent().size() > 0) {
                List<BeerDto> dtoList = beerPagedList.getContent();

                int k = new Random().nextInt(dtoList.size());

                return Optional.of(dtoList.get(k).getId());
            }
        }

        log.debug("Failed to get list of beers");

        return Optional.empty();

    }
}
