package de.stromentlastung.zahlung;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.stromentlastung.zahlung.erhebung.RueckforderungRepository;
import de.stromentlastung.zahlung.erhebung.RueckforderungZustand;
import de.stromentlastung.zahlung.erhebung.ZahlungRepository;

@SpringBootTest
class SaatTest {

    @Autowired
    ZahlungRepository zahlungen;
    @Autowired
    RueckforderungRepository rueckforderungen;

    @Test
    void saat_ist_vollstaendig() {
        assertThat(zahlungen.count()).isEqualTo(3);
        assertThat(rueckforderungen.findByAktenzeichen("HZA-N-9b-2024-000002")).get()
                .extracting("zustand", "betragCent").containsExactly(RueckforderungZustand.OFFEN, 623000L);
    }
}
