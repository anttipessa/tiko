
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class DBManager {

    private Connection con;

    private static final String PROTOKOLLA = "jdbc:postgresql:";
    private static final String PALVELIN = "localhost";
    private static final int PORTTI = 5432;
    private static final String TIETOKANTA = "tiko_ht";
    private static final String KAYTTAJA = "tiko";
    private static final String SALASANA = "t1k0";

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
        
        
        /*
        
        sshTunneli versio
        try {
            String strSshUser = "ab123456";  // SSH login username
            String strSshPassword = "pptunnussalasana";  // SSH login password
            String strSshHost = "linux-ssh.tuni.fi";  // hostname or ip or SSH server
            int nSshPort = 22;  // remote SSH host port number
            String strRemoteHost = "dbstud2.sis.uta.fi";  // hostname or ip of your database server
            int nLocalPort = 3306;  // local port number use to bind SSH tunnel
            int nRemotePort = 5432;  // remote port number of your database 
            String strDbUser = "ab123456";  // database login username
            String strDbPassword = "sqlsalasana";  // database login password

            doSshTunnel(strSshUser, strSshPassword, strSshHost, nSshPort, strRemoteHost, nLocalPort, nRemotePort);
            con = DriverManager.getConnection("jdbc:postgresql://localhost:"+nLocalPort+"/", strDbUser, strDbPassword);
            
        } catch( Exception e ) {
            e.printStackTrace();
            System.exit(0);
        } 
         */
    }

    // https://cryptofreek.org/2012/06/06/howto-jdbc-over-an-ssh-tunnel/
    private static void doSshTunnel(String strSshUser, String strSshPassword, String strSshHost, int nSshPort, String strRemoteHost, int nLocalPort, int nRemotePort) throws JSchException {
        final JSch jsch = new JSch();
        Session session = jsch.getSession(strSshUser, strSshHost, nSshPort);
        session.setPassword(strSshPassword);

        final Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        session.setPortForwardingL(nLocalPort, strRemoteHost, nRemotePort);
    }

    /**
     * Lisää uuden asiakkaan tietokantaan.
     *
     * @param enimi
     * @param snimi
     * @param osoite
     * @param puhelin
     * @param sahkoposti
     * @throws SQLException
     */
    public void lisaaAsiakas(String enimi, String snimi, String osoite,
            String puhelin, String sahkoposti) throws SQLException {
        System.out.println("luodaan uusi asiakas: " + enimi + snimi + osoite
                + puhelin + sahkoposti);

        try {
            con.setAutoCommit(false);
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
            con.commit();
            stmt.close();

        } catch (SQLException e) {
            con.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            con.setAutoCommit(true);
        }
    }

    /**
     * Lisää uuden asiakkaan tietokantaan.
     *
     * @param enimi
     * @param snimi
     * @param osoite
     * @param sahkoposti
     * @throws SQLException
     */
    public void lisaaAsiakas(String enimi, String snimi, String osoite,
            String sahkoposti) throws SQLException {
        System.out.println("luodaan uusi asiakas ilman puhelinnumeroa: "
                + enimi + snimi + osoite + sahkoposti);

        try {
            con.setAutoCommit(false);
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
            con.commit();
            stmt.close();

        } catch (SQLException e) {
            con.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            con.setAutoCommit(true);
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

    /**
     * Luo uuden työkohteen tietokantaan.
     *
     * @param asiakasid
     * @param tyyppi
     * @param tarjous
     * @param osoite
     * @param eralkm
     * @throws SQLException
     */
    public void lisaaKohde(String asiakasid, String tyyppi, String tarjous, String osoite, String eralkm)
            throws SQLException {
        System.out.println("luodaan uusi työkohde: " + asiakasid + tyyppi + osoite
                + eralkm);

        try {
            con.setAutoCommit(false);
            Statement stmt = con.createStatement();

            String update = "INSERT INTO tyokohde (asiakasid, tyyppi, tarjous, osoite, eralkm)"
                    + " VALUES (%s, '%s', %s, '%s', %s)";
            stmt.executeUpdate(String.format(update, asiakasid, tyyppi, tarjous, osoite, eralkm));
            con.commit();
            stmt.close();

        } catch (SQLException e) {
            con.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            con.setAutoCommit(true);
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

    /**
     * Asettaa tarjouksen hyväksytyksi eli muuttaa tarjous muuttujan.
     *
     * @param kohdeid
     * @throws SQLException
     */
    public void hyvaksyTarjous(String kohdeid) throws SQLException {
        try {
            con.setAutoCommit(false);
            Statement stmt = con.createStatement();
            String query = "UPDATE tyokohde SET tarjous = FALSE "
                    + "WHERE kohdeid = %s";
            stmt.executeUpdate(String.format(query, kohdeid));
            con.commit();
            stmt.close();
        } catch (SQLException e) {
            con.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            con.setAutoCommit(true);
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
            ResultSet rs = stmt.executeQuery(
                    "SELECT kohdeid, osoite "
                    + "FROM tyokohde "
                    + "WHERE kohdeid NOT IN (SELECT kohdeid FROM lasku) "
                    + "AND tarjous = false "
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
     * Hakee tietokannasta ne työkohteet, jotka ovat sopimusvaiheessa ja joiden
     * osoite sisältää parametrina annetun hakusanan.
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
                    + "WHERE LOWER(osoite) LIKE ? "
                    + "AND kohdeid NOT IN (SELECT kohdeid FROM lasku) AND tarjous = false "
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
     * Hakee kaikki työkohteet tietokannasta mistä ei ole vielä laskua.
     *
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeKohteetIlmanLaskua() throws SQLException {

        ArrayList<String> kohteet = new ArrayList<>();

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT kohdeid, osoite "
                    + "FROM tyokohde "
                    + "WHERE kohdeid NOT IN (SELECT kohdeid FROM lasku) "
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
     * Hakee kaikki annettua osoitetta vastaavat työkohteet tietokannasta mistä
     * ei ole vielä laskua.
     *
     * @param osoite
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeKohteetIlmanLaskua(String osoite) throws SQLException {

        ArrayList<String> kohteet = new ArrayList<>();

        try {
            PreparedStatement pstmt = con.prepareStatement(
                    "SELECT kohdeid, osoite "
                    + "FROM tyokohde "
                    + "WHERE LOWER(osoite) LIKE ? "
                    + "AND kohdeid NOT IN (SELECT kohdeid FROM lasku) "
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
     * Hakee asiakkaan nimen ja työkohteen osoitteen, jotka liittyvät
     * työkohteeseen jonka kohdeid vastaanotetaan parametrina.
     *
     * @param kohdeid
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeTarjousTiedot(String kohdeid) throws SQLException {
        ArrayList<String> tarjous = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT enimi || ' ' || snimi AS nimi, "
                    + "t.osoite, t.tyyppi, t.eralkm FROM asiakas a INNER JOIN tyokohde t "
                    + "ON a.asiakasid = t.asiakasid WHERE t.kohdeid = " + kohdeid);
            if (rs.next()) {
                tarjous.add(rs.getString("nimi"));
                tarjous.add(rs.getString("osoite"));
                tarjous.add(rs.getString("tyyppi"));
                tarjous.add(String.valueOf(rs.getInt("eralkm")));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }

        return tarjous;
    }

    /**
     * Lisää tarvikkeen tietokantaan annetuilla parameterilla. Jos annettua
     * nimi-parametria vastaava tarvike, jossa tila = 'käytössä' löytyy jo
     * tietokannasta, päivitetään sen tilaksi 'vanhentunut' ja lisätään sama
     * tarvike päivitetyillä tiedoilla. Jos kaikki tiedot vastaavat jo olemassa
     * olevaa riviä niin silloin päivitystä ei tehdä.
     *
     * @param nimi
     * @param yksikko
     * @param varastotil
     * @param ostohinta
     * @param kate
     * @param alv
     * @throws SQLException
     */
    public void lisaaTarvike(String nimi, String yksikko, double ostohinta, double kate, double alv) throws SQLException {
        try {
            con.setAutoCommit(false);
            Statement stmt = con.createStatement();
            String select, insert, update;
            select = "SELECT tarvikeid, nimi, yksikko, ostohinta, kate, alv "
                    + "FROM tarvike WHERE nimi = '%s' AND tila = 'käytössä'";
            ResultSet rs = stmt.executeQuery(String.format(select, nimi));
            if (rs.next()) {
                int tarvikeid = rs.getInt("tarvikeid");
                String n = rs.getString("nimi");
                String y = rs.getString("yksikko");
                double oh = rs.getDouble("ostohinta");
                double k = rs.getDouble("kate");
                double a = rs.getDouble("alv");
                // Kaikki tiedot eivät vastaa uuden lisättävän tarvikkeen kanssa
                if (!n.equals(nimi) || !y.equals(yksikko) || oh != ostohinta || k != kate || a != alv) {
                    // Lisätään tarvike päivitetyillä tiedoilla
                    insert = "INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, alv) "
                            + "VALUES ('%s', '%s', %s, %s, %s)";
                    stmt.executeUpdate(String.format(insert, nimi, yksikko, ostohinta, kate, alv));
                    // Päivitetään vanhan tarvikkeen tila
                    update = "UPDATE tarvike SET tila = 'vanhentunut' WHERE tarvikeid = " + tarvikeid;
                    stmt.executeUpdate(update);
                }
            } else {
                // Lisätään uusi tarvike
                insert = "INSERT INTO tarvike (nimi, yksikko, ostohinta, kate, alv) "
                        + "VALUES ('%s', '%s', %s, %s, %s)";
                stmt.executeUpdate(String.format(insert, nimi, yksikko, ostohinta, kate, alv));
            }
            con.commit();
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            con.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            con.setAutoCommit(true);
        }
    }

    /**
     * Hakee tietokannasta kaikki tarvikkeet jotka ovat käytössä.
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
                    + "WHERE tila = 'käytössä' "
                    + "ORDER BY tarvikeid");
            while (rs.next()) {
                String tarvike = rs.getInt("tarvikeid") + " - " + rs.getString("nimi") + " - " + rs.getDouble("ostohinta") + " €";
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
     * Hakee tietokannasta kaikki tarvikkeet.
     *
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeKaikkiTarvikkeet() throws SQLException {

        ArrayList<String> tarvikkeet = new ArrayList<>();

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT * FROM tarvike ORDER BY tarvikeid");
            while (rs.next()) {
                String tarvike = rs.getInt("tarvikeid") + "::" + rs.getString("nimi") + "::"
                        + rs.getString("yksikko") + "::" + rs.getDouble("ostohinta")
                        + "::" + rs.getDouble("kate") + "::" + rs.getDouble("alv")
                        + "::" + rs.getString("tila");

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
     * Hakee kaikki käytössä olevat tarvikkeet.
     *
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeKaytossaTarvikkeet() throws SQLException {

        ArrayList<String> tarvikkeet = new ArrayList<>();

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT * FROM tarvike WHERE tila = 'käytössä' ORDER BY tarvikeid");
            while (rs.next()) {
                String tarvike = rs.getInt("tarvikeid") + "::" + rs.getString("nimi") + "::"
                        + rs.getString("yksikko") + "::" + rs.getDouble("ostohinta")
                        + "::" + rs.getDouble("kate") + "::" + rs.getDouble("alv")
                        + "::" + rs.getString("tila");

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
     * Hakee kaikki poistetut/päivitetyt tarvikkeet.
     *
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haePoistetutTarvikkeet() throws SQLException {

        ArrayList<String> tarvikkeet = new ArrayList<>();

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT * FROM tarvike WHERE tila = 'vanhentunut' ORDER BY tarvikeid");
            while (rs.next()) {
                String tarvike = rs.getInt("tarvikeid") + "::" + rs.getString("nimi") + "::"
                        + rs.getString("yksikko") + "::" + rs.getDouble("ostohinta")
                        + "::" + rs.getDouble("kate") + "::" + rs.getDouble("alv")
                        + "::" + rs.getString("tila");

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
     * Hakee tietokannasta kaikki käytössä olevat tarvikkeet jotka vastaavat
     * parametrina annettua nimeä.
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
                    + "WHERE LOWER(nimi) LIKE ? AND tila = 'käytössä' "
                    + "ORDER BY tarvikeid");
            pstmt.setString(1, "%" + nimi + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String tarvike = rs.getInt("tarvikeid") + " - " + rs.getString("nimi") + " - " + rs.getDouble("ostohinta") + " €";
                tarvikkeet.add(tarvike);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
        return tarvikkeet;
    }

    /**
     * Hakee työkohteelle tehtävät tunnit.
     *
     * @param id
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeKohteenTunnit(String id) throws SQLException {
        ArrayList<String> tunnit = new ArrayList<>();

        try {
            Statement stmt = con.createStatement();
            String query = "SELECT tt.ttid, tt.nimi, te.lkm, tt.hinta, te.ale, tt.alv "
                    + "FROM tyokohde t INNER JOIN tehdaan te ON t.kohdeid = te.kohdeid "
                    + "INNER JOIN tuntityyppi tt ON te.ttid = tt.ttid "
                    + "WHERE t.kohdeid = %s "
                    + "ORDER BY tt.nimi";
            ResultSet rs = stmt.executeQuery(String.format(query, id));
            while (rs.next()) {
                String tuntityyppi = rs.getString("nimi") + "::" + rs.getDouble("lkm")
                        + "::" + rs.getDouble("hinta") + "::" + rs.getDouble("ale")
                        + "::" + rs.getInt("ttid") + "::" + rs.getDouble("alv");
                tunnit.add(tuntityyppi);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }

        return tunnit;
    }

    /**
     * Hakee työkohteelle lisätyt tarvikkeet.
     *
     * @param id
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeKohteenTarvikkeet(String id) throws SQLException {
        ArrayList<String> tarvikkeet = new ArrayList<>();

        try {
            Statement stmt = con.createStatement();
            String query = "SELECT ta.tarvikeid, ta.nimi, ta.yksikko, s.lkm, ta.ostohinta, "
                    + "s.ale, ta.kate, ta.alv "
                    + "FROM tyokohde t INNER JOIN sisaltaa s ON t.kohdeid = s.kohdeid "
                    + "INNER JOIN tarvike ta ON s.tarvikeid = ta.tarvikeid "
                    + "WHERE t.kohdeid = %s "
                    + "ORDER BY ta.nimi";
            ResultSet rs = stmt.executeQuery(String.format(query, id));
            while (rs.next()) {
                String tarvike = rs.getString("nimi") + "::" + rs.getString("yksikko")
                        + "::" + rs.getInt("lkm") + "::" + rs.getDouble("ostohinta")
                        + "::" + rs.getDouble("ale") + "::" + rs.getInt("tarvikeid")
                        + "::" + rs.getDouble("kate") + "::" + rs.getDouble("alv");
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
     * Lisää työkohteeseen tunteja tai tarvikkeita ensimmäisen parametrin
     * perusteella. Tapahtumanhallinta valvoo, että mikäli joku suoritettavista
     * lauseista epäonnistuu, niin kaikki tapahtumat perutaan.
     *
     * @param tarvike
     * @param kohdeid
     * @param nimi
     * @param lkm
     * @throws SQLException
     */
    public void lisaaKohteeseen(boolean tarvike, String kohdeid, String info, String lkm)
            throws SQLException {
        try {
            con.setAutoCommit(false);
            Statement stmt = con.createStatement();
            String insert;

            // Lisätään tuntityyppiä
            if (!tarvike) {
                ResultSet rs = stmt.executeQuery("SELECT ttid FROM tuntityyppi WHERE nimi = '" + info + "'");
                if (rs.next()) {
                    int ttid = rs.getInt("ttid");
                    rs.close();
                    rs = stmt.executeQuery("SELECT ttid FROM tehdaan WHERE ttid = "
                            + ttid + " AND kohdeid = " + kohdeid);
                    if (rs.next()) {
                        stmt.executeUpdate("UPDATE tehdaan SET lkm = lkm + " + lkm
                                + " WHERE ttid = " + ttid + " AND kohdeid = " + kohdeid);
                    } else {
                        insert = "INSERT INTO tehdaan (ttid, kohdeid, lkm) "
                                + "VALUES (%s, %s, %s)";
                        stmt.executeUpdate(String.format(insert, ttid, kohdeid, lkm));
                    }
                    con.commit();
                    rs.close();
                    stmt.close();
                } else {
                    throw new SQLException("ttid not found!");
                }
            } // Lisätään tarviketta
            else {
                ResultSet rs = stmt.executeQuery("SELECT tarvikeid FROM sisaltaa WHERE tarvikeid = "
                        + info + " AND kohdeid = " + kohdeid);
                if (rs.next()) {
                    stmt.executeUpdate("UPDATE sisaltaa SET lkm = lkm + " + lkm
                            + " WHERE tarvikeid = " + info + " AND kohdeid = " + kohdeid);
                } else {
                    insert = "INSERT INTO sisaltaa (kohdeid, tarvikeid, lkm) "
                            + "VALUES (%s, %s, %s)";
                    stmt.executeUpdate(String.format(insert, kohdeid, info, lkm));
                }
                con.commit();
                rs.close();
                stmt.close();
            }
        } catch (SQLException e) {
            con.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            con.setAutoCommit(true);
        }
    }

    /**
     * Poistaa työkohteesta tehtäviä tunteja. Hakee tuntityypin nimeä vastaavan
     * ttid:n tuntityyppi-taulusta ja sen avulla poistaa tehdaan-taulusta oikean
     * rivin.
     *
     * @param kohdeid työkohteen kohdeid
     * @param tt tuntityypin nimi
     * @throws SQLException
     */
    public void poistaKohteestaTunteja(String kohdeid, String ttid) throws SQLException {
        try {
            con.setAutoCommit(false);
            Statement stmt = con.createStatement();
            String delete = "DELETE FROM tehdaan WHERE kohdeid = %s AND ttid = %s";
            stmt.executeUpdate(String.format(delete, kohdeid, ttid));
            con.commit();
            stmt.close();
        } catch (SQLException e) {
            con.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            con.setAutoCommit(true);
        }
    }

    /**
     * Poistaa työkohteesta lisättyjä tarvikkeita.
     *
     * @param kohdeid työkohteen kohdeid
     * @param tarvike tarvikkeen nimi
     * @param lkm työkohteessa olevien tarvikkeiden lukumäärä
     * @throws SQLException
     */
    public void poistaKohteestaTarvikkeita(String kohdeid, String tarvikeid)
            throws SQLException {
        try {
            con.setAutoCommit(false);
            Statement stmt = con.createStatement();
            String delete = "DELETE FROM sisaltaa WHERE kohdeid = %s AND tarvikeid = %s";
            stmt.executeUpdate(String.format(delete, kohdeid, tarvikeid));
            con.commit();
            stmt.close();
        } catch (SQLException e) {
            con.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            con.setAutoCommit(true);
        }
    }

    public void lisaaTarvikeAlennus(String tarvikeid, String kohdeid, String ale) throws SQLException {
        try {
            con.setAutoCommit(false);
            Statement stmt = con.createStatement();
            String query = "UPDATE sisaltaa SET ale = %s  "
                    + "WHERE kohdeid = %s AND tarvikeid = %s";
            stmt.executeUpdate(String.format(query, ale, kohdeid, tarvikeid));
            con.commit();
            stmt.close();
        } catch (SQLException e) {
            con.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            con.setAutoCommit(true);
        }
    }

    /**
     * Päivittää tehdaan-taulun ale-sarakkeen arvon.
     *
     * @param ttid
     * @param kohdeid
     * @param ale
     * @throws SQLException
     */
    public void lisaaTuntiAlennus(String ttid, String kohdeid, String ale) throws SQLException {
        try {
            con.setAutoCommit(false);
            Statement stmt = con.createStatement();
            String query = "UPDATE tehdaan SET ale = %s  "
                    + "WHERE kohdeid = %s AND ttid = %s";
            stmt.executeUpdate(String.format(query, ale, kohdeid, ttid));
            con.commit();
            stmt.close();
        } catch (SQLException e) {
            con.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            con.setAutoCommit(true);
        }
    }

    /**
     * Hakee kaikki laskut joista eräpäivä on umpeutunut.
     *
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeLaskutErapvmUmpeutunut() throws SQLException {
        ArrayList<String> laskut = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            String query = "SELECT laskuid, a.enimi || ' ' || a.snimi AS nimi, t.osoite, "
                    + "luontipvm, erapvm, maksupvm, perintakulu, tila "
                    + "FROM lasku l INNER JOIN asiakas a ON l.asiakasid = a.asiakasid "
                    + "INNER JOIN tyokohde t ON l.kohdeid = t.kohdeid "
                    + "WHERE erapvm < CURRENT_DATE AND tila = 'kesken' "
                    + "ORDER BY laskuid";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String lasku = rs.getInt("laskuid") + "::" + rs.getString("nimi") + "::"
                        + rs.getString("osoite") + "::" + rs.getDate("luontipvm") + "::"
                        + rs.getDate("erapvm") + "::" + rs.getDate("maksupvm") + "::"
                        + rs.getDouble("perintakulu") + "::" + rs.getString("tila");
                laskut.add(lasku);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
        return laskut;
    }

    /**
     * Hakee tietokannasta kaikki laskut.
     *
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeKaikkiLaskut() throws SQLException {
        ArrayList<String> laskut = new ArrayList<>();

        try {
            Statement stmt = con.createStatement();
            String query = "SELECT laskuid, a.enimi || ' ' || a.snimi AS nimi, t.osoite, "
                    + "luontipvm, erapvm, maksupvm, perintakulu, tila "
                    + "FROM lasku l INNER JOIN asiakas a ON l.asiakasid = a.asiakasid "
                    + "INNER JOIN tyokohde t ON l.kohdeid = t.kohdeid "
                    + "ORDER BY laskuid";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String lasku = rs.getInt("laskuid") + "::" + rs.getString("nimi") + "::"
                        + rs.getString("osoite") + "::" + rs.getDate("luontipvm") + "::"
                        + rs.getDate("erapvm") + "::" + rs.getDate("maksupvm") + "::"
                        + rs.getDouble("perintakulu") + "::" + rs.getString("tila");
                laskut.add(lasku);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
        return laskut;
    }

    /**
     * Hakee tietokannasta kaikki laskut joita ei ole maksettu ja joista ei ole
     * lähetetty muistutuslaskuja.
     *
     * @return
     */
    public ArrayList<String> haeMaksamattomatLaskut() throws SQLException {
        ArrayList<String> laskut = new ArrayList<>();

        try {
            Statement stmt = con.createStatement();
            String query = "SELECT laskuid, a.enimi || ' ' || a.snimi AS nimi, t.osoite, "
                    + "luontipvm, erapvm, maksupvm, perintakulu, tila "
                    + "FROM lasku l INNER JOIN asiakas a ON l.asiakasid = a.asiakasid "
                    + "INNER JOIN tyokohde t ON l.kohdeid = t.kohdeid "
                    + "WHERE tila = 'kesken' "
                    + "ORDER BY laskuid";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String lasku = rs.getInt("laskuid") + "::" + rs.getString("nimi") + "::"
                        + rs.getString("osoite") + "::" + rs.getDate("luontipvm") + "::"
                        + rs.getDate("erapvm") + "::" + rs.getDate("maksupvm") + "::"
                        + rs.getDouble("perintakulu") + "::" + rs.getString("tila");
                laskut.add(lasku);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
        return laskut;
    }

    /**
     * Hakee tietokannasta kaikki maksamattomat muistutuslaskut (eli laskut
     * joita edeltää yksi aiempi lasku), joista ei ole lähetetty myöhempiä
     * karhulaskuja.
     *
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeMuistutuslaskut() throws SQLException {
        ArrayList<String> laskut = new ArrayList<>();

        try {
            Statement stmt = con.createStatement();
            String query = "SELECT laskuid, a.enimi || ' ' || a.snimi AS nimi, t.osoite, "
                    + "luontipvm, erapvm, maksupvm, perintakulu, tila "
                    + "FROM lasku l INNER JOIN asiakas a ON l.asiakasid = a.asiakasid "
                    + "INNER JOIN tyokohde t ON l.kohdeid = t.kohdeid "
                    + "WHERE tila = 'kesken' "
                    + "AND edeltavaid NOT IN (SELECT laskuid FROM lasku WHERE edeltavaid IS NOT NULL) "
                    + "ORDER BY laskuid";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String lasku = rs.getInt("laskuid") + "::" + rs.getString("nimi") + "::"
                        + rs.getString("osoite") + "::" + rs.getDate("luontipvm") + "::"
                        + rs.getDate("erapvm") + "::" + rs.getDate("maksupvm") + "::"
                        + rs.getDouble("perintakulu") + "::" + rs.getString("tila");
                laskut.add(lasku);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
        return laskut;
    }

    /**
     * Hakee tietokannasta kaikki maksamattomat karhulaskut (eli laskut joita
     * edeltää vähintään kaksi aiempaa laskua), joista ei ole lähetetty
     * myöhempiä karhulaskuja.
     *
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeKarhulaskut() throws SQLException {
        ArrayList<String> laskut = new ArrayList<>();

        try {
            Statement stmt = con.createStatement();
            String query = "SELECT laskuid, a.enimi || ' ' || a.snimi AS nimi, t.osoite, "
                    + "luontipvm, erapvm, maksupvm, perintakulu, tila "
                    + "FROM lasku l INNER JOIN asiakas a ON l.asiakasid = a.asiakasid "
                    + "INNER JOIN tyokohde t ON l.kohdeid = t.kohdeid "
                    + "WHERE tila = 'kesken' "
                    + "AND edeltavaid IN (SELECT laskuid FROM lasku WHERE edeltavaid IS NOT NULL) "
                    + "ORDER BY laskuid";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String lasku = rs.getInt("laskuid") + "::" + rs.getString("nimi") + "::"
                        + rs.getString("osoite") + "::" + rs.getDate("luontipvm") + "::"
                        + rs.getDate("erapvm") + "::" + rs.getDate("maksupvm") + "::"
                        + rs.getDouble("perintakulu") + "::" + rs.getString("tila");
                laskut.add(lasku);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
        return laskut;
    }

    /**
     * Hakee tietokannasta kaikki laskut, jotka on maksettu.
     *
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeMaksetutLaskut() throws SQLException {
        ArrayList<String> laskut = new ArrayList<>();

        try {
            Statement stmt = con.createStatement();
            String query = "SELECT laskuid, a.enimi || ' ' || a.snimi AS nimi, t.osoite, "
                    + "luontipvm, erapvm, maksupvm, perintakulu, tila "
                    + "FROM lasku l INNER JOIN asiakas a ON l.asiakasid = a.asiakasid "
                    + "INNER JOIN tyokohde t ON l.kohdeid = t.kohdeid "
                    + "WHERE tila = 'valmis' "
                    + "ORDER BY laskuid";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String lasku = rs.getInt("laskuid") + "::" + rs.getString("nimi") + "::"
                        + rs.getString("osoite") + "::" + rs.getDate("luontipvm") + "::"
                        + rs.getDate("erapvm") + "::" + rs.getDate("maksupvm") + "::"
                        + rs.getDouble("perintakulu") + "::" + rs.getString("tila");
                laskut.add(lasku);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
        return laskut;
    }

    /**
     * Palauttaa tiedon kuinka monessa erässä kohde laskutetaan.
     *
     * @param kohdeid
     * @return
     * @throws SQLException
     */
    public int haeErat(String kohdeid) throws SQLException {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT eralkm FROM tyokohde "
                    + "where kohdeid = " + kohdeid);
            int erat = 0;
            if (rs.next()) {
                erat = Integer.parseInt(rs.getString("eralkm"));
            }
            stmt.close();
            return erat;
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * Luo kohteesta laskun tietokantaan.
     *
     * @param asiakasid
     * @param kohdeid
     * @throws SQLException
     */
    public void luoLasku(String kohdeid, int eralkm) throws SQLException { //, IllegalArgumentException {
        try {
            /*
            if(eralkm != 1 && eralkm != 2) {
                throw new IllegalArgumentException("väärä erälukumäärä syötetty");
            }
             */
            con.setAutoCommit(false);
            Statement stmt = con.createStatement();
            String update;
            update = "INSERT INTO lasku (asiakasid, kohdeid) "
                    + "VALUES ((SELECT a.asiakasid FROM asiakas as a LEFT JOIN "
                    + "tyokohde as t ON a.asiakasid = t.asiakasid WHERE kohdeid = %s), %s)";
            stmt.executeUpdate(String.format(update, kohdeid, kohdeid));

            if (eralkm == 2) {
                int vuosi = LocalDate.now().plusYears(1).getYear();
                update = "INSERT INTO lasku (asiakasid, kohdeid, luontipvm, erapvm) "
                        + "VALUES ((SELECT a.asiakasid FROM asiakas as a LEFT JOIN "
                        + "tyokohde as t ON a.asiakasid = t.asiakasid WHERE kohdeid = %s), %s,"
                        + "'%d-01-01', '%d-01-28')";
                stmt.executeUpdate(String.format(update, kohdeid, kohdeid, vuosi, vuosi));
            }
            con.commit();
            stmt.close();
        } catch (SQLException e) {
            con.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            con.setAutoCommit(true);
        }
    }

    /**
     * Päivitetään laskun tila muotoon 'valmis' eli lasku maksetuksi.
     * Vastaanottaa parametrina päivitettävän laskun laskuid sarakkeen.
     *
     * @param laskuid
     * @return
     * @throws SQLException
     */
    public String laskuMaksettu(String laskuid) throws SQLException {
        try {
            String laskuTiedot = "";
            con.setAutoCommit(false);
            Statement stmt = con.createStatement();
            stmt.executeUpdate("UPDATE lasku SET maksupvm = CURRENT_DATE, tila = 'valmis' "
                    + "WHERE laskuid = " + laskuid);
            ResultSet rs = stmt.executeQuery("SELECT maksupvm, tila FROM lasku WHERE laskuid = " + laskuid);
            if (rs.next()) {
                laskuTiedot = rs.getString("maksupvm") + "::" + rs.getString("tila");
            }
            con.commit();
            rs.close();
            stmt.close();
            return laskuTiedot;
        } catch (SQLException e) {
            con.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            con.setAutoCommit(true);
        }
    }

    /**
     * Lähettää muistutuslaskun. Haetaan ensin parametrina vastaanotettua
     * laskuid:tä vastaavan laskun tiedot, jonka jälkeen luodaan uusi lasku
     * jossa edeltavaid = parametrina saatu laskuid. Tämän jälkeen asetetaan
     * vanhan laskun tilaksi 'siirtynyt'. Käytetään tapahtumanhallintaa, jotta
     * muutokset menevät läpi loogisena kokonaisuutena, tai jos joku menee
     * pieleen niin peruutetaan alkuperäiseen tilaan.
     *
     * @param laskuid
     * @throws SQLException
     */
    public void lahetaMuistutuslasku(String laskuid) throws SQLException {
        try {
            con.setAutoCommit(false);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT asiakasid, kohdeid, COALESCE(perintakulu, 0) "
                    + "AS perintakulu FROM lasku WHERE laskuid = " + laskuid);
            if (rs.next()) {
                int asiakasid = rs.getInt("asiakasid");
                int kohdeid = rs.getInt("kohdeid");
                double lisamaksu = rs.getDouble("perintakulu");
                rs.close();
                String insert = "INSERT INTO lasku (asiakasid, kohdeid, edeltavaid, perintakulu, tila) "
                        + "VALUES (%s, %s, %s, %s + 5, 'kesken')";
                stmt.executeUpdate(String.format(insert, asiakasid, kohdeid, laskuid, lisamaksu));
                stmt.executeUpdate("UPDATE lasku SET tila = 'siirtynyt' WHERE laskuid = "
                        + laskuid);
                con.commit();
                stmt.close();
            }
        } catch (SQLException e) {
            con.rollback();
            throw new SQLException(e.getMessage());
        } finally {
            con.setAutoCommit(true);
        }
    }

    /**
     * Hakee laskun erittelyyn tarvittavat tiedot tietokannasta. Lasku haetaan
     * parametrina saatua laskuid arvoa käyttäen.
     *
     * @param laskuid
     * @return
     * @throws SQLException
     */
    public ArrayList<String> haeLaskuErittely(String laskuid) throws SQLException {
        ArrayList<String> eriteltavat = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT a.enimi || ' ' || a.snimi AS nimi, "
                    + "l.kohdeid, l.luontipvm, l.erapvm, l.perintakulu, t.tyyppi, t.osoite, t.eralkm "
                    + "FROM lasku l INNER JOIN asiakas a ON l.asiakasid = a.asiakasid "
                    + "INNER JOIN tyokohde t ON l.kohdeid = t.kohdeid "
                    + "WHERE laskuid = " + laskuid);
            if (rs.next()) {
                eriteltavat.add(String.valueOf(rs.getInt("kohdeid")));
                eriteltavat.add(rs.getString("nimi"));
                eriteltavat.add(rs.getString("tyyppi"));
                eriteltavat.add(rs.getString("osoite"));
                eriteltavat.add(rs.getString("luontipvm"));
                eriteltavat.add(rs.getString("erapvm"));
                eriteltavat.add(String.valueOf(rs.getDouble("perintakulu")));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }

        return eriteltavat;
    }

    public String haeEraluku(String kohdeid, String laskuid) throws SQLException {
        try {
            String eraluku = "";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT laskuid FROM lasku "
                    + "WHERE edeltavaid IS null AND kohdeid = " + kohdeid
                    + "ORDER BY luontipvm ASC LIMIT 1;");
            while (rs.next()) {
                eraluku += String.valueOf(rs.getString("laskuid")).equals(laskuid) ? "1" : "2";
            }

            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT COUNT(laskuid) FROM lasku "
                    + "WHERE edeltavaid IS null AND kohdeid = " + kohdeid + ";");
            while (rs.next()) {
                eraluku += "/" + rs.getString("count");
            }
            rs.close();
            stmt.close();
            return eraluku;
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public String haeAiempi(String laskuid) throws SQLException {
        try {
            String aiempi = "";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT edeltavaid FROM lasku "
                    + "WHERE laskuid = " + laskuid);
            while (rs.next()) {
                aiempi = rs.getString("edeltavaid");
            }
            rs.close();
            stmt.close();
            return aiempi;
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * Lukee tekstitiedoston rivi kerrallaan ja yrittää lisätä tarvikkeen.
     *
     * @param file
     */
    public void update(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                String nimi = line.split(";")[0];
                String yksikko = line.split(";")[1];
                double hinta = Double.parseDouble(line.split(";")[2]);
                double kate = Double.parseDouble(line.split(";")[3]);
                double alv = Double.parseDouble(line.split(";")[4]);
                lisaaTarvike(nimi, yksikko, hinta, kate, alv);
            }
        } catch (Exception e) {
            System.out.println("Tarvikelistauksen päivitysoperaatio räjähti.");
        }
    }

    /**
     * Sulkee yhteyden tietokantaan kun ohjelma suljetaan.
     */
    public void close() {
        try {
            System.out.println("Suljetaan yhteydet..");
            con.close();
        } catch (SQLException e) {
            System.out.println("Yhteyden sulkeminen epäonnistui..");
        }
    }
}
