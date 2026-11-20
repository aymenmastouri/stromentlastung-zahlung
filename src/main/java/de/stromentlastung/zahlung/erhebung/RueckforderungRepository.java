package de.stromentlastung.zahlung.erhebung;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RueckforderungRepository extends JpaRepository<Rueckforderung, Long> {

    Optional<Rueckforderung> findByAktenzeichen(String aktenzeichen);

    List<Rueckforderung> findByDienststelleAndZustandOrderByFaelligkeit(String dienststelle, RueckforderungZustand zustand);
}
