package com.example.demo;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

@Controller
public class HomePageController {

    private static final Logger log = LoggerFactory.getLogger(HomePageController.class);

    private final WebClient webClient = WebClient.builder().baseUrl("http://ip-api.com").build();
    
    private String getApiUri(String ipAddress) {
        return String.format("/json/%s", ipAddress.strip());
    }

    // Odpytanie API o dane o adresie IP
    private ResponseEntity<Map<String, String>> callApiForIpData(String ipAddress) {
        String apiUri = getApiUri(ipAddress);
        return webClient.get()
                .uri(apiUri)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<Map<String, String>>() {})
                .block();
    }

    /**
     * Wyciąga adres IP klienta z requestu servletowego.
     * Obsługiwane są żądania przekazywane przez proxy.
     * <a href="https://stackoverflow.com/questions/22877350/how-to-extract-ip-address-in-spring-mvc-controller-get-call">Źródło</a>
     */
    private String extractIpAddress(HttpServletRequest req) {
        String proxyHeader = req.getHeader("X-Forwarded-For");
        if(proxyHeader == null) {
            return req.getRemoteAddr();
        } else {
            return proxyHeader;
        }
    }

    @GetMapping("/")
    @ResponseBody
    public String homePage(HttpServletRequest req) {
        String userIP = extractIpAddress(req);
        log.info("Przyjęto żądanie z IP {}", userIP);
        ResponseEntity<Map<String, String>> apiResponse = callApiForIpData(userIP);
        log.info("Odpowiedź API: {}", apiResponse);
        if(!apiResponse.getStatusCode().is2xxSuccessful()) {
            log.error("API zwróciło błędny status HTTP przy odpytaniu o informacje o IP {}: {}.", userIP, apiResponse.getStatusCode());
            return provideFailedMessage(userIP);
        } else {
            Map<String, String> apiResponseBody = apiResponse.getBody();
            String status = apiResponseBody.get("status");
            if("fail".equals(status)) {
                log.warn("API zwróciło typ odpowiedzi 'fail': {}. ", apiResponseBody.get("message"));
                return provideFailedMessage(userIP);
            }
            String userTimeZoneStr = apiResponseBody.get("timezone");
            log.info("Strefa czasowa dla IP {}: {}", userIP, userTimeZoneStr);
            userTimeZoneStr = userTimeZoneStr.strip();
            ZoneId userTimeZone = ZoneId.of(userTimeZoneStr);
            ZonedDateTime serverTime = ZonedDateTime.now();
            ZonedDateTime userTime = serverTime.withZoneSameInstant(userTimeZone);
            log.info("Obecny czas serwera: {}. Czas użytkownika: {}.", serverTime, userTime);
            return String.format("Adres IP: %s. Czas: %s.", userIP, userTime.toLocalDateTime());
        }
    }

    private String provideFailedMessage(String userIP) {
        return String.format("Adres IP: %s, strefa czasowa nieznana. Czas serwera: %s", userIP, LocalDateTime.now());
    }
}
