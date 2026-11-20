package de.stromentlastung.zahlung.common;

/** Ein Zulieferdienst antwortet nicht; der Vorgang bleibt in seinem Zustand (Architektur A-08). */
public class DienstNichtErreichbar extends RuntimeException {

    public DienstNichtErreichbar(String message, Throwable cause) {
        super(message, cause);
    }
}
