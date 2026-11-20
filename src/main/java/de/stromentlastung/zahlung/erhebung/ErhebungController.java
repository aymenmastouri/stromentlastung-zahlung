package de.stromentlastung.zahlung.erhebung;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.stromentlastung.zahlung.erhebung.dto.AuszahlungAnlage;
import de.stromentlastung.zahlung.erhebung.dto.RueckforderungAnlage;
import de.stromentlastung.zahlung.erhebung.dto.RueckforderungResponse;
import de.stromentlastung.zahlung.erhebung.dto.RueckzahlungAnlage;
import de.stromentlastung.zahlung.erhebung.dto.ZahlungResponse;
import jakarta.validation.Valid;

@RestController
public class ErhebungController {

    private final ErhebungService erhebung;

    public ErhebungController(ErhebungService erhebung) {
        this.erhebung = erhebung;
    }

    @GetMapping
    public List<ZahlungResponse> zahlungen(@RequestParam String aktenzeichen) {
        return erhebung.zahlungen(aktenzeichen);
    }

    @PostMapping("/auszahlungen")
    @PreAuthorize("hasRole('SACHBEARBEITUNG')")
    @ResponseStatus(HttpStatus.CREATED)
    public ZahlungResponse auszahlung(@Valid @RequestBody AuszahlungAnlage anlage) {
        return erhebung.erfasseAuszahlung(anlage);
    }

    @GetMapping("/rueckforderungen")
    @PreAuthorize("hasAnyRole('SACHBEARBEITUNG', 'ZEICHNUNG', 'PRUEFDIENST')")
    public List<RueckforderungResponse> offene() {
        return erhebung.offeneDerDienststelle();
    }

    @PostMapping("/rueckforderungen")
    @PreAuthorize("hasAnyRole('SACHBEARBEITUNG', 'ZEICHNUNG')")
    @ResponseStatus(HttpStatus.CREATED)
    public RueckforderungResponse rueckforderungAnlegen(@Valid @RequestBody RueckforderungAnlage anlage) {
        return erhebung.legeRueckforderungAn(anlage);
    }

    @GetMapping("/rueckforderungen/{aktenzeichen}")
    public RueckforderungResponse rueckforderungLesen(@PathVariable String aktenzeichen) {
        return erhebung.rueckforderung(aktenzeichen);
    }

    @PostMapping("/rueckforderungen/{aktenzeichen}/rueckzahlungen")
    @PreAuthorize("hasRole('SACHBEARBEITUNG')")
    public RueckforderungResponse rueckzahlung(@PathVariable String aktenzeichen, @Valid @RequestBody RueckzahlungAnlage anlage) {
        return erhebung.erfasseRueckzahlung(aktenzeichen, anlage);
    }
}
