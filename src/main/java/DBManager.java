
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.*;

public class DBManager {

    private static final String AJURI = "org.postgresql.Driver";
    private static final String PROTOKOLLA = "jdbc:postgresql:";
    private static final String PALVELIN = "localhost";
    private static final int PORTTI = 5432;
    private static final String TIETOKANTA = "tiko_ht"; // tähän oma käyttäjätunnus
    private static final String KAYTTAJA = "tiko"; // tähän oma käyttäjätunnus
    private static final String SALASANA = "t1k0"; // tähän tietokannan salasana

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
                    + "WHERE enimi = ? OR snimi = ?"
                    + "ORDER BY asiakasid");
            pstmt.setString(1, nimi);
            pstmt.setString(2, nimi);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String asiakas = rs.getInt("asiakasid") + " " + rs.getString("enimi")
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
                    + "WHERE enimi = ? AND snimi = ?"
                    + "ORDER BY asiakasid");
            pstmt.setString(1, enimi);
            pstmt.setString(2, snimi);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String asiakas = rs.getInt("asiakasid") + " " + rs.getString("enimi")
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
                String asiakas = rs.getInt("asiakasid") + " " + rs.getString("enimi")
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

    public void lisaaKohde(String asiakasid, String tyyppi, String osoite, String eralkm)
            throws SQLException {
        System.out.println("luodaan uusi työkohde: " + asiakasid + tyyppi + osoite
                + eralkm);

        try {
            Statement stmt = con.createStatement();
            String update;

            update = "INSERT INTO tyokohde (asiakasid, tyyppi, osoite, eralkm)"
                    + " VALUES (%s, '%s', '%s', %s)";
            stmt.executeUpdate(String.format(update, asiakasid, tyyppi, osoite, eralkm));

            stmt.close();

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

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
