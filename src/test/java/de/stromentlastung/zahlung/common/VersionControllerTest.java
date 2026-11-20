package de.stromentlastung.zahlung.common;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import de.stromentlastung.zahlung.config.SecurityConfig;

/** Der Bauzustand ist angemeldeten Personen zugänglich, sonst niemandem. */
@WebMvcTest(VersionController.class)
@Import({SecurityConfig.class, CurrentUser.class})
class VersionControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void meldet_dienst_und_herkunft() throws Exception {
        mvc.perform(get("/version").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dienst").value("stromentlastung-zahlung"))
                .andExpect(jsonPath("$.revision").value("pruefstand"))
                .andExpect(jsonPath("$.referenz").value("pruefstand"));
    }

    @Test
    void ohne_anmeldung_nicht() throws Exception {
        mvc.perform(get("/version")).andExpect(status().isUnauthorized());
    }
}
