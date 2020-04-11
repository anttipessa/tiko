
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.*;

public class DBManager {

    private static final String PROTOKOLLA = "jdbc:postgresql:";
    private static final String PALVELIN = "localhost";
    private static final int PORTTI = 5432;
    private static final String TIETOKANTA = "tiko_ht";
    private static final String KAYTTAJA = "tiko";
    private static final String SALASANA = "t1k0";

    private Connection con;

    /**
     * Alustaa tietokantayhteyden DBManager-oliolle.
     *
     * Jos tietokantayhteyden avaaminen epäonnistuu, keskeytetään ohjelman
     * suoritus.
     */
    public DBManager() {
        try {
            con = DriverManager.getConnection(PROTOKOLLA + "//" + PALVELIN + ":"
                    + PORTTI + "/" + TIETOKANTA, KAYTTAJA, SALASANA);

            System.out.println("Tietokantayhteys avattu!");

        } catch (SQLException poikkeus) {
            System.out.println(con);
            System.out.println("Yhteyden avaaminen tietokantaan epäonnistui: "
                    + poikkeus.getMessage());
            // Lopetetaan ohjelman suoritus, jos tietokantayhteys epäonnistuu
            System.exit(0);
        }
    }

    public void lisaaAsiakas(String enimi, String snimi, String osoite,
            String puhelin, String sahkoposti) throws SQLException {
        System.out.println("luodaan uusi asiakas: " + enimi + snimi + osoite
                + puhelin + sahkoposti);

        try {
            Statement stmt = con.createStatement();
            String update;
            if (sahkoposti.length() == 0) {
                update = "INSERT INTO asiakas (enimi, snimi, osoite, puhelin, sposti)"
                        + " VALUES ('%s', '%s', '%s', '%s', NULL)";
                stmt.executeUpdate(String.format(update, enimi, snimi, osoite, puhelin));
            } else {
                update = "INSERT INTO asiakas (enimi, snimi, osoite, puhelin, sposti)"
                        + " VALUES ('%s', '%s', '%s', '%s', '%s')";
                stmt.executeUpdate(String.format(update, enimi, snimi, osoite, puhelin, sahkoposti));
            }
            stmt.close();

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public void lisaaAsiakas(String enimi, String snimi, String osoite,
            String sahkoposti) throws SQLException {
        System.out.println("luodaan uusi asiakas ilman puhelinnumeroa: "
                + enimi + snimi + osoite + sahkoposti);

        try {
            Statement stmt = con.createStatement();
            String update;

            if (sahkoposti.length() == 0) {
                update = "INSERT INTO asiakas (enimi, snimi, osoite, sposti)"
                        + " VALUES ('%s', '%s', '%s', NULL)";
                stmt.executeUpdate(String.format(update, enimi, snimi, osoite));
            } else {
                update = "INSERT INTO asiakas (enimi, snimi, osoite, sposti)"
                        + " VALUES ('%s', '%s', '%s', '%s')";
                stmt.executeUpdate(String.format(update, enimi, snimi, osoite, sahkoposti));
            }

            stmt.close();

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * Hakee asiakkaat jossa etunimi tai sukunimi täsmäävät hakusanoihin.
     *
     * @param nimi
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeAsiakkaat(String nimi) throws SQLException {
        ArrayList<String> asiakkaat = new ArrayList<>();

        try {
            PreparedStatement pstmt = con.prepareStatement(
                    "SELECT asiakasid, enimi, snimi "
                    + "FROM asiakas "
                    + "WHERE LOWER(enimi) = ? OR LOWER(snimi) = ?"
                    + "ORDER BY asiakasid");
            pstmt.setString(1, nimi);
            pstmt.setString(2, nimi);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String asiakas = rs.getInt("asiakasid") + " - " + rs.getString("enimi")
                        + " " + rs.getString("snimi");
                asiakkaat.add(asiakas);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }

        return asiakkaat;
    }

    /**
     * Hakee asiakkaat jossa etunimi ja sukunimi täsmäävät hakusanoihin.
     *
     * @param enimi
     * @param snimi
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeAsiakkaat(String enimi, String snimi) throws SQLException {
        ArrayList<String> asiakkaat = new ArrayList<>();

        try {
            PreparedStatement pstmt = con.prepareStatement(
                    "SELECT asiakasid, enimi, snimi "
                    + "FROM asiakas "
                    + "WHERE LOWER(enimi) = ? AND LOWER(snimi) = ?"
                    + "ORDER BY asiakasid");
            pstmt.setString(1, enimi);
            pstmt.setString(2, snimi);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String asiakas = rs.getInt("asiakasid") + " - " + rs.getString("enimi")
                        + " " + rs.getString("snimi");
                asiakkaat.add(asiakas);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }

        return asiakkaat;
    }

    /**
     * Hakee kaikki asiakkaat tietokannasta.
     *
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeAsiakkaat() throws SQLException {
        ArrayList<String> asiakkaat = new ArrayList<>();

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT asiakasid, enimi, snimi "
                    + "FROM asiakas "
                    + "ORDER BY asiakasid");
            while (rs.next()) {
                String asiakas = rs.getInt("asiakasid") + " - " + rs.getString("enimi")
                        + " " + rs.getString("snimi");
                asiakkaat.add(asiakas);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }

        return asiakkaat;
    }

    public void lisaaKohde(String asiakasid, String tyyppi, String tarjous, String osoite, String eralkm)
            throws SQLException {
        System.out.println("luodaan uusi työkohde: " + asiakasid + tyyppi + osoite
                + eralkm);

        try {
            Statement stmt = con.createStatement();

            String update = "INSERT INTO tyokohde (asiakasid, tyyppi, tarjous, osoite, eralkm)"
                    + " VALUES (%s, '%s', %s, '%s', %s)";
            stmt.executeUpdate(String.format(update, asiakasid, tyyppi, tarjous, osoite, eralkm));

            stmt.close();

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * Hakee tietokannasta kaikki työkohteet jotka ovat tarjousvaiheessa.
     *
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeTarjoukset() throws SQLException {
        ArrayList<String> tarjoukset = new ArrayList<>();

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT kohdeid, osoite "
                    + "FROM tyokohde "
                    + "WHERE tarjous "
                    + "ORDER BY kohdeid");
            while (rs.next()) {
                String tyokohde = rs.getInt("kohdeid") + " - " + rs.getString("osoite");
                tarjoukset.add(tyokohde);
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }

        return tarjoukset;
    }

    /**
     * Hakee tietokannasta ne työkohteet, jotka ovat tarjousvaiheessa ja joiden
     * osoite sisältää parametrina annetun hakusanan.
     *
     * @param osoite
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeTarjoukset(String osoite) throws SQLException {
        ArrayList<String> tarjoukset = new ArrayList<>();

        try {
            PreparedStatement pstmt = con.prepareStatement(
                    "SELECT kohdeid, osoite "
                    + "FROM tyokohde "
                    + "WHERE LOWER(osoite) LIKE ? AND tarjous "
                    + "ORDER BY kohdeid");
            pstmt.setString(1, "%" + osoite + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String tyokohde = rs.getInt("kohdeid") + " - " + rs.getString("osoite");
                tarjoukset.add(tyokohde);
            }
            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }

        return tarjoukset;
    }

    public void hyvaksyTarjous(String kohdeid) throws SQLException {
        try {
            Statement stmt = con.createStatement();
            String query = "UPDATE tyokohde SET tarjous = FALSE "
                    + "WHERE kohdeid = %s";
            stmt.executeUpdate(String.format(query, kohdeid));
            System.out.println("Päivitys ok.");
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * Hakee kaikki kohteet joista on sopimus.
     *
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeKohteet() throws SQLException {

        ArrayList<String> kohteet = new ArrayList<>();

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT kohdeid, osoite "
                    + "FROM tyokohde "
                    + "WHERE tarjous = false "
                    + "ORDER BY kohdeid");
            while (rs.next()) {
                String tyokohde = rs.getInt("kohdeid") + " - " + rs.getString("osoite");
                kohteet.add(tyokohde);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
        return kohteet;
    }

    /**
     * Hakee tietokannasta ne ty�kohteet, jotka ovat sopimusvaiheessa ja joiden
     * osoite sis�lt�� parametrina annetun hakusanan.
     *
     * @param osoite
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeKohteet(String osoite) throws SQLException {

        ArrayList<String> kohteet = new ArrayList<>();

        try {
            PreparedStatement pstmt = con.prepareStatement(
                    "SELECT kohdeid, osoite "
                    + "FROM tyokohde "
                    + "WHERE LOWER(osoite) LIKE ? AND tarjous = false "
                    + "ORDER BY kohdeid");
            pstmt.setString(1, "%" + osoite + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String tyokohde = rs.getInt("kohdeid") + " - " + rs.getString("osoite");
                kohteet.add(tyokohde);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
        return kohteet;
    }

    /**
     * Hakee kaikki ty�kohteet tietokannasta mist� ei ole viel� laskua.
     *
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeKohteetIlmanLaskua() throws SQLException {

        ArrayList<String> kohteet = new ArrayList<>();

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT tyokohde.kohdeid, osoite "
                    + "FROM tyokohde FULL JOIN lasku ON tyokohde.kohdeid = lasku.kohdeid "
                    + "WHERE laskuid IS NULL "
                    + "ORDER BY kohdeid");
            while (rs.next()) {
                String tyokohde = rs.getInt("kohdeid") + " - " + rs.getString("osoite");
                kohteet.add(tyokohde);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
        return kohteet;
    }

    /**
     * Hakee kaikki annettua osoitetta vastaavat ty�kohteet tietokannasta mist�
     * ei ole viel� laskua.
     *
     * @param osoite
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeKohteetIlmanLaskua(String osoite) throws SQLException {

        ArrayList<String> kohteet = new ArrayList<>();

        try {
            PreparedStatement pstmt = con.prepareStatement(
                    "SELECT tyokohde.kohdeid, osoite "
                    + "FROM tyokohde FULL JOIN lasku ON tyokohde.kohdeid = lasku.kohdeid "
                    + "WHERE LOWER(osoite) LIKE ? AND laskuid IS NULL "
                    + "ORDER BY kohdeid");
            pstmt.setString(1, "%" + osoite + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String tyokohde = rs.getInt("kohdeid") + " - " + rs.getString("osoite");
                kohteet.add(tyokohde);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
        return kohteet;
    }

    /**
     * Lis�� tarvikkeen tietokantaan annetuilla parameterill�.
     *
     * @param nimi
     * @param yksikko
     * @param varastotil
     * @param ostohinta
     * @param kate
     * @param alv
     * @throws SQLException
     */
    public void lisaaTarvike(String nimi, String yksikko, int varastotil, double ostohinta, double kate, double alv) throws SQLException {
        try {
            Statement stmt = con.createStatement();
            String update;
            update = "INSERT INTO tarvike (nimi, yksikko, varastotilanne, ostohinta, kate, alv)"
                    + " VALUES ('%s', '%s', '%s', '%s','%s', '%s')";
            stmt.executeUpdate(String.format(update, nimi, yksikko, varastotil, ostohinta, kate, alv));
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * Hakee tietokannasta kaikki tarvikkeet jotka ovat k�yt�ss�.
     *
     * @return tarvikkeet
     * @throws SQLException
     */
    public ArrayList<String> haeTarvikkeet() throws SQLException {

        ArrayList<String> tarvikkeet = new ArrayList<>();

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * "
                    + "FROM tarvike "
                    + "WHERE tila = 'kaytossa' "
                    + "ORDER BY tarvikeid");
            while (rs.next()) {
                String tarvike = rs.getString("nimi") + " - " + rs.getDouble("ostohinta") + " �";
                tarvikkeet.add(tarvike);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
        return tarvikkeet;
    }

    /**
     * Hakee tietokannasta kaikki k�yt�ss� olevat tarvikkeet jotka vastaavat parametrina
     * annettua nime�.
     *
     * @param nimi
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeTarvikkeet(String nimi) throws SQLException {

        ArrayList<String> tarvikkeet = new ArrayList<>();

        try {
            PreparedStatement pstmt = con.prepareStatement(
                    "SELECT * "
                    + "FROM tarvike "
                    + "WHERE LOWER(nimi) LIKE ? AND tila = 'kaytossa' "
                    + "ORDER BY tarvikeid");
            pstmt.setString(1, "%" + nimi + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String tarvike = rs.getString("nimi") + " - " + rs.getDouble("ostohinta") + " �";
                tarvikkeet.add(tarvike);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
        return tarvikkeet;
    }

    public void update(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println("Tarvikelistauksen päivitysoperaatio räjähti.");
        }
    }

    public void close() {
        try {
            System.out.println("Suljetaan yhteydet..");
            con.close();
        } catch (SQLException e) {
            System.out.println("Yhteyden sulkeminen epäonnistui..");
        }
    }
}
