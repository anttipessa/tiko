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
  asiakasid INTEGER,  
  tyyppi VARCHAR(30) NOT NULL,
  osoite VARCHAR(100) NOT NULL,
  eralkm INTEGER DEFAULT 1 NOT NULL,
  FOREIGN KEY (asiakasid) REFERENCES asiakas(asiakasid)
);

CREATE TABLE lasku (
  laskuid SERIAL PRIMARY KEY,
  asiakasid INTEGER,
  kohdeid INTEGER,
  edeltavaid INTEGER,
  luontipvm DATE DEFAULT CURRENT_DATE NOT NULL,
  erapvm DATE DEFAULT CURRENT_DATE + INTERVAL '28 day' NOT NULL,
  maksupvm DATE,
  tila VARCHAR(10) DEFAULT 'kesken' NOT NULL,
  FOREIGN KEY (asiakasid) REFERENCES asiakas(asiakasid),
  FOREIGN KEY (kohdeid) REFERENCES tyokohde(kohdeid),
  FOREIGN KEY (edeltavaid) REFERENCES lasku(laskuid)
);

CREATE TABLE tarvike (
  tarvikeid SERIAL PRIMARY KEY,
  nimi VARCHAR(60) NOT NULL,
  yksikko VARCHAR(20) NOT NULL,
  varastotilanne INTEGER NOT NULL,
  ostohinta NUMERIC(8,2) NOT NULL,
  kate NUMERIC(6,2) NOT NULL,
  alv NUMERIC(4,2) DEFAULT 24 NOT NULL,
  tila VARCHAR(20) DEFAULT 'kaytossa' NOT NULL
);

CREATE TABLE sisaltaa (
  kohdeid INTEGER,
  tarvikeid INTEGER,
  lkm INTEGER DEFAULT 0 NOT NULL,
  ale NUMERIC(5,2) DEFAULT 0,
  PRIMARY KEY (kohdeid, tarvikeid),
  FOREIGN KEY (kohdeid) REFERENCES tyokohde(kohdeid),
  FOREIGN KEY (tarvikeid) REFERENCES tarvike(tarvikeid)
  );
  
CREATE TABLE tuntityyppi (
  ttid SERIAL PRIMARY KEY,
  nimi VARCHAR(30) NOT NULL,
  hinta NUMERIC(5,2) NOT NULL,
  alv NUMERIC(4,2) NOT NULL,
);

CREATE TABLE tehdaan (
  ttid INTEGER,
  kohdeid INTEGER,
  lkm NUMERIC(5,2) DEFAULT 0 NOT NULL,
  ale NUMERIC(5,2) DEFAULT 0,
  PRIMARY KEY (ttid, kohdeid),
  FOREIGN KEY (ttid) REFERENCES tuntityyppi(ttid),
  FOREIGN KEY (kohdeid) REFERENCES tyokohde(kohdeid)
  );
