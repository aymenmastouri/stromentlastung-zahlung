package de.stromentlastung.zahlung.erhebung.dto;

import java.time.Instant;
import java.time.LocalDate;

import de.stromentlastung.zahlung.erhebung.ZahlungArt;
import de.stromentlastung.zahlung.erhebung.Zahlungsweg;

public record ZahlungResponse(Long id, String aktenzeichen, String unternehmen, ZahlungArt art, long betragCent,
                              LocalDate wertstellung, Zahlungsweg zahlungsweg, String erfasstVon, Instant erfasstAm) {
}
