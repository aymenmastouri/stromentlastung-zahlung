package de.stromentlastung.zahlung.common;

/**
 * Eine fachliche Regel hat Nein gesagt. Wird zu 409 mit dem Regelcode des
 * Fachkonzepts; die Oberfläche übersetzt den Code, die Nachricht ist Diagnose.
 */
public class RegelVerletzung extends RuntimeException {

    private final String code;

    public RegelVerletzung(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
