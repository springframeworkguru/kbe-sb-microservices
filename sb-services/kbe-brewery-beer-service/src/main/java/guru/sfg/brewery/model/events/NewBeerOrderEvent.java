package guru.sfg.brewery.model.events;

import guru.sfg.brewery.model.BeerOrderDto;
import org.springframework.context.ApplicationEvent;

public class NewBeerOrderEvent extends ApplicationEvent {

    public NewBeerOrderEvent(BeerOrderDto source) {
        super(source);
    }

    public BeerOrderDto getBeerOrder(){
        return (BeerOrderDto) this.source;
    }
}
