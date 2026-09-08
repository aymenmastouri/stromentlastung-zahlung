package de.stromentlastung.zahlung.erhebung;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.stromentlastung.zahlung.erhebung.SaeumnisRechner.Rueckzahlung;
import de.stromentlastung.zahlung.erhebung.SaeumnisRechner.Saeumnis;

/** Rückforderung 6.200,00 Euro, fällig am 13. März 2026 (Fachkonzept R-13, F-06). */
class SaeumnisRechnerTest {

    private static final long FORDERUNG = 620000;
    private static final LocalDate FAELLIG = LocalDate.of(2026, 3, 13);

    @Test
    void am_faelligkeitstag_keine_saeumnis() {
        Saeumnis s = SaeumnisRechner.berechne(FORDERUNG, FAELLIG, List.of(), FAELLIG);
        assertThat(s.angefangeneMonate()).isZero();
        assertThat(s.zuschlagCent()).isZero();
        assertThat(s.rueckstaendigCent()).isEqualTo(FORDERUNG);
    }

    @Test
    void der_tag_nach_der_faelligkeit_beginnt_den_ersten_monat() {
        Saeumnis s = SaeumnisRechner.berechne(FORDERUNG, FAELLIG, List.of(), FAELLIG.plusDays(1));
        assertThat(s.angefangeneMonate()).isEqualTo(1);
        assertThat(s.zuschlagCent()).isEqualTo(6200);
    }

    @Test
    void ueberweisung_innerhalb_von_drei_tagen_bleibt_zuschlagsfrei() {
        Saeumnis s = SaeumnisRechner.berechne(FORDERUNG, FAELLIG,
                List.of(new Rueckzahlung(FAELLIG.plusDays(3), FORDERUNG, Zahlungsweg.UEBERWEISUNG)), LocalDate.of(2026, 9, 8));
        assertThat(s.innerhalbSchonfrist()).isTrue();
        assertThat(s.zuschlagCent()).isZero();
        assertThat(s.rueckstaendigCent()).isZero();
    }

    @Test
    void die_schonfrist_gilt_nicht_bei_uebergabe_von_zahlungsmitteln() {
        Saeumnis s = SaeumnisRechner.berechne(FORDERUNG, FAELLIG,
                List.of(new Rueckzahlung(FAELLIG.plusDays(3), FORDERUNG, Zahlungsweg.SONSTIGE)), LocalDate.of(2026, 9, 8));
        assertThat(s.innerhalbSchonfrist()).isFalse();
        assertThat(s.angefangeneMonate()).isEqualTo(1);
        assertThat(s.zuschlagCent()).isEqualTo(6200);
    }

    @Test
    void genau_ein_monat_ist_ein_angefangener_monat_ein_tag_mehr_sind_zwei() {
        assertThat(SaeumnisRechner.berechne(FORDERUNG, FAELLIG, List.of(), LocalDate.of(2026, 4, 13)).angefangeneMonate()).isEqualTo(1);
        Saeumnis zwei = SaeumnisRechner.berechne(FORDERUNG, FAELLIG,
                List.of(new Rueckzahlung(LocalDate.of(2026, 4, 14), FORDERUNG, Zahlungsweg.UEBERWEISUNG)), LocalDate.of(2026, 9, 8));
        assertThat(zwei.angefangeneMonate()).isEqualTo(2);
        assertThat(zwei.zuschlagCent()).isEqualTo(12400);
    }

    @Test
    void sechs_angefangene_monate_am_achten_september() {
        Saeumnis s = SaeumnisRechner.berechne(FORDERUNG, FAELLIG, List.of(), LocalDate.of(2026, 9, 8));
        assertThat(s.angefangeneMonate()).isEqualTo(6);
        assertThat(s.zuschlagCent()).isEqualTo(37200);
    }

    @Test
    void eine_rechtzeitige_teilzahlung_senkt_die_bemessungsgrundlage() {
        Saeumnis s = SaeumnisRechner.berechne(FORDERUNG, FAELLIG,
                List.of(new Rueckzahlung(FAELLIG.minusDays(1), 320000, Zahlungsweg.UEBERWEISUNG)), LocalDate.of(2026, 4, 1));
        assertThat(s.bemessungsgrundlageCent()).isEqualTo(300000);
        assertThat(s.zuschlagCent()).isEqualTo(3000);
    }
}
