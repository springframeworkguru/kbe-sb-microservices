package guru.sfg.brewery.order.service.services.testcomponents;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.sfg.brewery.model.events.AllocateBeerOrderResult;
import guru.sfg.brewery.order.service.config.JmsConfig;
import guru.sfg.brewery.order.service.domain.BeerOrder;
import guru.sfg.brewery.order.service.repositories.BeerOrderRepository;
import guru.sfg.brewery.order.service.web.mappers.BeerOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by jt on 2019-09-27.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderAllocationListener {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(Message msg) throws IOException, JMSException {

        String jsonString = msg.getBody(String.class);

        JsonNode event = objectMapper.readTree(jsonString);
        log.debug("Beer Order Allocation Mock received request");

        JsonNode beerOrder = event.get("beerOrder");

        boolean allocationError = false;
        boolean sendOrder = true;
        JsonNode orderId = beerOrder.get("id");
        BeerOrder beerOrderFromDB = beerOrderRepository.getOne(UUID.fromString(orderId.asText()));

        if(beerOrder.get("customerRef") != null && !StringUtils.isBlank(beerOrder.get("customerRef").asText())) {
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%");
            System.out.println(beerOrder.get("customerRef").asText());
            if (beerOrder.get("customerRef").asText().equals("allocation-fail")) {
                allocationError = true;
            } else if (beerOrder.get("customerRef").asText().equals("dont-allocate")){
                sendOrder = false;
            }
        }

        if (sendOrder){
            jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESULT_QUEUE, AllocateBeerOrderResult.builder()
                    .beerOrderDto(beerOrderMapper.beerOrderToDto(beerOrderFromDB))
                    .allocationError(allocationError)
                    .pendingInventory(false)
                    .build());
        }
    }
}
