package guru.sfg.brewery.order.service.services.listeners;

import guru.sfg.brewery.model.events.BeerOrderValidationResult;
import guru.sfg.brewery.order.service.config.JmsConfig;
import guru.sfg.brewery.order.service.services.BeerOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by jt on 2019-09-08.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderValidationResultListener {

    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESULT_QUEUE)
    public void listen(BeerOrderValidationResult result) {
        final UUID beerOrderId = result.getBeerOrderId();

        log.debug("Validation Result for Order Id: " + beerOrderId + " is: " + result.getIsValid());

        if(result.getIsValid()){
            beerOrderManager.beerOrderPassedValidation(beerOrderId);
        } else {
            beerOrderManager.beerOrderFailedValidation(beerOrderId);
        }
    }
}
