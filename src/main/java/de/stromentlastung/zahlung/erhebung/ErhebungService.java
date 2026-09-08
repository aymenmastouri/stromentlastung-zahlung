package de.stromentlastung.zahlung.erhebung;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.stromentlastung.zahlung.common.CurrentUser;
import de.stromentlastung.zahlung.common.NichtGefunden;
import de.stromentlastung.zahlung.common.RegelVerletzung;
import de.stromentlastung.zahlung.common.ZugriffVerweigert;
import de.stromentlastung.zahlung.erhebung.dto.AuszahlungAnlage;
import de.stromentlastung.zahlung.erhebung.dto.RueckforderungAnlage;
import de.stromentlastung.zahlung.erhebung.dto.RueckforderungResponse;
import de.stromentlastung.zahlung.erhebung.dto.RueckzahlungAnlage;
import de.stromentlastung.zahlung.erhebung.dto.SaeumnisResponse;
import de.stromentlastung.zahlung.erhebung.dto.ZahlungResponse;

@Service
@Transactional
public class ErhebungService {

    private final ZahlungRepository zahlungen;
    private final RueckforderungRepository rueckforderungen;
    private final CurrentUser user;
    private final Clock clock;

    public ErhebungService(ZahlungRepository zahlungen, RueckforderungRepository rueckforderungen, CurrentUser user, Clock clock) {
        this.zahlungen = zahlungen;
        this.rueckforderungen = rueckforderungen;
        this.user = user;
        this.clock = clock;
    }

    public ZahlungResponse erfasseAuszahlung(AuszahlungAnlage anlage) {
        String dienststelle = user.dienststelleOderVerweigert();
        LocalDate wertstellung = anlage.wertstellung() != null ? anlage.wertstellung() : LocalDate.now(clock);
        Zahlung z = zahlungen.save(new Zahlung(anlage.aktenzeichen(), anlage.unternehmenKennung(), dienststelle, ZahlungArt.AUSZAHLUNG,
                anlage.betragCent(), wertstellung, anlage.zahlungsweg(), user.kontoname(), Instant.now(clock)));
        return zahlung(z);
    }

    @Transactional(readOnly = true)
    public List<ZahlungResponse> zahlungen(String aktenzeichen) {
        List<Zahlung> liste = zahlungen.findByAktenzeichenOrderByWertstellungAscIdAsc(aktenzeichen);
        liste.forEach(z -> pruefeLesen(z.getUnternehmenKennung(), z.getDienststelle()));
        return liste.stream().map(this::zahlung).toList();
    }

    public RueckforderungResponse legeRueckforderungAn(RueckforderungAnlage anlage) {
        String dienststelle = user.dienststelleOderVerweigert();
        rueckforderungen.findByAktenzeichen(anlage.aktenzeichen()).ifPresent(r -> {
            throw new RegelVerletzung("RUECKFORDERUNG_BEREITS_VORHANDEN", "zu " + anlage.aktenzeichen() + " besteht bereits eine Rückforderung");
        });
        if (anlage.faelligkeit().isBefore(anlage.bekanntgabe())) {
            throw new RegelVerletzung("FAELLIGKEIT_VOR_BEKANNTGABE", "Fälligkeit " + anlage.faelligkeit() + " liegt vor Bekanntgabe " + anlage.bekanntgabe());
        }
        Rueckforderung r = rueckforderungen.save(new Rueckforderung(anlage.aktenzeichen(), anlage.unternehmenKennung(), dienststelle,
                anlage.bescheidKennung(), anlage.betragCent(), anlage.bekanntgabe(), anlage.faelligkeit(), Instant.now(clock)));
        return rueckforderung(r);
    }

    @Transactional(readOnly = true)
    public RueckforderungResponse rueckforderung(String aktenzeichen) {
        Rueckforderung r = lade(aktenzeichen);
        pruefeLesen(r.getUnternehmenKennung(), r.getDienststelle());
        return rueckforderung(r);
    }

    @Transactional(readOnly = true)
    public List<RueckforderungResponse> offeneDerDienststelle() {
        return rueckforderungen.findByDienststelleAndZustandOrderByFaelligkeit(user.dienststelleOderVerweigert(), RueckforderungZustand.OFFEN)
                .stream().map(this::rueckforderung).toList();
    }

