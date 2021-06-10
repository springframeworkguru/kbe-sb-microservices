/*
 *  Copyright 2019 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package guru.sfg.brewery.beer_service.bootstrap;

import guru.sfg.brewery.beer_service.domain.Beer;
import guru.sfg.brewery.beer_service.domain.Brewery;
import guru.sfg.brewery.beer_service.repositories.BeerRepository;
import guru.sfg.brewery.beer_service.repositories.BreweryRepository;
import guru.sfg.brewery.model.BeerStyleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by jt on 2019-01-26.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultBreweryLoader implements CommandLineRunner {

    public static final String BEER_1_UPC = "0631234200036";
    public static final String BEER_2_UPC = "0631234300019";
    public static final String BEER_3_UPC = "0083783375213";
    public static final String BEER_4_UPC = "0083783375232";
    public static final String BEER_5_UPC = "0083782275213";
    public static final String BEER_6_UPC = "0023763375222";
    public static final String BEER_7_UPC = "0783783332215";
    public static final String BEER_8_UPC = "0883783375217";
    public static final String BEER_9_UPC = "0983783375443";

    private final BreweryRepository breweryRepository;
    private final BeerRepository beerRepository;
    private final CacheManager cacheManager;


    @Override
    public void run(String... args) throws Exception {
        log.debug("Initializing Data");

        loadBreweryData();
        loadBeerData();

        cacheManager.getCache("beerListCache").clear();
        log.debug("Data Initialized. Beer Records loaded {}", beerRepository.count());
    }



    private void loadBeerData() {

        beerRepository.deleteAll();
        beerRepository.flush();

        Beer mangoBobs = Beer.builder()
                .beerName("Mango Bobs")
                .beerStyle(BeerStyleEnum.IPA)
                .minOnHand(12)
                .quantityToBrew(200)
                .quantityOnHand(500)
                .upc(BEER_1_UPC)
                .build();

        beerRepository.save(mangoBobs);

        Beer galaxyCat = Beer.builder()
                .beerName("Galaxy Cat")
                .beerStyle(BeerStyleEnum.PALE_ALE)
                .minOnHand(12)
                .quantityToBrew(200)
                .upc(BEER_2_UPC)
                .build();

        beerRepository.save(galaxyCat);

        Beer pinball = Beer.builder()
                .beerName("Pinball Porter")
                .beerStyle(BeerStyleEnum.PORTER)
                .minOnHand(12)
                .quantityToBrew(200)
                .upc(BEER_3_UPC)
                .build();

        beerRepository.save(pinball);

        beerRepository.save(Beer.builder()
                .beerName("Golden Buddha")
                .beerStyle(BeerStyleEnum.IPA)
                .minOnHand(12)
                .quantityToBrew(300)
                .upc(BEER_4_UPC)
                .build());

        beerRepository.save(Beer.builder()
                .beerName("Cage Blond")
                .beerStyle(BeerStyleEnum.ALE)
                .minOnHand(12)
                .quantityToBrew(200)
                .upc(BEER_5_UPC)
                .build());

        beerRepository.save(Beer.builder()
                .beerName("Amarmillo IPA")
                .beerStyle(BeerStyleEnum.IPA)
                .minOnHand(12)
                .quantityToBrew(200)
                .upc(BEER_6_UPC)
                .build());

        beerRepository.save(Beer.builder()
                .beerName("King Krush")
                .beerStyle(BeerStyleEnum.IPA)
                .minOnHand(12)
                .quantityToBrew(200)
                .upc(BEER_7_UPC)
                .build());

        beerRepository.save(Beer.builder()
                .beerName("Static IPA")
                .beerStyle(BeerStyleEnum.IPA)
                .minOnHand(12)
                .quantityToBrew(200)
                .upc(BEER_8_UPC)
                .build());

        beerRepository.saveAndFlush(Beer.builder()
                .beerName("Grand Central")
                .beerStyle(BeerStyleEnum.ALE)
                .minOnHand(12)
                .quantityToBrew(200)
                .upc(BEER_9_UPC)
                .build());

    }

    private void loadBreweryData() {
        if (breweryRepository.count() == 0) {
            breweryRepository.save(Brewery
                    .builder()
                    .breweryName("Cage Brewing")
                    .build());
        }
    }

}
