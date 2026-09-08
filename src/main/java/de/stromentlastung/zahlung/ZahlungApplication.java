package de.stromentlastung.zahlung;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Erhebung: Auszahlung, Rückforderung, Säumniszuschläge. */
@SpringBootApplication
public class ZahlungApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZahlungApplication.class, args);
    }
}
