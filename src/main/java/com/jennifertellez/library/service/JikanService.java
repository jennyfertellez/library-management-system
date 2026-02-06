package com.jennifertellez.library.service;

import com.jennifertellez.library.dto.jikan.JikanMangaResponse;
import com.jennifertellez.library.dto.jikan.JikanMangaResponse.JikanMangaData;
import com.jennifertellez.library.dto.jikan.JikanSingleMangaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JikanService {

    private final RestTemplate restTemplate;
    private static final String JIKAN_BASE_URL = "https://api.jikan.moe/v4";

    /**
     * Search for manga by title
     */
    public Optional<JikanMangaData> searchMangaByTitle(String title) {
        try {
            Thread.sleep(350); // rate limit safety

            String url = UriComponentsBuilder
                    .fromUriString(JIKAN_BASE_URL + "/manga")
                    .queryParam("q", title)
                    .queryParam("limit", 1)
                    .queryParam("order_by", "popularity")
                    .build()
                    .toUriString();

            log.info("Searching Jikan API for manga: {}", title);

            JikanMangaResponse response =
                    restTemplate.getForObject(url, JikanMangaResponse.class);

            if (response != null && response.getData() != null && !response.getData().isEmpty()) {
                return Optional.of(response.getData().get(0));
            }

            return Optional.empty();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while waiting for rate limit", e);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error searching Jikan API for title: {}", title, e);
            return Optional.empty();
        }
    }

    /**
     * Get manga by MAL ID
     */
    public Optional<JikanMangaData> getMangaById(Long malId) {
        try {
            Thread.sleep(350);

            String url = JIKAN_BASE_URL + "/manga/" + malId;

            log.info("Fetching manga from Jikan API with MAL ID: {}", malId);

            JikanSingleMangaResponse response =
                    restTemplate.getForObject(url, JikanSingleMangaResponse.class);

            return response != null && response.getData() != null
                    ? Optional.of(response.getData())
                    : Optional.empty();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while waiting for rate limit", e);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error fetching manga from Jikan API with ID: {}", malId, e);
            return Optional.empty();
        }
    }
}
