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

/**
 * Das Leistungsgebot eines Änderungsbescheids (Fachkonzept B-07): Betrag,
 * Bekanntgabe und Fälligkeit (F-05). Säumniszuschläge werden bis zur
 * Begleichung laufend berechnet und dann festgesetzt (R-13, F-06).
 */
@Entity
@Table(name = "rueckforderung")
public class Rueckforderung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String aktenzeichen;
    @Column(name = "unternehmen_kennung", nullable = false)
    private String unternehmenKennung;
    @Column(nullable = false)
    private String dienststelle;
    @Column(name = "bescheid_kennung", nullable = false)
    private String bescheidKennung;
    @Column(name = "betrag_cent", nullable = false)
    private long betragCent;
    @Column(nullable = false)
    private LocalDate bekanntgabe;
    @Column(nullable = false)
    private LocalDate faelligkeit;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RueckforderungZustand zustand;
    @Column(name = "saeumniszuschlag_festgesetzt_cent")
    private Long saeumniszuschlagFestgesetztCent;
    @Column(name = "beglichen_am")
    private LocalDate beglichenAm;
    @Column(name = "angelegt_am", nullable = false)
    private Instant angelegtAm;

    protected Rueckforderung() {
    }

    public Rueckforderung(String aktenzeichen, String unternehmenKennung, String dienststelle, String bescheidKennung,
                          long betragCent, LocalDate bekanntgabe, LocalDate faelligkeit, Instant angelegtAm) {
        this.aktenzeichen = aktenzeichen;
        this.unternehmenKennung = unternehmenKennung;
        this.dienststelle = dienststelle;
        this.bescheidKennung = bescheidKennung;
        this.betragCent = betragCent;
        this.bekanntgabe = bekanntgabe;
        this.faelligkeit = faelligkeit;
        this.zustand = RueckforderungZustand.OFFEN;
        this.angelegtAm = angelegtAm;
    }

    public void begleiche(long saeumniszuschlagCent, LocalDate am) {
        this.zustand = RueckforderungZustand.BEGLICHEN;
        this.saeumniszuschlagFestgesetztCent = saeumniszuschlagCent;
        this.beglichenAm = am;
    }

    public Long getId() { return id; }
    public String getAktenzeichen() { return aktenzeichen; }
    public String getUnternehmenKennung() { return unternehmenKennung; }
    public String getDienststelle() { return dienststelle; }
    public String getBescheidKennung() { return bescheidKennung; }
    public long getBetragCent() { return betragCent; }
    public LocalDate getBekanntgabe() { return bekanntgabe; }
    public LocalDate getFaelligkeit() { return faelligkeit; }
    public RueckforderungZustand getZustand() { return zustand; }
    public Long getSaeumniszuschlagFestgesetztCent() { return saeumniszuschlagFestgesetztCent; }
    public LocalDate getBeglichenAm() { return beglichenAm; }
    public Instant getAngelegtAm() { return angelegtAm; }
}
