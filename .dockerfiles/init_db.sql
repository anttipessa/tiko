/*
DROP TABLE tehdaan;
DROP TABLE tuntityyppi;
DROP TABLE sisaltaa;
DROP TABLE tarvike;
DROP TABLE lasku;
DROP TABLE tyokohde;
DROP TABLE asiakas;
*/

-- Luodaan taulut
CREATE TABLE asiakas (
  asiakasid SERIAL PRIMARY KEY,   
  enimi VARCHAR(40) NOT NULL,
  snimi VARCHAR(40) NOT NULL,
  osoite VARCHAR(100) NOT NULL,
  puhelin VARCHAR(16),
  sposti VARCHAR(40)
);

CREATE TABLE tyokohde (
  kohdeid SERIAL PRIMARY KEY,
  asiakasid INTEGER NOT NULL,
  tyyppi VARCHAR(30) NOT NULL,
  osoite VARCHAR(100) NOT NULL,
  eralkm INTEGER DEFAULT 1 NOT NULL,
  FOREIGN KEY (asiakasid) REFERENCES asiakas(asiakasid),
  CHECK (LOWER(tyyppi) IN ('urakka', 'tunti')),
  CHECK (eralkm >= 0)
);

CREATE TABLE lasku (
  laskuid SERIAL PRIMARY KEY,
  asiakasid INTEGER NOT NULL,
  kohdeid INTEGER NOT NULL,
  edeltavaid INTEGER,
  luontipvm DATE DEFAULT CURRENT_DATE NOT NULL,
  erapvm DATE DEFAULT CURRENT_DATE + INTERVAL '28 day' NOT NULL,
  maksupvm DATE,
  tila VARCHAR(10) DEFAULT 'kesken' NOT NULL,
  perintakulu NUMERIC(4,2),
  FOREIGN KEY (asiakasid) REFERENCES asiakas(asiakasid),
  FOREIGN KEY (kohdeid) REFERENCES tyokohde(kohdeid),
  FOREIGN KEY (edeltavaid) REFERENCES lasku(laskuid),
  CHECK (LOWER(tila) IN ('kesken', 'siirtynyt', 'valmis')),
  CHECK (luontipvm < erapvm)
);

CREATE TABLE tarvike (
  tarvikeid SERIAL PRIMARY KEY,
  nimi VARCHAR(60) NOT NULL,
  yksikko VARCHAR(20) NOT NULL,
  varastotilanne INTEGER NOT NULL,
  ostohinta NUMERIC(8,2) NOT NULL,
  kate NUMERIC(6,2) DEFAULT 40 NOT NULL,
  alv NUMERIC(4,2) DEFAULT 24 NOT NULL,
  tila VARCHAR(20) DEFAULT 'kaytossa' NOT NULL,
  CHECK (LOWER(tila) IN ('kaytossa', 'vanhentunut'))
);

CREATE TABLE sisaltaa (
  kohdeid INTEGER,
  tarvikeid INTEGER,
  lkm INTEGER DEFAULT 0 NOT NULL,
  ale NUMERIC(5,2) DEFAULT 0,
  PRIMARY KEY (kohdeid, tarvikeid),
  FOREIGN KEY (kohdeid) REFERENCES tyokohde(kohdeid),
  FOREIGN KEY (tarvikeid) REFERENCES tarvike(tarvikeid),
  CHECK (lkm >= 0)
);
  
CREATE TABLE tuntityyppi (
  ttid SERIAL PRIMARY KEY,
  nimi VARCHAR(30) NOT NULL,
  hinta NUMERIC(5,2) NOT NULL,
  alv NUMERIC(4,2) DEFAULT 24 NOT NULL
);

CREATE TABLE tehdaan (
  ttid INTEGER,
  kohdeid INTEGER,
  lkm NUMERIC(5,2) DEFAULT 0 NOT NULL,
  ale NUMERIC(5,2) DEFAULT 0,
  PRIMARY KEY (ttid, kohdeid),
  FOREIGN KEY (ttid) REFERENCES tuntityyppi(ttid),
  FOREIGN KEY (kohdeid) REFERENCES tyokohde(kohdeid),
  CHECK (lkm >= 0)
);