    /** Eine Rückzahlung wird erfasst; deckt sie Forderung und Säumniszuschläge, ist die Rückforderung beglichen. */
    public RueckforderungResponse erfasseRueckzahlung(String aktenzeichen, RueckzahlungAnlage anlage) {
        Rueckforderung r = lade(aktenzeichen);
        if (!user.dienststelle().map(r.getDienststelle()::equals).orElse(false)) {
            throw new ZugriffVerweigert("Rückforderung " + aktenzeichen + " gehört zu " + r.getDienststelle());
        }
        if (r.getZustand() != RueckforderungZustand.OFFEN) {
            throw new RegelVerletzung("RUECKFORDERUNG_NICHT_OFFEN", "Rückforderung " + aktenzeichen + " ist " + r.getZustand());
        }
        LocalDate wertstellung = anlage.wertstellung() != null ? anlage.wertstellung() : LocalDate.now(clock);
        zahlungen.save(new Zahlung(aktenzeichen, r.getUnternehmenKennung(), r.getDienststelle(), ZahlungArt.RUECKZAHLUNG,
                anlage.betragCent(), wertstellung, anlage.zahlungsweg(), user.kontoname(), Instant.now(clock)));
        SaeumnisRechner.Saeumnis stand = stand(r);
        if (stand.rueckgezahltCent() >= r.getBetragCent() + stand.zuschlagCent()) {
            r.begleiche(stand.zuschlagCent(), wertstellung);
        }
        return rueckforderung(r);
    }

    private SaeumnisRechner.Saeumnis stand(Rueckforderung r) {
        List<SaeumnisRechner.Rueckzahlung> rueckzahlungen = zahlungen
                .findByAktenzeichenAndArtOrderByWertstellungAscIdAsc(r.getAktenzeichen(), ZahlungArt.RUECKZAHLUNG).stream()
                .map(z -> new SaeumnisRechner.Rueckzahlung(z.getWertstellung(), z.getBetragCent(), z.getZahlungsweg())).toList();
        return SaeumnisRechner.berechne(r.getBetragCent(), r.getFaelligkeit(), rueckzahlungen, LocalDate.now(clock));
    }

    private Rueckforderung lade(String aktenzeichen) {
        return rueckforderungen.findByAktenzeichen(aktenzeichen).orElseThrow(() -> new NichtGefunden("Rückforderung " + aktenzeichen));
    }

    private void pruefeLesen(String unternehmen, String dienststelle) {
        boolean eigenes = user.unternehmen().map(unternehmen::equals).orElse(false);
        boolean eigeneDienststelle = user.dienststelle().map(dienststelle::equals).orElse(false);
        if (!eigenes && !eigeneDienststelle) {
            throw new ZugriffVerweigert("kein Zugriff auf Zahlungen von " + unternehmen);
        }
    }

    private ZahlungResponse zahlung(Zahlung z) {
        return new ZahlungResponse(z.getId(), z.getAktenzeichen(), z.getUnternehmenKennung(), z.getArt(), z.getBetragCent(),
                z.getWertstellung(), z.getZahlungsweg(), z.getErfasstVon(), z.getErfasstAm());
    }

    private RueckforderungResponse rueckforderung(Rueckforderung r) {
        SaeumnisRechner.Saeumnis s = stand(r);
        long zuschlag = r.getZustand() == RueckforderungZustand.BEGLICHEN ? r.getSaeumniszuschlagFestgesetztCent() : s.zuschlagCent();
        boolean gedeckt = s.rueckgezahltCent() >= r.getBetragCent() + zuschlag;
        return new RueckforderungResponse(r.getAktenzeichen(), r.getUnternehmenKennung(), r.getBescheidKennung(), r.getBetragCent(),
                r.getBekanntgabe(), r.getFaelligkeit(), r.getZustand(), s.rueckgezahltCent(), s.rueckstaendigCent(),
                new SaeumnisResponse(s.angefangeneMonate(), s.bemessungsgrundlageCent(), zuschlag, s.stichtag(), s.innerhalbSchonfrist()),
                r.getSaeumniszuschlagFestgesetztCent(), r.getBeglichenAm(), gedeckt);
    }
}
