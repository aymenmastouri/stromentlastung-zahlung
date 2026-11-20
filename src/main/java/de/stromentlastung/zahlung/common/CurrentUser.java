package de.stromentlastung.zahlung.common;

import java.util.Locale;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Liest die Claims des Zugriffstokens: die Unternehmenskennung eines
 * Antragstellerkontos, die Dienststelle eines Beschäftigtenkontos, den
 * Anzeigenamen für Zeichnungen und Ereignisse, und das Token selbst für die
 * Weitergabe an Zulieferdienste (Architektur A-06).
 */
@Component
public class CurrentUser {

    private Jwt jwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken token) {
            return token.getToken();
        }
        throw new ZugriffVerweigert("kein Zugriffstoken");
    }

    public String kontoname() {
        Jwt jwt = jwt();
        String name = jwt.getClaimAsString("name");
        return name != null && !name.isBlank() ? name : jwt.getClaimAsString("preferred_username");
    }

    public String konto() {
        return jwt().getClaimAsString("preferred_username");
    }

    public Optional<String> unternehmen() {
        return Optional.ofNullable(jwt().getClaimAsString("unternehmen")).filter(s -> !s.isBlank());
    }

    public String unternehmenOderVerweigert() {
        return unternehmen().orElseThrow(() -> new ZugriffVerweigert("das Konto gehört zu keinem Unternehmen"));
    }

    public Optional<String> dienststelle() {
        return Optional.ofNullable(jwt().getClaimAsString("dienststelle")).filter(s -> !s.isBlank());
    }

    public String dienststelleOderVerweigert() {
        return dienststelle().orElseThrow(() -> new ZugriffVerweigert("das Konto gehört zu keiner Dienststelle"));
    }

    public boolean hatRolle(String rolle) {
        String autoritaet = "ROLE_" + rolle.toUpperCase(Locale.ROOT);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream().anyMatch(a -> autoritaet.equals(a.getAuthority()));
    }

    public String bearer() {
        return jwt().getTokenValue();
    }
}
