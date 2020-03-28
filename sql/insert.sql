-- Test insert queries

-- Asiakas

INSERT INTO asiakas VALUES (DEFAULT, 'Jorma', 'Jormakka', 'Hameenkatu 45, 33200 Tampere', '0401234567', 'jorma.jormakka@suomi24.fi');
INSERT INTO asiakas VALUES (DEFAULT, 'Matti', 'Jokinen', 'Eerontie 2, 33400 Ylojarvi', '0508326134', 'matti.jokinen@luukku.fi');
INSERT INTO asiakas VALUES (DEFAULT, 'Natalia', 'Moilanen', 'Linnankatu 89, 02380 Espoo', '0403526284', 'natalia.moilanen@gmail.com');

-- Tyokohde

INSERT INTO tyokohde VALUES (DEFAULT, 1, 'tunti', 'Hameenkatu 45, 33200 Tampere');
INSERT INTO tyokohde VALUES (DEFAULT, 3, 'urakka', 'Linnankatu 89, 02380 Espoo');

-- Tarvike

INSERT INTO tarvike VALUES (DEFAULT, 'Vasara', 'kpl', 1, 9.95, 50);
INSERT INTO tarvike VALUES (DEFAULT, 'X-Naula', 'kpl', 40, 0.20, 45);
INSERT INTO tarvike VALUES (DEFAULT, 'Opaskirja', 'kpl', 2, 10, 40, 10);
INSERT INTO tarvike VALUES (DEFAULT, 'Sahkojohto','metri', 10, 5.76, 40);
INSERT INTO tarvike VALUES (DEFAULT, 'Pistorasia uppo 2-osainen maadoitettu', 'kpl', 5, 5.99, 40);
INSERT INTO tarvike VALUES (DEFAULT, 'Pistotulppa valkoinen', 'kpl', 20, 1.99, 40);
INSERT INTO tarvike VALUES (DEFAULT, 'Airam jatkojohto 3-osainen maadoitettu 3 m', 'kpl', 6, 5.99, 40);
INSERT INTO tarvike VALUES (DEFAULT, 'ElectroGEAR valonsaadin', 'kpl', 2, 59.90, 40);

-- Tuntityyppi

INSERT INTO tuntityyppi VALUES (DEFAULT, 'suunnittelu', 55);
INSERT INTO tuntityyppi VALUES (DEFAULT, 'tyo', 45);
INSERT INTO tuntityyppi VALUES (DEFAULT, 'aputyo', 35);
INSERT INTO tuntityyppi VALUES (DEFAULT, 'asennus', 40);

-- Tehdaan

INSERT INTO tehdaan VALUES (1, 1, 2);
INSERT INTO tehdaan VALUES (2, 1, 4);

-- Sisaltaa

INSERT INTO sisaltaa VALUES (1, 1, 1);
INSERT INTO sisaltaa VALUES (1, 2, 20);
INSERT INTO sisaltaa VALUES (1, 3, 1, 10);

-- Lasku

INSERT INTO lasku VALUES (DEFAULT, 1, 1, 1);

