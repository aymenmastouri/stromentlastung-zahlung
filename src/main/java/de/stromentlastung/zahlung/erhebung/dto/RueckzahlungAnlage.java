package de.stromentlastung.zahlung.erhebung.dto;

import java.time.LocalDate;

import de.stromentlastung.zahlung.erhebung.Zahlungsweg;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RueckzahlungAnlage(@Positive long betragCent, LocalDate wertstellung, @NotNull Zahlungsweg zahlungsweg) {
}
