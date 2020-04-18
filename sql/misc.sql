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

-- Hae muistutuslaskut (yksi edeltävä lasku)
SELECT laskuid, a.enimi || ' ' || a.snimi AS nimi, t.osoite, luontipvm, erapvm, maksupvm, perintakulu
FROM lasku l INNER JOIN asiakas a ON l.asiakasid = a.asiakasid
INNER JOIN tyokohde t ON l.kohdeid = t.kohdeid
WHERE tila = 'kesken'
AND edeltavaid NOT IN (SELECT laskuid FROM lasku WHERE edeltavaid IS NOT NULL)
ORDER BY laskuid

-- Hae karhulaskut (vähintään kaksi edeltävää laskua)
SELECT laskuid, a.enimi || ' ' || a.snimi AS nimi, t.osoite, luontipvm, erapvm, maksupvm, perintakulu
FROM lasku l INNER JOIN asiakas a ON l.asiakasid = a.asiakasid
INNER JOIN tyokohde t ON l.kohdeid = t.kohdeid
WHERE tila = 'kesken'
AND edeltavaid IN (SELECT laskuid FROM lasku WHERE edeltavaid IS NOT NULL)
ORDER BY laskuid