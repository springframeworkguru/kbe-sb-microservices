package guru.sfg.brewery.beer_service.services;

import guru.sfg.brewery.beer_service.domain.Beer;
import guru.sfg.brewery.beer_service.repositories.BeerRepository;
import guru.sfg.brewery.beer_service.services.inventory.BeerInventoryService;
import guru.sfg.brewery.beer_service.web.mappers.BeerMapper;
import guru.sfg.brewery.beer_service.web.mappers.BeerMapperImpl;
import guru.sfg.brewery.beer_service.web.mappers.BeerMapperImpl_;
import guru.sfg.brewery.beer_service.web.mappers.DateMapper;
import guru.sfg.brewery.model.BeerDto;
import guru.sfg.brewery.model.BeerPagedList;
import guru.sfg.brewery.model.BeerStyleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringJUnitConfig(classes = {BeerServiceImplTest.BeerServiceConfig.class})
class BeerServiceImplTest {

    @Configuration
    static class BeerServiceConfig {

        @Bean
        DateMapper dateMapper() {
            return new DateMapper();
        }

        @Bean
        @Primary
        BeerMapper beerMapper() {
            return new BeerMapperImpl();
        }

        @Bean
        @Qualifier("delegate")
        BeerMapper beerMapperImpl() {
            return new BeerMapperImpl_();
        }

        @Bean("beerService")
        BeerServiceImpl beerService(BeerRepository beerRepository, BeerMapper mapper) {
            return new BeerServiceImpl(beerRepository, mapper);
        }
    }

    @MockBean
    BeerRepository beerRepository;

    @MockBean
    BeerInventoryService beerInventoryService;

    @Autowired
    BeerServiceImpl beerService;

    @DisplayName("List Ops - ")
    @Nested
    public class TestListOptions {
        private List<Beer> beerList;
        private PageImpl<Beer> beerPage;

        @BeforeEach
        void setUp() {
            beerList = new ArrayList<>();
            List<Beer> beerList = new ArrayList<>();
            beerList.add(Beer.builder().id(UUID.randomUUID()).build());
            beerList.add(Beer.builder().id(UUID.randomUUID()).build());
            beerPage = new PageImpl<>(beerList, PageRequest.of(1, 25), 2);

            given(beerInventoryService.getOnhandInventory(any())).willReturn(1);
        }

        @DisplayName("Test Find By Name and Style")
        @Test
        void listBeersTestFindByNameAndStyle() {
            //given
            given(beerRepository.findAllByBeerNameAndBeerStyle(anyString(), any(BeerStyleEnum.class),
                    any(PageRequest.class))).willReturn(beerPage);

            //when
            BeerPagedList beerPagedList = beerService.listBeers("uuum IPA beer_service", BeerStyleEnum.IPA,
                    PageRequest.of(1, 25), false);

            //then
            assertThat(2).isEqualTo(beerPagedList.getContent().size());
        }

        @DisplayName("Test Find By Name Only")
        @Test
        void listBeersTestFindByNameOnly() {
            //given
            given(beerRepository.findAllByBeerName(anyString(), any(PageRequest.class))).willReturn(beerPage);

            //when
            BeerPagedList beerPagedList = beerService.listBeers("uuum IPA beer_service", null,
                    PageRequest.of(1, 25), false);

            //then
            assertThat(2).isEqualTo(beerPagedList.getContent().size());
        }

        @DisplayName("Test Find By Style Only")
        @Test
        void listBeersTestFindByStyleOnly() {
            //given
            given(beerRepository.findAllByBeerStyle(any(BeerStyleEnum.class), any(PageRequest.class))).willReturn(beerPage);

            //when
            BeerPagedList beerPagedList = beerService.listBeers(null, BeerStyleEnum.IPA,
                    PageRequest.of(1, 25), false);

            //then
            assertThat(2).isEqualTo(beerPagedList.getContent().size());
        }

        @DisplayName("Test Find All")
        @Test
        void listBeersTestFindAll() {
            //given
            given(beerRepository.findAll(any(PageRequest.class))).willReturn(beerPage);

            //when
            BeerPagedList beerPagedList = beerService.listBeers(null, null,
                    PageRequest.of(1, 25), false);

            //then
            assertThat(2).isEqualTo(beerPagedList.getContent().size());
        }
    }

    @DisplayName("Find By UUID")
    @Test
    void findBeerById() {
        //given
        given(beerRepository.findById(any(UUID.class))).willReturn(Optional.of(Beer.builder().build()));
        //when
        BeerDto beerDto = beerService.findBeerById(UUID.randomUUID(), false);

        //then
        assertThat(beerDto).isNotNull();
    }

    @DisplayName("Find By UUID Not Found")
    @Test
    void findBeerByIdNotFound() {
        //given
        given(beerRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        //when/then
        assertThrows(RuntimeException.class, () -> beerService.findBeerById(UUID.randomUUID(), false));
    }

    @Test
    void testSaveBeer() {
        Beer savedBeer = Beer.builder().id(UUID.randomUUID()).build();
        BeerDto newBeer = BeerDto.builder().beerName("foo").build();
        given(beerRepository.save(any())).willReturn(savedBeer);

        BeerDto savedBeerDto = beerService.saveBeer(newBeer);

        then(beerRepository).should().save(any());

        assertEquals(savedBeer.getId(), savedBeerDto.getId());
    }

    @Test
    void testUpdateBeer() {
        Beer updated = Beer.builder().id(UUID.randomUUID()).build();
        BeerDto beerDto = BeerDto.builder().build();

        given(beerRepository.save(any())).willReturn(updated);
        given(beerRepository.findById(any())).willReturn(Optional.of(updated));

        beerService.updateBeer(updated.getId(), beerDto);

        then(beerRepository).should().save(any());
    }
}