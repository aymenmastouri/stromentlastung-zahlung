package de.stromentlastung.zahlung.erhebung.dto;

import java.time.LocalDate;

public record SaeumnisResponse(int angefangeneMonate, long bemessungsgrundlageCent, long zuschlagCent, LocalDate stichtag,
                               boolean innerhalbSchonfrist) {
}
