package de.stromentlastung.zahlung.common;

/** Fehlerkörper aller Dienste: ein Regelcode für die Oberfläche, eine Diagnose für Entwickler. */
public record ApiError(String code, String message) {
}
