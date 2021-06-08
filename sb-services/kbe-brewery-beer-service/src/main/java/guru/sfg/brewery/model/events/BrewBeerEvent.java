package guru.sfg.brewery.model.events;

import guru.sfg.brewery.model.BeerDto;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public class BrewBeerEvent extends BeerEvent implements Serializable {

    static final long serialVersionUID = 5294557463904704401L;

    public BrewBeerEvent(BeerDto beerDto) {
        super(beerDto);
    }

}
