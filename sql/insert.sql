-- Test insert queries

-- Asiakas

INSERT INTO asiakas (enimi, snimi, osoite, puhelin, sposti) VALUES ('Jorma', 'Jormakka', 'Hämeenkatu 45, 33200 Tampere', '0401234567', 'jorma.jormakka@suomi24.fi');
INSERT INTO asiakas (enimi, snimi, osoite, puhelin, sposti) VALUES ('Matti', 'Jokinen', 'Eerontie 2, 33400 Ylöjarvi', '0508326134', 'matti.jokinen@luukku.fi');
INSERT INTO asiakas (enimi, snimi, osoite, puhelin, sposti) VALUES ('Natalia', 'Moilanen', 'Linnankatu 89, 02380 Espoo', '0403526284', 'natalia.moilanen@gmail.com');

-- Tyokohde

INSERT INTO tyokohde (asiakasid, tyyppi, tarjous, osoite, eralkm) VALUES (1, 'tunti', FALSE, 'Hämeenkatu 45, 33200 Tampere', 1);
INSERT INTO tyokohde (asiakasid, tyyppi, tarjous, osoite, eralkm) VALUES (3, 'urakka', FALSE, 'Linnankatu 89, 02380 Espoo', 2);

-- Tarvike

INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('Vasara', 'kpl', 9.95, 50, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('X-Naula', 'kpl', 0.20, 45, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, alv, tila) VALUES ('Opaskirja', 'kpl', 10, 40, 10, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('Sähköjohto','metri', 5.76, 40, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('Pistorasia uppo 2-osainen maadoitettu', 'kpl', 5.99, 40, 'vanhentunut');
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('Pistotulppa valkoinen', 'kpl', 1.99, 40, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('Airam jatkojohto 3-osainen maadoitettu 3 m', 'kpl', 5.99, 40, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, tila) VALUES ('ElectroGEAR valonsäädin', 'kpl', 59.90, 40, 'vanhentunut');

-- Tuntityyppi

INSERT INTO tuntityyppi (nimi, hinta) VALUES ('suunnittelu', 55);
INSERT INTO tuntityyppi (nimi, hinta) VALUES ('työ', 45);
INSERT INTO tuntityyppi (nimi, hinta) VALUES ('aputyö', 35);
INSERT INTO tuntityyppi (nimi, hinta) VALUES ('asennus', 40);

-- Tehdaan

INSERT INTO tehdaan (ttid, kohdeid, lkm) VALUES (1, 1, 2);
INSERT INTO tehdaan (ttid, kohdeid, lkm) VALUES (2, 1, 4);
INSERT INTO tehdaan (ttid, kohdeid, lkm, ale) VALUES (2, 2, 20, 10);

-- Sisaltaa

INSERT INTO sisaltaa (kohdeid, tarvikeid, lkm) VALUES (1, 1, 1);
INSERT INTO sisaltaa (kohdeid, tarvikeid, lkm) VALUES (1, 2, 20);
INSERT INTO sisaltaa (kohdeid, tarvikeid, lkm, ale) VALUES (1, 3, 1, 10);
INSERT INTO sisaltaa (kohdeid, tarvikeid, lkm, ale) VALUES (2, 3, 1, 40);

-- Lasku

INSERT INTO lasku (asiakasid, kohdeid, edeltavaid, tila) VALUES (1, 1, NULL, 'siirtynyt');
INSERT INTO lasku (asiakasid, kohdeid, edeltavaid, perintakulu, tila) VALUES (1, 1, 1, 5, 'kesken');

INSERT INTO lasku (asiakasid, kohdeid, edeltavaid, tila) VALUES (2, 2, NULL, 'siirtynyt');
INSERT INTO lasku (asiakasid, kohdeid, edeltavaid, perintakulu, tila) VALUES (2, 2, 3, 5, 'siirtynyt');
INSERT INTO lasku (asiakasid, kohdeid, edeltavaid, perintakulu, tila) VALUES (2, 2, 4, 10, 'kesken');

