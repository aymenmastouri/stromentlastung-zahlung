package de.stromentlastung.zahlung.erhebung;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Eine erfasste Zahlung; ein Zahlungsverkehr ist nicht angebunden (Fachkonzept V-10). */
@Entity
@Table(name = "zahlung")
public class Zahlung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String aktenzeichen;
    @Column(name = "unternehmen_kennung", nullable = false)
    private String unternehmenKennung;
    @Column(nullable = false)
    private String dienststelle;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ZahlungArt art;
    @Column(name = "betrag_cent", nullable = false)
    private long betragCent;
    @Column(nullable = false)
    private LocalDate wertstellung;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Zahlungsweg zahlungsweg;
    @Column(name = "erfasst_von", nullable = false)
    private String erfasstVon;
    @Column(name = "erfasst_am", nullable = false)
    private Instant erfasstAm;

    protected Zahlung() {
    }

    public Zahlung(String aktenzeichen, String unternehmenKennung, String dienststelle, ZahlungArt art, long betragCent,
                   LocalDate wertstellung, Zahlungsweg zahlungsweg, String erfasstVon, Instant erfasstAm) {
        this.aktenzeichen = aktenzeichen;
        this.unternehmenKennung = unternehmenKennung;
        this.dienststelle = dienststelle;
        this.art = art;
        this.betragCent = betragCent;
        this.wertstellung = wertstellung;
        this.zahlungsweg = zahlungsweg;
        this.erfasstVon = erfasstVon;
        this.erfasstAm = erfasstAm;
    }

    public Long getId() { return id; }
    public String getAktenzeichen() { return aktenzeichen; }
    public String getUnternehmenKennung() { return unternehmenKennung; }
    public String getDienststelle() { return dienststelle; }
    public ZahlungArt getArt() { return art; }
    public long getBetragCent() { return betragCent; }
    public LocalDate getWertstellung() { return wertstellung; }
    public Zahlungsweg getZahlungsweg() { return zahlungsweg; }
    public String getErfasstVon() { return erfasstVon; }
    public Instant getErfasstAm() { return erfasstAm; }
}
