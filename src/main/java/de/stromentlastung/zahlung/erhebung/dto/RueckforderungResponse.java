package de.stromentlastung.zahlung.erhebung.dto;

import java.time.LocalDate;

import de.stromentlastung.zahlung.erhebung.RueckforderungZustand;

public record RueckforderungResponse(String aktenzeichen, String unternehmen, String bescheidKennung, long betragCent,
                                     LocalDate bekanntgabe, LocalDate faelligkeit, RueckforderungZustand zustand,
                                     long rueckgezahltCent, long rueckstaendigCent, SaeumnisResponse saeumnis,
                                     Long saeumniszuschlagFestgesetztCent, LocalDate beglichenAm, boolean gedeckt) {
}
