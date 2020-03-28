-- Test select queries

SELECT tuntityyppi.nimi, SUM((tuntityyppi.hinta * 1.24)*tehdaan.lkm), SUM(((tuntityyppi.hinta * 1.24)*tehdaan.lkm) *(1 - (1 / 1.24))), laskuid
FROM tuntityyppi LEFT JOIN tehdaan ON tuntityyppi.ttid = tehdaan.ttid LEFT JOIN tyokohde ON tehdaan.kohdeid = tyokohde.kohdeid LEFT JOIN lasku ON tyokohde.kohdeid = lasku.kohdeid
WHERE lasku.kohdeid = 1
GROUP BY tuntityyppi.nimi, laskuid;