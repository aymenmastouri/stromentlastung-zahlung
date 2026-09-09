package de.stromentlastung.zahlung.common;

import java.time.Instant;

/**
 * Der Bauzustand eines Dienstes: welche Fassung läuft, aus welchem Stand sie gebaut
 * wurde und wann. Ein Fachverfahren muss beantworten können, was im Betrieb steht.
 */
public record VersionResponse(String dienst, String version, String revision, String referenz, Instant erbautAm) {
}
