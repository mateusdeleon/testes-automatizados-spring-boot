package com.example.swplanetapi;

import static com.example.swplanetapi.common.PlanetConstants.PLANET;
import static com.example.swplanetapi.common.PlanetConstants.TATOOINE;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.swplanetapi.domain.Planet;

@ActiveProfiles("it")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/import_planets.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/remove_planets.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class PlanetWebTestClientIT {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void createPlanet_ReturnsCreated() {
        Planet sut = webTestClient.post().uri("/planets").bodyValue(PLANET)
            .exchange().expectStatus().isCreated().expectBody(Planet.class)
            .returnResult().getResponseBody();
 
        assertThat(sut.getId()).isNotNull();
        assertThat(sut.getName()).isEqualTo(PLANET.getName());
    }

    @Test
    public void getPlanet_ReturnsPlanet() {
        Planet sut = webTestClient.get().uri("/planets/" + TATOOINE.getId()).exchange().expectStatus().isOk().expectBody(Planet.class)
                        .returnResult().getResponseBody();

        assertThat(sut).isNotNull();
        assertThat(sut).isEqualTo(TATOOINE);
    }

    @Test
    public void getPlanetByName_ReturnsPlanet() {
        Planet sut = webTestClient.get().uri("/planets/name/" + TATOOINE.getName()).exchange().expectStatus().isOk().expectBody(Planet.class)
                        .returnResult().getResponseBody();

        assertThat(sut).isNotNull();
        assertThat(sut).isEqualTo(TATOOINE);
    }

    @Test
    public void listPlanets_ReturnsAllPlanets() {
        Planet[] sut = webTestClient.get().uri("/planets").exchange().expectStatus().isOk().expectBody(Planet[].class)
                        .returnResult().getResponseBody();

        assertThat(sut).hasSize(3);
        assertThat(sut[0]).isEqualTo(TATOOINE);
    }

    @Test
    public void listPlanets_ByClimate_ReturnsPlanets() {
        Planet[] sut = webTestClient.get().uri("/planets?" + String.format("climate=%s", TATOOINE.getClimate()))
                        .exchange().expectStatus().isOk().expectBody(Planet[].class)
                        .returnResult().getResponseBody();

        assertThat(sut).hasSize(1);
        assertThat(sut[0]).isEqualTo(TATOOINE);
    }

    @Test
    public void listPlanet_ByTerrain_ReturnsPlanets() {
        Planet[] sut = webTestClient.get().uri("/planets?" + String.format("terrain=%s", TATOOINE.getTerrain()))
                        .exchange().expectStatus().isOk().expectBody(Planet[].class)
                        .returnResult().getResponseBody();

        assertThat(sut).hasSize(1);
        assertThat(sut[0]).isEqualTo(TATOOINE);
    }

    @Test
    public void removePlanet_ReturnsNoContent() {
        Void sut = webTestClient.delete().uri("/planets/" + TATOOINE.getId())
                        .exchange().expectStatus().isNoContent().expectBody(Void.class)
                        .returnResult().getResponseBody();
        
        assertThat(sut).isNull();
    }
}
