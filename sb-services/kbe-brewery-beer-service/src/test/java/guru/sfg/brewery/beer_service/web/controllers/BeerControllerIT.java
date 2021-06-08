package guru.sfg.brewery.beer_service.web.controllers;

import guru.sfg.brewery.model.BeerDto;
import guru.sfg.brewery.model.BeerPagedList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by jt on 2019-03-03.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BeerControllerIT {

    public static final String API_V_1_BEER = "/api/v1/beer/";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetBeers() {
        BeerPagedList pagedList = restTemplate.getForObject(API_V_1_BEER, BeerPagedList.class);

        assertThat(pagedList.getContent()).hasSize(9);

        pagedList.getContent().forEach(beerDto -> {
            BeerDto fetchedBeerDto = restTemplate.getForObject(API_V_1_BEER + beerDto.getId().toString(), BeerDto.class);

            assertThat(beerDto.getId()).isEqualByComparingTo(fetchedBeerDto.getId());
        });
    }
}