-- Lisätään dataa
INSERT INTO asiakas (enimi, snimi, osoite, puhelin, sposti) VALUES ('Jorma', 'Jormakka', 'Hameenkatu 45, 33200 Tampere', '0401234567', 'jorma.jormakka@suomi24.fi');
INSERT INTO asiakas (enimi, snimi, osoite, puhelin, sposti) VALUES ('Matti', 'Jokinen', 'Eerontie 2, 33400 Ylojarvi', '0508326134', 'matti.jokinen@luukku.fi');
INSERT INTO asiakas (enimi, snimi, osoite, puhelin, sposti) VALUES ('Natalia', 'Moilanen', 'Linnankatu 89, 02380 Espoo', '0403526284', 'natalia.moilanen@gmail.com');

INSERT INTO tyokohde (asiakasid, tyyppi, osoite, eralkm) VALUES (1, 'tunti', 'Hameenkatu 45, 33200 Tampere', 1);
INSERT INTO tyokohde (asiakasid, tyyppi, osoite, eralkm) VALUES (3, 'urakka', 'Linnankatu 89, 02380 Espoo', 2);

INSERT INTO tarvike (nimi, yksikko, varastotilanne, ostohinta, kate, tila) VALUES ('Vasara', 'kpl', 1, 9.95, 50, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, varastotilanne, ostohinta, kate, tila) VALUES ('X-Naula', 'kpl', 40, 0.20, 45, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, varastotilanne, ostohinta, kate, alv, tila) VALUES ('Opaskirja', 'kpl', 2, 10, 40, 10, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, varastotilanne, ostohinta, kate, tila) VALUES ('Sahkojohto','metri', 10, 5.76, 40, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, varastotilanne, ostohinta, kate, tila) VALUES ('Pistorasia uppo 2-osainen maadoitettu', 'kpl', 5, 5.99, 40, 'vanhentunut');
INSERT INTO tarvike (nimi, yksikko, varastotilanne, ostohinta, kate, tila) VALUES ('Pistotulppa valkoinen', 'kpl', 20, 1.99, 40, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, varastotilanne, ostohinta, kate, tila) VALUES ('Airam jatkojohto 3-osainen maadoitettu 3 m', 'kpl', 6, 5.99, 40, DEFAULT);
INSERT INTO tarvike (nimi, yksikko, varastotilanne, ostohinta, kate, tila) VALUES ('ElectroGEAR valonsaadin', 'kpl', 2, 59.90, 40, 'vanhentunut');

INSERT INTO tuntityyppi (nimi, hinta) VALUES ('suunnittelu', 55);
INSERT INTO tuntityyppi (nimi, hinta) VALUES ('tyo', 45);
INSERT INTO tuntityyppi (nimi, hinta) VALUES ('aputyo', 35);
INSERT INTO tuntityyppi (nimi, hinta) VALUES ('asennus', 40);

INSERT INTO tehdaan (ttid, kohdeid, lkm) VALUES (1, 1, 2);
INSERT INTO tehdaan (ttid, kohdeid, lkm) VALUES (2, 1, 4);
INSERT INTO tehdaan (ttid, kohdeid, lkm, ale) VALUES (2, 2, 20, 10);

INSERT INTO sisaltaa (kohdeid, tarvikeid, lkm) VALUES (1, 1, 1);
INSERT INTO sisaltaa (kohdeid, tarvikeid, lkm) VALUES (1, 2, 20);
INSERT INTO sisaltaa (kohdeid, tarvikeid, lkm, ale) VALUES (1, 3, 1, 10);
INSERT INTO sisaltaa (kohdeid, tarvikeid, lkm, ale) VALUES (2, 3, 1, 40);

INSERT INTO lasku (asiakasid, kohdeid, edeltavaid, tila) VALUES (1, 1, NULL, 'kesken');
INSERT INTO lasku (asiakasid, kohdeid, edeltavaid, perintakulu, tila) VALUES (1, 1, 1, 5, 'kesken');