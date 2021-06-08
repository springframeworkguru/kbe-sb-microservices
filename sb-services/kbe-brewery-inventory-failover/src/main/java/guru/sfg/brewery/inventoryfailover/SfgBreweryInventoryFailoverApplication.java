package guru.sfg.brewery.inventoryfailover;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;

@SpringBootApplication(exclude = ArtemisAutoConfiguration.class)
public class SfgBreweryInventoryFailoverApplication {

	public static void main(String[] args) {
		SpringApplication.run(SfgBreweryInventoryFailoverApplication.class, args);
	}



}
