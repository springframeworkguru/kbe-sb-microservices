/*
 *  Copyright 2019 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package guru.sfg.brewery.order.service.services;

import guru.sfg.brewery.model.BeerDto;
import guru.sfg.brewery.order.service.domain.BeerOrder;
import guru.sfg.brewery.order.service.domain.BeerOrderLine;
import guru.sfg.brewery.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.brewery.order.service.domain.Customer;
import guru.sfg.brewery.order.service.repositories.BeerOrderLineRepository;
import guru.sfg.brewery.order.service.repositories.BeerOrderRepository;
import guru.sfg.brewery.order.service.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class BaseServiceTest {

    public final UUID uuid1 = UUID.fromString("0ee5a0bc-f113-4b81-803d-2339787e9a87");
    public final UUID uuid2 = UUID.fromString("3a9dbef1-9671-4429-a661-085988a50692");
    public final UUID uuid3 = UUID.fromString("1b92eaa8-79aa-4257-a4ba-049f687cc7a9");

    @Autowired
    BeerOrderService beerOrderService;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    BeerOrderLineRepository beerOrderLineRepository;

    BeerDto testBeerGalaxy;
    BeerDto testBeerJava;
    BeerDto testBeerMangoBob;

    Customer testCustomer;
    BeerOrder testOrder1;
    BeerOrder testOrder2;
    BeerOrder testOrder3;

    @BeforeEach
    void setUp() {
        testBeerGalaxy = BeerDto.builder()
                .id(uuid1)
                .beerName("Galaxy Cat")
                .beerStyle("PALE_ALE")
                .build();

        testBeerJava = BeerDto.builder()
                .beerName("Java Jill")
                .beerStyle("PORTER")
                .build();

        testBeerMangoBob = BeerDto.builder()
                .beerName("Mango Bobs")
                .beerStyle("IPA")
                .build();

        testCustomer = customerRepository.save(Customer
                .builder()
                .customerName("Test 1").apiKey(UUID.randomUUID())
                .build());

        Set<BeerOrderLine> orderLines1 = new HashSet<>();
        orderLines1.add(BeerOrderLine.builder().beerId(testBeerGalaxy.getId())
                .orderQuantity(15).quantityAllocated(0).build());
        orderLines1.add(BeerOrderLine.builder().beerId(testBeerJava.getId())
                .orderQuantity(7).quantityAllocated(0).build());

        testOrder1 = beerOrderRepository.save(BeerOrder.builder()
                .orderStatus(BeerOrderStatusEnum.NEW)
                .customer(testCustomer)
                .customerRef("testOrder1")
                .orderStatusCallbackUrl("http://example.com/post")
                .beerOrderLines(orderLines1)
                .build());

        orderLines1.forEach(line -> {
            line.setBeerOrder(testOrder1);
        });

        beerOrderRepository.save(testOrder1);

        Set<BeerOrderLine> orderLines2 = new HashSet<>();
        orderLines2.add(BeerOrderLine.builder().beerId(testBeerGalaxy.getId())
                .orderQuantity(15).quantityAllocated(0).build());
        orderLines2.add(BeerOrderLine.builder().beerId(testBeerJava.getId())
                .orderQuantity(7).quantityAllocated(0).build());

        testOrder2 = beerOrderRepository.save(BeerOrder.builder()
                .orderStatus(BeerOrderStatusEnum.NEW)
                .customer(testCustomer)
                .customerRef("testOrder2")
                .orderStatusCallbackUrl("http://example.com/post")
                .beerOrderLines(orderLines2)
                .build());

        orderLines2.forEach(line -> {
            line.setBeerOrder(testOrder2);
        });

        beerOrderRepository.save(testOrder2);

        Set<BeerOrderLine> orderLines3 = new HashSet<>();
        orderLines3.add(BeerOrderLine.builder().beerId(testBeerGalaxy.getId())
                .orderQuantity(15).quantityAllocated(0).build());
        orderLines3.add(BeerOrderLine.builder().beerId(testBeerJava.getId())
                .orderQuantity(7).quantityAllocated(0).build());

        testOrder3 = beerOrderRepository.save(BeerOrder.builder()
                .orderStatus(BeerOrderStatusEnum.NEW)
                .customer(testCustomer)
                .customerRef("testOrder3")
                .orderStatusCallbackUrl("http://example.com/post")
                .beerOrderLines(orderLines3)
                .build());

        orderLines3.forEach(line -> {
            line.setBeerOrder(testOrder3);
        });

        beerOrderRepository.saveAndFlush(testOrder3);
    }
}
