package de.stromentlastung.zahlung.common;

import java.time.Instant;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Meldet den Bauzustand dieses Dienstes. Version und Bauzeitpunkt stammen aus dem Bau
 * selbst, die Revision aus dem Bau des Abbilds; nichts davon lässt sich zur Laufzeit
 * anders behaupten. Diese Klasse ist in jedem Dienst gleich (Architektur A-04).
 */
@RestController
@RequestMapping("/version")
public class VersionController {

    private final String dienst;
    private final String revision;
    private final String referenz;
    private final ObjectProvider<BuildProperties> bau;

    public VersionController(@Value("${spring.application.name:unbekannt}") String dienst,
                             @Value("${stromentlastung.bau.revision}") String revision,
                             @Value("${stromentlastung.bau.referenz}") String referenz,
                             ObjectProvider<BuildProperties> bau) {
        this.dienst = dienst;
        this.revision = revision;
        this.referenz = referenz;
        this.bau = bau;
    }

    @GetMapping
    public VersionResponse version() {
        BuildProperties eigenschaften = bau.getIfAvailable();
        String version = eigenschaften != null ? eigenschaften.getVersion() : "unbekannt";
        Instant erbautAm = eigenschaften != null ? eigenschaften.getTime() : null;
        return new VersionResponse(dienst, version, revision, referenz, erbautAm);
    }
}
