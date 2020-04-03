-- Tapahtumat


-- T1: Lisätään asiakkaalle xx uusi työkohde

INSERT INTO tyokohde (asiakasid, tyyppi, osoite, eralkm) VALUES (asiakasid.xx, 'tyyppi', 'osoite', eralkm);

-- T2: Tallennetaan työkohteeseen liittyvät tuntityöt ja käytetyistä tarvikkeista tiedot päivän päätteeksi.

-- Esim tuntityöt && tarvikkeet
INSERT INTO tehdaan (ttid, kohdeid, lkm, ale) VALUES (1, 1, 2, 0);
INSERT INTO tehdaan (ttid, kohdeid, lkm, ale) VALUES (2, 1, 4, 0);

INSERT INTO sisaltaa VALUES (1, 1, 1);
INSERT INTO sisaltaa VALUES (1, 2, 20);
INSERT INTO sisaltaa VALUES (1, 3, 1, 10);

-- T3: Muodosta muistutuslasku laskuista, joita ei ole maksettu ja joiden eräpäivä umpeutunut,
--     ja joista ei ole aiemmin lähetetty muistutuslaskua.

-- Haetaan laskut, joita ei ole maksettu, eräpäivä umpeutunut ja joista ei aiemmin ole lähetetty
-- muistutuslaskua.
SELECT laskuid, asiakasid, kohdeid
FROM lasku
WHERE maksupvm IS NULL AND erapvm > CURRENT_DATE AND tila = 'kesken' AND edeltavaid IS NULL;

-- Tehdään muistutuslasku, johon käytetään haettuja sarakkeiden arvoja (laskuid, asiakasid, kohdeid)
INSERT INTO lasku (asiakasid, kohdeid, edeltavaid, perintakulu, tila)
VALUES (asiakasid, kohdeid, laskuid, 5, 'kesken');

-- Muutetaan vanhan laskun tilaksi siirtynyt (laskuid haetaan SELECT lauseen palauttamista sarakkeista)
UPDATE lasku SET tila = 'siirtynyt' WHERE laskuid = laskuid;

-- T4: Muodosta karhulasku (kolmas) muistutuslaskuista, joita ei ole maksettu ja joiden eräpäivä
--     on umpeutunut

-- T5: Tavarantoimittaja lähettää uuden hinnaston (tekstimuodossa). Pitää korvata olemassa olevat sekä
--     pitää poistaa vanhat ja lisätä uudet. Vanhat tuotteet ja tarvikkeet on toimitettava historiakansioon