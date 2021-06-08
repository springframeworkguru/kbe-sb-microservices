package guru.sfg.brewery.order.service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.sfg.brewery.model.BeerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(BeerServiceImpl.class)
class BeerServiceImplTest {

    @Autowired
    BeerServiceImpl beerService;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    ObjectMapper mapper;

    @Test
    void getBeerById() throws JsonProcessingException {
        //given
        UUID testUUID = UUID.randomUUID();
        BeerDto dto = BeerDto.builder().id(testUUID).build();
        String jsonDto = mapper.writeValueAsString(dto);

        server.expect(requestTo("http://localhost:8083/api/v1/beer/" + testUUID.toString()))
                .andRespond(withSuccess(jsonDto, MediaType.APPLICATION_JSON));

        Optional<BeerDto> beerDtoOptional = beerService.getBeerById(testUUID);

        assertTrue(beerDtoOptional.isPresent());
    }
}