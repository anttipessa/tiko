# Tietokantaohjelmointi 2020


## Docker-ympäristön pystyttäminen
Projektin juurikansiossa suoritetaan komento:

`docker-compose up -d`

Tämä pystyttää kontin ja käynnistää palvelut (PostgreSQL-tietokanta + adminer web-käyttöliittymätyökalu)

Komennolla `docker-compose logs -f` voi katsoa kontissa tapahtuvia lokeja. Lokinäkymästä pääsee takaisin painamalla `Ctrl + C`.

Kun kontti on pystyssä, pääset sisällä olevaan Postgres-tietokantaan antamalla seuraavan komennon:

`docker exec -it tiko_db_1 psql -U tiko -W tiko_ht`

Ohjelma pyytää salasanaa johon kirjoitetaan `t1k0` ja painetaan Enter.

Nyt olet sisällä PostgreSQL tietokannassa ja voit antaa SQL-lauseita normaalisti. Listaa esim. tietokannassa olevat taulut komennolla `\dt`

Kun haluat lopettaa, poistu kontin sisältä komennolla `exit`

Lopuksi sammutetaan ajossa olevat kontit komennolla:

`docker-compose down`

<hr>

Vaihtoehtoisesti voit navigoida selaimella osoitteeseen: `http://localhost:8080` josta löytyy Adminer-työkalu.

Sisäänkirjautuminen tapahtuu seuraavilla tiedoilla:

```
Järjestelmä: PostgreSQL
Palvelin: db
Käyttäjänimi: tiko
Salasana: t1k0
Tietokanta: tiko_ht
```

## Docker Toolbox

Avaa komentokehote admininia ja suorita komento:

`  docker-machine create --virtualbox-disk-size 40000 --virtualbox-memory 4096 default`

Myöhemmillä kerroilla riittää komento:

`docker-machine start default`

Sitten:

`docker-machine ssh default`

Nyt voi jatkaa Docker-ympäristön pystyttäminen kohdan mukaisesti.


## Docker-tiedostot

### .dockerfiles/db

Tietokantaympäristön määritykset.

### .dockerfiles/init_db.sql

Tietokannan alustuksessa käytettävät SQL-komennot. Luodaan tarvittavat taulut ja lisätään hieman rivejä jokaiseen tauluun. Tänne voi laittaa lisää INSERT-lauseita jos haluaa, että tietokannassa on heti alussa enemmän tavaraa.

### docker-compose.yml

Tässä tiedostossa määritetään Dockerin käyttämät palvelut, verkot ja voluumit.

Palvelut:
- db
  - Avaa PostgreSQL-tietokannan ja ohjaa sen porttiin 5432.
- adminer
  - Avaa adminer-webkäyttöliittymätyökalun porttiin 8080.
