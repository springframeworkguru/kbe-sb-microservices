package guru.sfg.brewery.inventoryfailover.config;

import guru.sfg.brewery.inventoryfailover.web.InventoryHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Created by jt on 3/13/20.
 */
@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction orderRouter(InventoryHandler handler) {

        return route(GET("/inventory-failover").and(accept(APPLICATION_JSON)), handler::listInventory);
    }
}
