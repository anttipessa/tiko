
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


public class DBManager {
    /*
    // private static final String AJURI = "org.postgresql.Driver";
    private static final String PROTOKOLLA = "jdbc:postgresql:";
    private static final String PALVELIN = "localhost";
    private static final int PORTTI = 5432;
    private static final String TIETOKANTA = "tiko_ht"; // tähän oma käyttäjätunnus
    private static final String KAYTTAJA = "tiko"; // tähän oma käyttäjätunnus
    private static final String SALASANA = "t1k0"; // tähän tietokannan salasana
    */
    
    public DBManager() {
        
    }
    
    public void lisaaAsiakas (String enimi, String snimi, String osoite, 
            int puhelin, String sahkoposti) {
        System.out.println("luodaan uusi asiakas: " + enimi + snimi + osoite + 
                puhelin + sahkoposti);
    }
    
    public void lisaaAsiakas (String enimi, String snimi, String osoite, 
            String sahkoposti) {
        System.out.println("luodaan uusi asiakas ilman puhelinnumeroa: " + 
                enimi + snimi + osoite + sahkoposti);
    }
    
    
    
    
    
    
    
    
    
    public void update (File file) {
        try {
            BufferedReader br = new BufferedReader( new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println("Tarvikelistauksen p�ivitysoperaatio r�j�hti.");
        }
    }
    
    public void close() {
        System.out.println("suljetaan yhteydet");
        // close();
    }
}
