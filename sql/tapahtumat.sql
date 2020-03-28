-- Tapahtumat


-- T1: Lisätään asiakkaalle xx uusi työkohde

INSERT INTO tyokohde VALUES (DEFAULT, asiakasid.xx, 'tyyppi', 'osoite');

-- T2: Tallennetaan työkohteeseen liittyvät tuntityöt ja käytetyistä tarvikkeista tiedot päivän päätteeksi.

--Esim tuntityöt && tarvikkeet
INSERT INTO tehdaan VALUES (1, 1, 2);
INSERT INTO tehdaan VALUES (2, 1, 4);

INSERT INTO sisaltaa VALUES (1, 1, 1);
INSERT INTO sisaltaa VALUES (1, 2, 20);
INSERT INTO sisaltaa VALUES (1, 3, 1, 10);

-- T3: Muodosta muistutuslasku laskuista, joita ei ole maksettu ja joiden eräpäivä umpeutunut, ja joista ei ole aiemmin lähetetty muistutuslaskua.

-- T4: Muodosta karhulasku (kolmas) muistutuslaskuista, joita ei ole maksettu ja joiden eräpäivä umpeutunut

-- T5: Tavarantoimittaja lähettää uuden hinnaston (tekstimuodossa). Pitää korvata olemassa olevat sekä pitää
--     poistaa vanhat ja lisätä uudet. Vanhat tuotteet ja tarvikkeet on toimitettava historiakansioon