package guru.sfg.brewery.beer_service.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.sfg.brewery.beer_service.bootstrap.DefaultBreweryLoader;
import guru.sfg.brewery.beer_service.services.BeerService;
import guru.sfg.brewery.model.BeerDto;
import guru.sfg.brewery.model.BeerPagedList;
import guru.sfg.brewery.model.BeerStyleEnum;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.atlassian.oai.validator.mockmvc.OpenApiValidationMatchers.openApi;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
class BeerControllerTest {

    public static final String GALAXY_CAT = "Galaxy Cat";
    public static final String OAC_SPEC = "https://raw.githubusercontent.com/sfg-beer-works/brewery-api/master/spec/openapi.yaml";

    @MockBean
    BeerService beerService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    BeerDto validBeer;
    BeerDto validReturnBeer;

    @BeforeEach
    void setUp() {
        validBeer = BeerDto.builder()
                .beerName("Beer1")
                .beerStyle(BeerStyleEnum.PALE_ALE)
                .price(new BigDecimal("12.99"))
                .quantityOnHand(4)
                .upc(DefaultBreweryLoader.BEER_1_UPC)
                .build();

        validReturnBeer = BeerDto.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerName("Beer1")
                .beerStyle(BeerStyleEnum.PALE_ALE)
                .price(new BigDecimal("12.99"))
                .quantityOnHand(4)
                .upc(DefaultBreweryLoader.BEER_1_UPC)
                .createdDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .build();
    }

    @AfterEach
    void tearDown() {
        reset(beerService);
    }

    @DisplayName("List Ops - ")
    @Nested
    public class TestListOperations {

        @Captor
        ArgumentCaptor<String> beerNameCaptor;

        @Captor
        ArgumentCaptor<BeerStyleEnum> beerStyleEnumCaptor;

        @Captor
        ArgumentCaptor<PageRequest> pageRequestCaptor;

        @Captor
        ArgumentCaptor<Boolean> showInventoryCaptor;

        BeerPagedList beerPagedList;

        @BeforeEach
        void setUp() {
            List<BeerDto> beers = new ArrayList<>();
            beers.add(validBeer);
            beers.add(BeerDto.builder().id(UUID.randomUUID())
                    .version(1)
                    .beerName("Beer4")
                    .upc(DefaultBreweryLoader.BEER_1_UPC)
                    .beerStyle(BeerStyleEnum.PALE_ALE)
                    .price(new BigDecimal("12.99"))
                    .quantityOnHand(66)
                    .createdDate(OffsetDateTime.now())
                    .lastModifiedDate(OffsetDateTime.now())
                    .build());

            beerPagedList = new BeerPagedList(beers, PageRequest.of(1, 1), 2L);

            given(beerService.listBeers(beerNameCaptor.capture(), beerStyleEnumCaptor.capture(),
                    pageRequestCaptor.capture(), showInventoryCaptor.capture())).willReturn(beerPagedList);
        }

