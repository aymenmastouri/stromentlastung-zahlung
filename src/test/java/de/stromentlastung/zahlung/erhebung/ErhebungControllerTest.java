package de.stromentlastung.zahlung.erhebung;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import de.stromentlastung.zahlung.common.CurrentUser;
import de.stromentlastung.zahlung.config.SecurityConfig;
import de.stromentlastung.zahlung.erhebung.dto.RueckforderungResponse;
import de.stromentlastung.zahlung.erhebung.dto.SaeumnisResponse;

@WebMvcTest(ErhebungController.class)
@Import({SecurityConfig.class, CurrentUser.class})
class ErhebungControllerTest {

    @Autowired
    MockMvc mvc;
    @MockitoBean
    ErhebungService erhebung;

    @Test
    void rueckforderung_traegt_den_saeumnisstand() throws Exception {
        when(erhebung.rueckforderung("HZA-N-9b-2024-000002")).thenReturn(new RueckforderungResponse(
                "HZA-N-9b-2024-000002", "U-007", "HZA-N-9b-2024-000002-B2", 623000, LocalDate.of(2026, 2, 13), LocalDate.of(2026, 3, 13),
                RueckforderungZustand.OFFEN, 0, 623000, new SaeumnisResponse(6, 623000, 37380, LocalDate.of(2026, 9, 8), false), null, null, false));
        mvc.perform(get("/rueckforderungen/HZA-N-9b-2024-000002")
                        .with(jwt().jwt(j -> j.claim("dienststelle", "HZA-N")).authorities(new SimpleGrantedAuthority("ROLE_SACHBEARBEITUNG"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saeumnis.angefangeneMonate").value(6))
                .andExpect(jsonPath("$.zustand").value("OFFEN"));
    }

    @Test
    void nur_die_sachbearbeitung_erfasst_rueckzahlungen() throws Exception {
        mvc.perform(post("/rueckforderungen/HZA-N-9b-2024-000002/rueckzahlungen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"betragCent\":623000,\"zahlungsweg\":\"UEBERWEISUNG\"}")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ANTRAGSTELLER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void eingabefehler_werden_feldweise_gemeldet() throws Exception {
        when(erhebung.erfasseRueckzahlung(eq("X"), any())).thenReturn(null);
        mvc.perform(post("/rueckforderungen/X/rueckzahlungen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"betragCent\":0,\"zahlungsweg\":\"UEBERWEISUNG\"}")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SACHBEARBEITUNG"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.betragCent").exists());
    }
}
