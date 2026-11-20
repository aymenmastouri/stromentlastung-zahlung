package de.stromentlastung.zahlung.config;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Das „Heute“ des Dienstes. Die Eigenschaft stromentlastung.heute friert es auf
 * ein Datum ein, damit Fristläufe reproduzierbar sind (Fachkonzept Kap. 10);
 * ohne sie gilt die Systemuhr in Europe/Berlin.
 */
@Configuration
public class ClockConfig {

    public static final ZoneId ZONE = ZoneId.of("Europe/Berlin");

    @Bean
    public Clock clock(@Value("${stromentlastung.heute:}") String heute) {
        if (heute == null || heute.isBlank()) {
            return Clock.system(ZONE);
        }
        return Clock.fixed(LocalDate.parse(heute.trim()).atTime(LocalTime.NOON).atZone(ZONE).toInstant(), ZONE);
    }
}
