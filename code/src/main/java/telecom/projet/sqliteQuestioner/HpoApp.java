package telecom.projet.sqliteQuestioner;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class HpoApp {

    /**
     * Connect to the test.db database
     * @return the Connection object
     */

    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:data/HPO/hpo_annotations.sqlite";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    
    //function that returns what is inside  the sql query
    public void selectAll(){
        String sql = "SELECT sign_id, disease_label FROM phenotype_annotation WHERE disease_label = \"Sotos syndrome\" ";
        
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString("sign_id") +  "\t" + 
                                   rs.getString("disease_label"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

      /**
     * Get the desease label from a HP_id
     * @param HP_id
     */
    public void getDiseaseLabelFromHP_id(String HP_id){
               String sql = "SELECT disease_label "
                          + "FROM phenotype_annotation "
                          +  "WHERE sign_id = ?";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){
            
            // setting the value of the parameter
            pstmt.setString(1,HP_id);
            //
            ResultSet rs  = pstmt.executeQuery();
            
            //getting the results
            while (rs.next()) {
                System.out.println(rs.getString("disease_label"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //Function that return the names of all tables
    public void listTables() {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name";
        
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {
            
            // Loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
   
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        HpoApp app = new HpoApp();
        //app.listTables();
        //app.selectAll();
        app.getDiseaseLabelFromHP_id("HP:0010754");
    }

}