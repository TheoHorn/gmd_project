package telecom.projet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class Controller {
    @FXML private Label label; // Label à mettre à jour

    @FXML private Label query;

    @FXML private CheckBox checkbox;

    @FXML private TextField symptoms;

    @FXML private VBox results;

    //private Data data; // Instance pour les données

    public Controller() {}

    @FXML
    public void search() {

        results.getChildren().clear();
        
        if (symptoms.getText().isEmpty()){
            query.setText("Please enter a symptom");
        }else{
            //display of the results
            query.setText("Symptoms: " + symptoms.getText());
        
            
            results.getChildren().add(new Label("Possible diseases:"));
            //possible diseases
            ObservableList<String> diseases = FXCollections.observableArrayList("disease 1", "disease 2", "h", "n", "n");
            results.getChildren().add(new ListView<>(diseases));


            results.getChildren().add(new Label("How to treat it:"));
            ObservableList<String> treatment = FXCollections.observableArrayList("treatment1", "treatment 2");
            results.getChildren().add(new ListView<>(treatment));


            if (checkbox.isSelected()){

                results.getChildren().add(new Label("It can also be a side effect of:"));
                ObservableList<String> meds = FXCollections.observableArrayList("medoc1", "medoc 2");
                results.getChildren().add(new ListView<>(meds));
                
                results.getChildren().add(new Label("How to treat it:"));
                ObservableList<String> treatmentse = FXCollections.observableArrayList("treatment1", "treatment 2");
                results.getChildren().add(new ListView<>(treatmentse));
                
            }
            
            
            
            
        }
    }
}
