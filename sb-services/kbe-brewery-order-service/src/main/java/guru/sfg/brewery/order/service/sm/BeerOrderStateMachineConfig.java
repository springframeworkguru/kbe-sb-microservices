package guru.sfg.brewery.order.service.sm;

import guru.sfg.brewery.order.service.domain.BeerOrderEventEnum;
import guru.sfg.brewery.order.service.domain.BeerOrderStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@RequiredArgsConstructor
@Configuration
@EnableStateMachineFactory
public class BeerOrderStateMachineConfig extends StateMachineConfigurerAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> validateBeerOrder;
    private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> allocateBeerOrder;
    private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> validationFailureAction;
    private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> allocationFailureAction;
    private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> deAllocateOrderAction;

    @Override
    public void configure(StateMachineStateConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> states) throws Exception {
        states.withStates()
                .initial(BeerOrderStatusEnum.NEW)
                .states(EnumSet.allOf(BeerOrderStatusEnum.class))
                .end(BeerOrderStatusEnum.PICKED_UP)
                .end(BeerOrderStatusEnum.DELIVERED)
                .end(BeerOrderStatusEnum.DELIVERY_EXCEPTION)
                .end(BeerOrderStatusEnum.VALIDATION_EXCEPTION)
                .end(BeerOrderStatusEnum.ALLOCATION_ERROR)
                .end(BeerOrderStatusEnum.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> transitions) throws Exception {
        transitions.withExternal()
                .source(BeerOrderStatusEnum.NEW).target(BeerOrderStatusEnum.PENDING_VALIDATION)
                    .event(BeerOrderEventEnum.VALIDATE_ORDER)
                    .action(validateBeerOrder)
            .and().withExternal()
                .source(BeerOrderStatusEnum.PENDING_VALIDATION).target(BeerOrderStatusEnum.VALIDATED)
                    .event(BeerOrderEventEnum.VALIDATION_PASSED)
             //       .action(validationPassedAction)
            .and().withExternal()
                .source(BeerOrderStatusEnum.PENDING_VALIDATION).target(BeerOrderStatusEnum.CANCELLED)
                .event(BeerOrderEventEnum.CANCEL_ORDER)
            .and().withExternal()
                .source(BeerOrderStatusEnum.PENDING_VALIDATION).target(BeerOrderStatusEnum.VALIDATION_EXCEPTION)
                .event(BeerOrderEventEnum.VALIDATION_FAILED)
                .action(validationFailureAction)
            .and().withExternal()
                .source(BeerOrderStatusEnum.VALIDATED).target(BeerOrderStatusEnum.PENDING_ALLOCATION)
                .event(BeerOrderEventEnum.ALLOCATE_ORDER)
                .action(allocateBeerOrder)
            .and().withExternal()
                .source(BeerOrderStatusEnum.VALIDATED).target(BeerOrderStatusEnum.CANCELLED)
                .event(BeerOrderEventEnum.CANCEL_ORDER)
            .and().withExternal()
                .source(BeerOrderStatusEnum.PENDING_ALLOCATION).target(BeerOrderStatusEnum.ALLOCATED)
                .event(BeerOrderEventEnum.ALLOCATION_SUCCESS)
            .and().withExternal()
                .source(BeerOrderStatusEnum.PENDING_ALLOCATION).target(BeerOrderStatusEnum.CANCELLED)
                .event(BeerOrderEventEnum.CANCEL_ORDER)
            .and().withExternal()
                .source(BeerOrderStatusEnum.PENDING_ALLOCATION).target(BeerOrderStatusEnum.ALLOCATION_ERROR)
                .event(BeerOrderEventEnum.ALLOCATION_FAILED)
                .action(allocationFailureAction)
            .and().withExternal()
                .source(BeerOrderStatusEnum.PENDING_ALLOCATION).target(BeerOrderStatusEnum.PENDING_INVENTORY)
                .event(BeerOrderEventEnum.ALLOCATION_NO_INVENTORY)
            .and().withExternal()
                .source(BeerOrderStatusEnum.PENDING_ALLOCATION).target(BeerOrderStatusEnum.CANCELLED)
                .event(BeerOrderEventEnum.CANCEL_ORDER)
            .and().withExternal()
                .source(BeerOrderStatusEnum.ALLOCATED).target(BeerOrderStatusEnum.CANCELLED)
                .event(BeerOrderEventEnum.CANCEL_ORDER)
                .action(deAllocateOrderAction)
            .and().withExternal()
                .source(BeerOrderStatusEnum.ALLOCATED).target(BeerOrderStatusEnum.PICKED_UP)
                .event(BeerOrderEventEnum.BEER_ORDER_PICKED_UP)
        ;
    }
}
