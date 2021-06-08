package guru.sfg.brewery.order.service.services.testcomponents;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.sfg.brewery.model.events.BeerOrderValidationResult;
import guru.sfg.brewery.order.service.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.IOException;
import java.util.UUID;

/**
 * test component for beer order validation
 *
 * Created by jt on 2019-09-26.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderValidationListener {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public void listen(Message msg) throws IOException, JMSException {

      //  await().atLeast(1L, TimeUnit.SECONDS).until(() -> true);

        String jsonString = msg.getBody(String.class);

        JsonNode event = objectMapper.readTree(jsonString);
        log.debug("Beer Order Validation Mock received request");

        JsonNode beerOrder = event.get("beerOrder");

        boolean isValid = true;
        boolean sendOrder = true;
        JsonNode orderId = beerOrder.get("id");

        if(beerOrder.get("customerRef") != null) {
            if (beerOrder.get("customerRef").asText().equals("fail-validation")) {
                isValid = false;
            } else if (beerOrder.get("customerRef").asText().equals("dont-validate")){
                sendOrder = false;
            }
        }

        if (sendOrder) {
            jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESULT_QUEUE, BeerOrderValidationResult.builder()
                    .beerOrderId(UUID.fromString(orderId.asText()))
                    .isValid(isValid)
                    .build());
        }
    }
}
