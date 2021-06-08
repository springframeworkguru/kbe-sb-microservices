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

package guru.sfg.brewery.order.service.web.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import guru.sfg.brewery.model.BeerOrderDto;
import guru.sfg.brewery.model.BeerOrderLineDto;
import guru.sfg.brewery.model.BeerOrderPagedList;
import guru.sfg.brewery.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.brewery.order.service.services.BeerOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.atlassian.oai.validator.mockmvc.OpenApiValidationMatchers.openApi;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({MockitoExtension.class})
class BeerOrderControllerTest {

    //test properties
    static final String API_ROOT = "/api/v1/customers/";
    static final UUID customerId = UUID.randomUUID();
    static final UUID orderId = UUID.randomUUID();
    static final UUID beerId = UUID.randomUUID();
    static final String callbackUrl = "http://example.com";
    static final String OAC_SPEC = "https://raw.githubusercontent.com/sfg-beer-works/brewery-api/master/spec/openapi.yaml";


    @Mock
    BeerOrderService beerOrderService;

    @InjectMocks
    BeerOrderController controller;

    MockMvc mockMvc;

    @Captor
    ArgumentCaptor<BeerOrderDto> beerOrderDtoArgumentCaptorCaptor;

    @Captor
    ArgumentCaptor<UUID> customerUUIDCaptor;

    @Captor
    ArgumentCaptor<UUID> orderUUIDCaptor;

    @BeforeEach
    void setUp() {
        //
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setMessageConverters(jacksonDateTimeConverter())
                .build();
    }

    @Test
    void listOrders() throws Exception {
        //given
        List<BeerOrderDto> orderDtos = new ArrayList<>();
        orderDtos.add(buildOrderDto());
        orderDtos.add(buildOrderDto());
        given(beerOrderService.listOrders(any(), any(Pageable.class)))
                .willReturn(new BeerOrderPagedList(orderDtos, PageRequest.of(1, 1), 2L));

        mockMvc.perform(get(API_ROOT + customerId.toString()+ "/orders").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(openApi().isValid(OAC_SPEC));

        then(beerOrderService).should().listOrders(any(), any(Pageable.class));
    }

    @Test
    void placeOrder() throws Exception {
        //given
        //place order
        BeerOrderDto orderDto = buildOrderDto();

        //response order
        BeerOrderDto orderResponseDto = getBeerOrderDtoResponse();

        //build json string
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(orderDto);
        System.out.println("Order Request: " + jsonString);

        given(beerOrderService.placeOrder(customerUUIDCaptor.capture(),
                beerOrderDtoArgumentCaptorCaptor.capture())).willReturn(orderResponseDto);

        mockMvc.perform(post(API_ROOT + customerId.toString() + "/orders")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.customerId", is(customerId.toString())))
                .andExpect(jsonPath("$.beerOrderLines", hasSize(1)))
                .andExpect(jsonPath("$.beerOrderLines[0].beerId", is(beerId.toString())))
                .andExpect(openApi().isValid(OAC_SPEC));

        then(beerOrderService).should().placeOrder(any(UUID.class), any(BeerOrderDto.class));

        assertThat(customerUUIDCaptor.getValue()).isEqualTo(customerId);
    }

    private BeerOrderDto getBeerOrderDtoResponse() {
        BeerOrderDto orderResponseDto = buildOrderDto();
        orderResponseDto.setCustomerId(customerId);
        orderResponseDto.setId(orderId);
        orderResponseDto.setOrderStatus(BeerOrderStatusEnum.NEW.name());

        BeerOrderLineDto beerOrderLine = BeerOrderLineDto.builder()
                .id(UUID.randomUUID())
                .beerId(beerId)
                .upc("123123")
                .orderQuantity(5)
                .build();

        orderResponseDto.setBeerOrderLines(Arrays.asList(beerOrderLine));

        return orderResponseDto;
    }

    private BeerOrderDto buildOrderDto() {
        List<BeerOrderLineDto> orderLines = Arrays.asList(BeerOrderLineDto.builder()
                .id(UUID.randomUUID())
                .beerId(beerId)
                .upc("123123")
                .orderQuantity(5)
                .build());

        return BeerOrderDto.builder()
                .customerId(customerId)
                .customerRef("123")
                .orderStatusCallbackUrl(callbackUrl)
                .beerOrderLines(orderLines)
                .build();
    }

    @Test
    void getOrder() throws Exception {
        given(beerOrderService.getOrderById(customerUUIDCaptor.capture(),
                orderUUIDCaptor.capture())).willReturn(getBeerOrderDtoResponse());

        mockMvc.perform(get(API_ROOT + customerId + "/orders/" + orderId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.customerId", is(customerId.toString())))
                .andExpect(jsonPath("$.beerOrderLines", hasSize(1)))
                .andExpect(jsonPath("$.beerOrderLines[0].beerId", is(beerId.toString())))
                .andExpect(openApi().isValid(OAC_SPEC));
    }

    @Test
    void pickupOrder() throws Exception {
        mockMvc.perform(put(API_ROOT + customerId + "/orders/" + orderId + "/pickup"))
                .andExpect(status().isNoContent())
                .andExpect(openApi().isValid(OAC_SPEC));
    }

    private MappingJackson2HttpMessageConverter jacksonDateTimeConverter() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(new JavaTimeModule());

        return new MappingJackson2HttpMessageConverter(objectMapper);
    }
}