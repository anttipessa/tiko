public class Tiko2020 {
    public static void main(String[] args) {
        System.out.println("Tietokantaohjelmointi 2020");
        
        DBManager dbmanager = new DBManager();
        
        GUI gui = new GUI(dbmanager);
        gui.setVisible(true);
    }
}
