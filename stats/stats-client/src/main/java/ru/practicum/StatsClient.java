package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class StatsClient {
    private final RestClient restClient;
    private final String statsUrl;

    public StatsClient(@Value("${stats-server.url}") String statsUrl) {
        this.statsUrl = statsUrl;
        this.restClient = RestClient.create(statsUrl);
    }

    public void hit(HitRequest hitDto) {
        String url = UriComponentsBuilder.fromHttpUrl(statsUrl).path("/hit").toUriString();
        ResponseEntity<Void> response = restClient.post()
                                                  .uri(url)
                                                  .contentType(APPLICATION_JSON)
                                                  .body(hitDto)
                                                  .retrieve()
                                                  .toBodilessEntity();
    }

    public List<ViewStats> stats(String start, String end, List<String> uris, Boolean unique) {
        String uri = UriComponentsBuilder.fromHttpUrl(statsUrl)
                                         .path("/stats")
                                         .queryParam("start", start)
                                         .queryParam("end", end)
                                         .queryParam("uris", uris)
                                         .queryParam("unique", unique)
                                         .toUriString();
        return restClient.get()
                         .uri(uri)
                         .retrieve()
                         .body(new ParameterizedTypeReference<>() {
                         });

    }
}
