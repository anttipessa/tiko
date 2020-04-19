-- Test insert queries

-- Asiakas

INSERT INTO asiakas (enimi, snimi, osoite, puhelin, sposti) VALUES ('Jorma', 'Jormakka', 'Hämeenkatu 45, 33200 Tampere', '0401234567', 'jorma.jormakka@suomi24.fi');
INSERT INTO asiakas (enimi, snimi, osoite, puhelin, sposti) VALUES ('Matti', 'Jokinen', 'Eerontie 2, 33400 Ylöjarvi', '0508326134', 'matti.jokinen@luukku.fi');
INSERT INTO asiakas (enimi, snimi, osoite, puhelin, sposti) VALUES ('Natalia', 'Moilanen', 'Linnankatu 89, 02380 Espoo', '0403526284', 'natalia.moilanen@gmail.com');
INSERT INTO asiakas (enimi, snimi, osoite, puhelin, sposti) VALUES ('Pentti', 'Hietala', 'Aleksanterinkatu 52, 00100 Helsinki', '0509871212', 'pentti.hietala@gmail.com');
INSERT INTO asiakas (enimi, snimi, osoite, puhelin, sposti) VALUES ('Liisa', 'Ronkainen', 'Lielahdenkatu 45, 33410 Tampere', '0404539076', 'liisaronkainen@hotmail.com');
INSERT INTO asiakas (enimi, snimi, osoite, puhelin, sposti) VALUES ('Tanja', 'Jokinen', 'Keskuskatu 8, 37800 Akaa', '0403552224', 't_jokinen@hotmail.com');

-- Tyokohde

INSERT INTO tyokohde (asiakasid, tyyppi, tarjous, osoite, eralkm) VALUES (1, 'tunti', FALSE, 'Hämeenkatu 45, 33200 Tampere', 1);
INSERT INTO tyokohde (asiakasid, tyyppi, tarjous, osoite, eralkm) VALUES (3, 'urakka', FALSE, 'Linnankatu 89, 02380 Espoo', 2);
INSERT INTO tyokohde (asiakasid, tyyppi, tarjous, osoite, eralkm) VALUES (4, 'urakka', TRUE, 'Satamakatu 5, 40100 Jyväskylä', 4);
INSERT INTO tyokohde (asiakasid, tyyppi, tarjous, osoite, eralkm) VALUES (4, 'urakka', FALSE, 'Runebergin Esplanadi 2, 00130 Helsinki', 2);

-- Tarvike

INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('Vasara', 'kpl', 9.95, 50, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('X-Naula', 'kpl', 0.20, 45, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, alv, tila) VALUES ('Opaskirja', 'kpl', 10, 40, 10, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('Sähköjohto','metri', 5.76, 40, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('Pistorasia uppo 2-osainen maadoitettu', 'kpl', 5.99, 40, 'vanhentunut');
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('Pistotulppa valkoinen', 'kpl', 1.99, 40, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('Airam jatkojohto 3-osainen maadoitettu 3m', 'kpl', 5.99, 40, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('ElectroGEAR valonsäädin', 'kpl', 59.90, 40, 'vanhentunut');
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('Nippuside musta, 100kpl', 'kpl', 3.95, 40, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('Putkikiinnike 20mm', 'kpl', 2.99, 40, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('Themo älytermostaatti', 'kpl', 149.99, 40, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('Airam LED-valonsäädin', 'kpl', 49.90, 40, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('Sidontaspiraali, musta 10mm', 'metri', 0.89, 40, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('ABB antennikeskiölevy, valkoinen', 'kpl', 3.99, 40, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('ABB ripustuskansi IP20', 'kpl', 5.90, 40, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('Kattokuppi, valkoinen', 'kpl', 1.99, 40, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('Läpivienti M16, 5-10mm', 'kpl', 0.50, 40, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('Holkkitiiviste M16, 5-10mm', 'kpl', 0.99, 40, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('Sulakekansi 25A', 'kpl', 1.99, 40, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('MCMK maakaapeli, 2x1,5+1,5mm', 'metri', 1.20, 40, DEFAULT);

-- Tuntityyppi

INSERT INTO tuntityyppi (nimi, hinta) VALUES ('suunnittelu', 55);
INSERT INTO tuntityyppi (nimi, hinta) VALUES ('työ', 45);
INSERT INTO tuntityyppi (nimi, hinta) VALUES ('aputyö', 35);
INSERT INTO tuntityyppi (nimi, hinta) VALUES ('asennus', 40);

-- Tehdaan

INSERT INTO tehdaan (ttid, kohdeid, lkm) VALUES (1, 1, 2);
INSERT INTO tehdaan (ttid, kohdeid, lkm) VALUES (2, 1, 4);
INSERT INTO tehdaan (ttid, kohdeid, lkm, ale) VALUES (4, 1, 10, 10);
INSERT INTO tehdaan (ttid, kohdeid, lkm) VALUES (4, 2, 5);
INSERT INTO tehdaan (ttid, kohdeid, lkm, ale) VALUES (2, 2, 20, 10);
INSERT INTO tehdaan (ttid, kohdeid, lkm) VALUES (1, 4, 3);
INSERT INTO tehdaan (ttid, kohdeid, lkm) VALUES (2, 4, 10);

-- Sisaltaa

INSERT INTO sisaltaa (kohdeid, tarvikeid, lkm) VALUES (1, 1, 1);
INSERT INTO sisaltaa (kohdeid, tarvikeid, lkm) VALUES (1, 2, 20);
INSERT INTO sisaltaa (kohdeid, tarvikeid, lkm, ale) VALUES (1, 3, 1, 10);
INSERT INTO sisaltaa (kohdeid, tarvikeid, lkm, ale) VALUES (2, 3, 1, 40);
INSERT INTO sisaltaa (kohdeid, tarvikeid, lkm) VALUES (3, 7, 1);
INSERT INTO sisaltaa (kohdeid, tarvikeid, lkm) VALUES (3, 10, 20);

-- Lasku

INSERT INTO lasku (asiakasid, kohdeid, edeltavaid, tila) VALUES (1, 1, NULL, 'siirtynyt');
INSERT INTO lasku (asiakasid, kohdeid, edeltavaid, perintakulu, tila) VALUES (1, 1, 1, 5, 'kesken');

INSERT INTO lasku (asiakasid, kohdeid, edeltavaid, tila) VALUES (2, 2, NULL, 'siirtynyt');
INSERT INTO lasku (asiakasid, kohdeid, edeltavaid, perintakulu, tila) VALUES (2, 2, 3, 5, 'siirtynyt');
INSERT INTO lasku (asiakasid, kohdeid, edeltavaid, perintakulu, tila) VALUES (2, 2, 4, 10, 'kesken');

