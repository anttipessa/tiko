
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;

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
     * Jos tietokantayhteyden avaaminen epäonnistuu, keskeytetään
     * ohjelman suoritus.
     */
    public DBManager() {
        try {
            con = DriverManager.getConnection(PROTOKOLLA + "//" + PALVELIN + ":"
                    + PORTTI + "/" + TIETOKANTA, KAYTTAJA, SALASANA);

            System.out.println("Tietokantayhteys avattu!");
            
            /*
            Statement stmt = con.createStatement();
            ResultSet rset = stmt.executeQuery("SELECT 1+1");
            if (rset.next()) {
                System.out.println("Löytyi luku: " + rset.getInt(1));
            } else {
                System.out.println("Ei löytynyt mitään!");
            }
            stmt.close();
            */
            
        } catch (SQLException poikkeus) {
            System.out.println("Yhteyden avaaminen tietokantaan epäonnistui: "
                    + poikkeus.getMessage());
            // Lopetetaan ohjelman suoritus, jos tietokantayhteys epäonnistuu
            System.exit(0);
        }
    }


    public void lisaaAsiakas (String enimi, String snimi, String osoite, 
                              String puhelin, String sahkoposti) throws SQLException {
        System.out.println("luodaan uusi asiakas: " + enimi + snimi + osoite + 
                puhelin + sahkoposti);

        try {
            Statement stmt = con.createStatement();
            String update;
            if (sahkoposti.length() == 0) {
                update = "INSERT INTO asiakas (enimi, snimi, osoite, puhelin, sposti)"
                    + " VALUES ('%s', '%s', '%s', %s, NULL)";
                stmt.executeUpdate(String.format(update, enimi, snimi, osoite, puhelin));
            } else {
                update = "INSERT INTO asiakas (enimi, snimi, osoite, puhelin, sposti)"
                    + " VALUES ('%s', '%s', '%s', %s, '%s')";
                stmt.executeUpdate(String.format(update, enimi, snimi, osoite, puhelin, sahkoposti));
            }
            stmt.close();
            
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }
    
    public void lisaaAsiakas (String enimi, String snimi, String osoite, 
                              String sahkoposti) throws SQLException {
        System.out.println("luodaan uusi asiakas ilman puhelinnumeroa: " + 
                enimi + snimi + osoite + sahkoposti);
        
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
    
    
    
    public void update (File file) {
        try {
            BufferedReader br = new BufferedReader( new FileReader(file));
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
