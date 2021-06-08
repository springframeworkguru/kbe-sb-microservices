package guru.sfg.brewery.model.events;

import guru.sfg.brewery.model.BeerDto;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public class NewInventoryEvent extends BeerEvent implements Serializable {

    static final long serialVersionUID = -1761313326070018802L;

    public NewInventoryEvent(BeerDto beerDto) {
        super(beerDto);
    }
}
