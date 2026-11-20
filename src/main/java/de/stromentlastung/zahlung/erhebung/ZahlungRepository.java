package de.stromentlastung.zahlung.erhebung;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ZahlungRepository extends JpaRepository<Zahlung, Long> {

    List<Zahlung> findByAktenzeichenOrderByWertstellungAscIdAsc(String aktenzeichen);

    List<Zahlung> findByAktenzeichenAndArtOrderByIdAsc(String aktenzeichen, ZahlungArt art);
}
