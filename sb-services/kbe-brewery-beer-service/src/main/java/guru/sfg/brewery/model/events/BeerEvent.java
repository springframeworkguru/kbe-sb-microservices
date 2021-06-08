package guru.sfg.brewery.model.events;

import guru.sfg.brewery.model.BeerDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by jt on 2019-06-24.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerEvent {

    private BeerDto beerDto;
}
