package guru.sfg.brewery.inventory_service.listeners;

import guru.sfg.brewery.inventory_service.config.JmsConfig;
import guru.sfg.brewery.inventory_service.services.AllocationService;
import guru.sfg.brewery.model.events.AllocateBeerOrderRequest;
import guru.sfg.brewery.model.events.AllocateBeerOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by jt on 2019-09-09.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationListener {

    private final AllocationService allocationService;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(AllocateBeerOrderRequest request){
        log.debug("Allocating Order: " + request.getBeerOrder().getId());

        AllocateBeerOrderResult.AllocateBeerOrderResultBuilder builder = AllocateBeerOrderResult.builder();
        builder.beerOrderDto(request.getBeerOrder());

        try {
            Boolean allocationResult = allocationService.allocateOrder(request.getBeerOrder());

            if (allocationResult){
                builder.pendingInventory(false);
            } else {
                builder.pendingInventory(true);
            }

            builder.allocationError(false);
        } catch (Exception e) {
            //some error occured
            builder.allocationError(true).pendingInventory(false);
            log.error("Allocation attempt failed for order id " + request.getBeerOrder().getId(), e);
        }

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESULT_QUEUE, builder.build());
    }
}
