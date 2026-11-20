package de.stromentlastung.zahlung.erhebung.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RueckforderungAnlage(@NotBlank String aktenzeichen, @NotBlank String unternehmenKennung,
                                   @NotBlank String bescheidKennung, @Positive long betragCent,
                                   @NotNull LocalDate bekanntgabe, @NotNull LocalDate faelligkeit) {
}
