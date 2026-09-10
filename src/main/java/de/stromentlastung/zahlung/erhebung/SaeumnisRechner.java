package de.stromentlastung.zahlung.erhebung;

import java.time.LocalDate;
import java.util.List;

/**
 * Säumniszuschläge nach § 240 AO auf zurückzuzahlende Steuervergütungen
 * (Abs. 1 Satz 2). Für jeden angefangenen Monat der Säumnis 1 Prozent der
 * Bemessungsgrundlage (Abs. 1 Satz 1); bei einer Säumnis bis zu drei Tagen
 * wird bei Überweisung nichts erhoben (Abs. 3). Angefangene Monate zählen ab
 * dem Tag nach der Fälligkeit (Fachkonzept F-00, F-06).
 *
 * Bemessungsgrundlage ist der Betrag, der bei Eintritt der Säumnis rückständig
 * war: die Forderung abzüglich der bis zum Fälligkeitstag eingegangenen
 * Rückzahlungen. Rein: keine Datenbank, keine Uhr.
 */
public final class SaeumnisRechner {

    public static final int SCHONFRIST_TAGE = 3;

    public record Rueckzahlung(LocalDate datum, long betragCent, Zahlungsweg weg) {
    }

    public record Saeumnis(int angefangeneMonate, long bemessungsgrundlageCent, long zuschlagCent,
                           long rueckgezahltCent, long rueckstaendigCent, LocalDate stichtag, boolean innerhalbSchonfrist) {
    }

    private SaeumnisRechner() {
    }

    public static Saeumnis berechne(long forderungCent, LocalDate faelligkeit, List<Rueckzahlung> rueckzahlungen, LocalDate heute) {
        long rueckgezahlt = rueckzahlungen.stream().mapToLong(Rueckzahlung::betragCent).sum();
        long rueckstaendig = Math.max(0, forderungCent - rueckgezahlt);
        long rechtzeitig = rueckzahlungen.stream().filter(r -> !r.datum().isAfter(faelligkeit)).mapToLong(Rueckzahlung::betragCent).sum();
        long bemessung = Math.max(0, forderungCent - rechtzeitig);
        long bemessungsgrundlage = (bemessung / 5000L) * 5000L;

        Rueckzahlung letzte = rueckzahlungen.isEmpty() ? null : rueckzahlungen.get(rueckzahlungen.size() - 1);
        LocalDate stichtag = rueckstaendig == 0 && letzte != null ? letzte.datum() : heute;

        if (!stichtag.isAfter(faelligkeit) || bemessung == 0) {
            return new Saeumnis(0, bemessungsgrundlage, 0, rueckgezahlt, rueckstaendig, stichtag, false);
        }
        boolean schonfrist = rueckstaendig == 0 && letzte != null && letzte.weg() == Zahlungsweg.UEBERWEISUNG
                && !stichtag.isAfter(faelligkeit.plusDays(SCHONFRIST_TAGE));
        if (schonfrist) {
            return new Saeumnis(0, bemessungsgrundlage, 0, rueckgezahlt, rueckstaendig, stichtag, true);
        }
        int monate = 0;
        for (LocalDate grenze = faelligkeit; grenze.isBefore(stichtag); grenze = grenze.plusMonths(1)) {
            monate++;
        }
        long zuschlag = monate * bemessungsgrundlage / 100;
        return new Saeumnis(monate, bemessungsgrundlage, zuschlag, rueckgezahlt, rueckstaendig, stichtag, false);
    }
}
