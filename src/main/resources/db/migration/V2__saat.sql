-- Zahlungen der Saatdaten-Geschichten (Fachkonzept Kap. 12): S-01, S-03 und S-07 sind ausgezahlt,
-- S-07 trägt nach der Außenprüfung eine offene Rückforderung von 6.230,00 Euro.
INSERT INTO zahlung (aktenzeichen, unternehmen_kennung, dienststelle, art, betrag_cent, wertstellung, zahlungsweg, erfasst_von, erfasst_am) VALUES
    ('HZA-N-9b-2025-000001', 'U-001', 'HZA-N', 'AUSZAHLUNG', 3675000, DATE '2026-05-20', 'UEBERWEISUNG', 'Sabine Wagner', TIMESTAMP WITH TIME ZONE '2026-05-20 11:00:00+02'),
    ('HZA-N-9b-2023-000001', 'U-003', 'HZA-N', 'AUSZAHLUNG',  190460, DATE '2025-01-15', 'UEBERWEISUNG', 'Sabine Wagner', TIMESTAMP WITH TIME ZONE '2025-01-15 11:00:00+01'),
    ('HZA-N-9b-2024-000002', 'U-007', 'HZA-N', 'AUSZAHLUNG', 2975000, DATE '2025-06-10', 'UEBERWEISUNG', 'Murat Demir',   TIMESTAMP WITH TIME ZONE '2025-06-10 11:00:00+02');

INSERT INTO rueckforderung (aktenzeichen, unternehmen_kennung, dienststelle, bescheid_kennung, betrag_cent, bekanntgabe, faelligkeit, zustand, angelegt_am) VALUES
    ('HZA-N-9b-2024-000002', 'U-007', 'HZA-N', 'HZA-N-9b-2024-000002-B2', 623000, DATE '2026-02-13', DATE '2026-03-13', 'OFFEN', TIMESTAMP WITH TIME ZONE '2026-02-09 15:00:00+01');
