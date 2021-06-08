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

import guru.sfg.brewery.model.BeerOrderDto;
import guru.sfg.brewery.model.BeerOrderLineDto;
import guru.sfg.brewery.model.BeerOrderPagedList;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ComponentScan(basePackages = {"guru.sfg.brewery.order.service.services", "guru.sfg.brewery.order.service.web.mappers"})
class BeerOrderServiceImplTest extends BaseServiceTest {

    @MockBean
    BeerService beerService;

    @Test
    void listOrders() {

        //make sure we have two orders
        assertThat(beerOrderRepository.count()).isGreaterThanOrEqualTo(3L);

        BeerOrderPagedList pagedList = beerOrderService.listOrders(testCustomer.getId(), PageRequest.of(0, 25));

        assertThat(pagedList.getTotalElements()).isGreaterThanOrEqualTo(3L);
        assertThat(pagedList.getContent().size()).isGreaterThanOrEqualTo(3);
    }

    @Test
    void placeOrder() {
        BeerOrderDto dto = BeerOrderDto.builder()
                .orderStatusCallbackUrl("http://foo.com")
                .beerOrderLines(Arrays.asList(BeerOrderLineDto
                        .builder().beerId(testBeerGalaxy.getId()).orderQuantity(12).build()))
                .build();

        BeerOrderDto placedOrder = beerOrderService.placeOrder(testCustomer.getId(), dto);

        assertThat(placedOrder.getId()).isNotNull();
        assertThat(placedOrder.getOrderStatus()).isEqualToIgnoringCase("NEW");
    }

    @Transactional
    @Test
    void getOrderById() {
        BeerOrderDto dto = beerOrderService.getOrderById(testCustomer.getId(), testOrder1.getId());

        assertThat(dto.getId()).isEqualTo(testOrder1.getId());
    }

    @Transactional
    @Test
    void pickupOrder() {
        beerOrderService.pickupOrder(testCustomer.getId(), testOrder1.getId());

        BeerOrderDto dto = beerOrderService.getOrderById(testCustomer.getId(), testOrder1.getId());

        assertThat(dto.getId()).isEqualTo(testOrder1.getId());
        assertThat(dto.getOrderStatus()).isEqualTo("PICKED_UP");
    }
}