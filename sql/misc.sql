/*
Drop table
 */

DROP TABLE asiakas CASCADE;
DROP TABLE tyokohde CASCADE;
DROP TABLE lasku CASCADE;
DROP TABLE tarvike CASCADE;
DROP TABLE sisaltaa CASCADE;
DROP TABLE tuntityyppi CASCADE;
DROP TABLE tehdaan CASCADE;

SELECT tyokohde.kohdeid, osoite 
FROM tyokohde FULL JOIN lasku ON tyokohde.kohdeid = lasku.kohdeid
WHERE LOWER(osoite) LIKE '%katu%' AND laskuid IS NULL
ORDER BY kohdeid;

SELECT kohdeid, osoite 
FROM tyokohde
WHERE LOWER(osoite) LIKE '%t%'
AND kohdeid NOT IN (SELECT kohdeid FROM lasku)
AND NOT tarjous
ORDER BY kohdeid;