        @DisplayName("Test No Params")
        @Test
        void testNoParams() throws Exception {
            mockMvc.perform(get("/api/v1/beer").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(openApi().isValid(OAC_SPEC));

            then(beerService).should().listBeers(isNull(), isNull(), any(PageRequest.class), anyBoolean());
            assertThat(0).isEqualTo(pageRequestCaptor.getValue().getPageNumber());
            assertThat(25).isEqualTo(pageRequestCaptor.getValue().getPageSize());
        }

        @DisplayName("Test Page Size Param")
        @Test
        void testPageSizeParam() throws Exception {
            mockMvc.perform(get("/api/v1/beer").accept(MediaType.APPLICATION_JSON)
                    .param("pageSize", "200"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(openApi().isValid(OAC_SPEC));

            then(beerService).should().listBeers(isNull(), isNull(), any(PageRequest.class), anyBoolean());
            assertThat(0).isEqualTo(pageRequestCaptor.getValue().getPageNumber());
            assertThat(200).isEqualTo(pageRequestCaptor.getValue().getPageSize());
        }

        @DisplayName("Test Page Param")
        @Test
        void testPageParam() throws Exception {
            mockMvc.perform(get("/api/v1/beer").accept(MediaType.APPLICATION_JSON)
                    .param("pageSize", "200"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(openApi().isValid(OAC_SPEC));

            then(beerService).should().listBeers(isNull(), isNull(), any(PageRequest.class), anyBoolean());
            assertThat(0).isEqualTo(pageRequestCaptor.getValue().getPageNumber());
            assertThat(200).isEqualTo(pageRequestCaptor.getValue().getPageSize());
        }

        @DisplayName("Test Beer Name Param")
        @Test
        void testBeerNameParam() throws Exception {
            mockMvc.perform(get("/api/v1/beer").accept(MediaType.APPLICATION_JSON)
                    .param("beerName", GALAXY_CAT))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(openApi().isValid(OAC_SPEC));

            then(beerService).should().listBeers(anyString(), isNull(), any(PageRequest.class), anyBoolean());
            assertThat(0).isEqualTo(pageRequestCaptor.getValue().getPageNumber());
            assertThat(25).isEqualTo(pageRequestCaptor.getValue().getPageSize());
            assertThat(GALAXY_CAT).isEqualToIgnoringCase(beerNameCaptor.getValue());
        }

        @DisplayName("Test Beer Style Param")
        @Test
        void testBeerStyle() throws Exception {
            mockMvc.perform(get("/api/v1/beer").accept(MediaType.APPLICATION_JSON)
                    .param("beerStyle", "IPA"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(openApi().isValid(OAC_SPEC));

            then(beerService).should().listBeers(isNull(), any(BeerStyleEnum.class), any(PageRequest.class), anyBoolean());
            assertThat(0).isEqualTo(pageRequestCaptor.getValue().getPageNumber());
            assertThat(25).isEqualTo(pageRequestCaptor.getValue().getPageSize());
            assertThat(BeerStyleEnum.IPA).isEqualTo(beerStyleEnumCaptor.getValue());
        }
    }

    @Test
    void getBeerById() throws Exception {
        //given
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
        given(beerService.findBeerById(any(UUID.class), anyBoolean())).willReturn(validReturnBeer);

        mockMvc.perform(get("/api/v1/beer/" + UUID.randomUUID()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.beerName", is("Beer1")))
                .andExpect(jsonPath("$.createdDate").isNotEmpty())
                .andExpect(openApi().isValid(OAC_SPEC));

    }

    @DisplayName("Save Ops - ")
    @Nested
    public class TestSaveOperations {
        @Test
        void testSaveNewBeer() throws Exception {
            //given
            BeerDto beerDto = validBeer;
            beerDto.setId(null);
            BeerDto savedDto = BeerDto.builder().id(UUID.randomUUID()).beerName("New Beer").build();
            String beerDtoJson = objectMapper.writeValueAsString(beerDto);

            given(beerService.saveBeer(any())).willReturn(savedDto);

            mockMvc.perform(post("/api/v1/beer/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(beerDtoJson))
                    .andExpect(status().isCreated());
        }

        @Test
        void testSaveNewBeerBadRequest() throws Exception {
            //given
            BeerDto beerDto = validBeer;
            beerDto.setId(null);
            beerDto.setBeerName(null);
            String beerDtoJson = objectMapper.writeValueAsString(beerDto);

            BeerDto savedDto = BeerDto.builder().id(UUID.randomUUID()).build();

            given(beerService.saveBeer(any())).willReturn(savedDto);

            mockMvc.perform(post("/api/v1/beer/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(beerDtoJson))
                    .andExpect(status().isBadRequest());

            then(beerService).shouldHaveZeroInteractions();
        }
    }

    @DisplayName("Save Ops - ")
    @Nested
    public class TestUpdateOperations {

        @Test
        void testUpdateBeer() throws Exception {
            //given
            BeerDto beerDto = validBeer;
            String beerDtoJson = objectMapper.writeValueAsString(beerDto);

            //when
            mockMvc.perform(put("/api/v1/beer/" +  UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(beerDtoJson))
                    .andExpect(status().isNoContent());

            then(beerService).should().updateBeer(any(), any());
        }

        @Test
        void testUpdateBeerBadRequest() throws Exception {
            //given
            BeerDto beerDto = validBeer;
            beerDto.setUpc(null);
            String beerDtoJson = objectMapper.writeValueAsString(beerDto);

            //when
            mockMvc.perform(put("/api/v1/beer/" + validBeer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(beerDtoJson))
                    .andExpect(status().isBadRequest());

            then(beerService).shouldHaveZeroInteractions();
        }
    }
}