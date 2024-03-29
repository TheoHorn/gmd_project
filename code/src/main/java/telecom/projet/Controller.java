package telecom.projet;

import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import telecom.projet.model.Record;

public class Controller {
    @FXML private Label label; // Label à mettre à jour

    @FXML private Label query;

    @FXML private Label searching;

    @FXML private CheckBox checkbox;

    @FXML private TextField symptoms;

    @FXML private ScrollPane results;

    private Data data; // Instance pour les données

    private ArrayList<Record> records = new ArrayList<>();

    public Controller() {}

    @FXML
    public void search() throws IOException {
        VBox vbox = new VBox();
        vbox.setStyle("-fx-alignment: center; -fx-spacing: 10;");

        if (symptoms.getText().isEmpty()){
            query.setText("Please enter a symptom");
        }else{
            data = new Data(symptoms.getText(), checkbox.isSelected());
            records = data.getRecords();

            ArrayList<String> treatments = new ArrayList<>();
            int max = 500;
            if (checkbox.isSelected()){
                if (records.size() < max){
                    max = records.size();
                }
                for (int i = 0; i < max; i++){
                        //suppress the \n, \r, \t and double spaces in the treatment
                        records.get(i).setTreatment(records.get(i).getTreatment().replace("\n", " ").replace("\r", " ").replace("\t", " ").replace("  ", " "));
                        //suppress the \n, \r, \t and double spaces in problem
                        records.get(i).setProblem(records.get(i).getProblem().replace("\n", " ").replace("\r", " ").replace("\t", " ").replace("  ", " "));
                        
                        treatments.add(records.get(i).getTreatment());
                        vbox.getChildren().add(line_of_infos(records.get(i).getSymptom(),records.get(i).getProblem(), treatments, checkbox.isSelected(), records.get(i).getData_source(), records.get(i).getScore()));
                        treatments = new ArrayList<>();
                    }
                
            }else{
                if (records.size() < max){
                    max = records.size();
                }
                String problem = "";
                for (int i = 0; i < max; i++){
                    //suppress the \n, \r, \t and double spaces in the treatment
                    records.get(i).setTreatment(records.get(i).getTreatment().replace("\n", " ").replace("\r", " ").replace("\t", " ").replace("  ", " "));
                    //suppress the \n, \r, \t and double spaces in problem
                    records.get(i).setProblem(records.get(i).getProblem().replace("\n", " ").replace("\r", " ").replace("\t", " ").replace("  ", " "));
                        
                    if (problem == ""){
                        problem = records.get(i).getProblem();
                        treatments.add(records.get(i).getTreatment());
                        
                    }
                    else if (problem.equals(records.get(i).getProblem())){
                        if (!treatments.contains(records.get(i).getTreatment())){
                            treatments.add(records.get(i).getTreatment());
                        }
                    }
                    else{
                        vbox.getChildren().add(line_of_infos(records.get(i-1).getSymptom(),problem, treatments, checkbox.isSelected(), records.get(i-1).getData_source(), records.get(i-1).getScore()));
                        
                        problem = records.get(i).getProblem();
                        treatments = new ArrayList<>();
                        treatments.add(records.get(i).getTreatment());}
                    if (i == max-1){
                        vbox.getChildren().add(line_of_infos(records.get(i).getSymptom(),problem, treatments, checkbox.isSelected(), records.get(i).getData_source(), records.get(i).getScore()));
                    }
                    
                }
            }
            
            results.setContent(vbox);

            query.setText("Symptoms: " + symptoms.getText()+ " ("+ records.size() +" results)"); //display the research made
            
        }
    }

    public BorderPane line_of_infos(String symptom, String problem, ArrayList<String> treatment, Boolean side_effect, String data_source, int score){
        //side_effect = true if it's a side effect, false if it's a disease

        //return a BorderPane composed of:
        //hboxLeft{ ImageView[image] | VBox[disease]} | ImageView[arrow]| hboxRight{ ImageView[image] | VBox[treatments]}

        BorderPane borderPane = new BorderPane();
        
        //-----------left
        HBox hboxLeft = new HBox();

        Image diseaseImage = new Image(getClass().getResource("images/disease.png").toExternalForm());
        
        if (side_effect){
            diseaseImage = new Image(getClass().getResource("images/meds.png").toExternalForm());
        }
        ImageView diseaseImageView = new ImageView(diseaseImage);
        diseaseImageView.setFitHeight(50);
        diseaseImageView.setFitWidth(50);
        diseaseImageView.setPreserveRatio(true);
        diseaseImageView.setStyle("-fx-padding: 10;");

        hboxLeft.getChildren().add(diseaseImageView);


        VBox diseaseBox = new VBox();
        if (!side_effect){
            diseaseBox.getChildren().add(new Label("Disease:"));
            hboxLeft.setStyle("-fx-background-color: #FFC0CB;-fx-background-radius: 20;");
        }else{
            diseaseBox.getChildren().add(new Label("Drug causing a side effect:"));
            hboxLeft.setStyle("-fx-background-color: #F1E2BE;-fx-background-radius: 20;");

        }
        diseaseBox.getChildren().add(new Label(problem + " (" + symptom + ")"));

        //display score if checkbox not selected
        if (!side_effect){
            diseaseBox.getChildren().add(new Label("Score: " + score));
            diseaseBox.getChildren().add(new Label("Data source: " + data_source));
        }
        
        //wrap text for the children of diseaseBox
        for (int i = 0; i < diseaseBox.getChildren().size(); i++){
            ((Label)diseaseBox.getChildren().get(i)).setWrapText(true);
        }

        diseaseBox.getChildren().get(0).setStyle("-fx-text-fill: gray; -fx-font-weight: bold;");
        
        if (side_effect && score != 0){
            diseaseBox.getChildren().add(new Label("Frequency: " + score + "%"));
        }
        hboxLeft.getChildren().add(diseaseBox);
        

        borderPane.setLeft(hboxLeft);

        //-----------center
        Image arrowImage = new Image(getClass().getResource("images/arrow.png").toExternalForm());
        ImageView arrowImageView = new ImageView(arrowImage);
        arrowImageView.setFitHeight(50);
        arrowImageView.setFitWidth(50);
        arrowImageView.setPreserveRatio(true);
        arrowImageView.setStyle("-fx-padding: 10;");
        borderPane.setCenter(arrowImageView);

        
        //----------right
        
        HBox hboxRight = new HBox();
        //add image
        Image image = new Image(getClass().getResource("images/treatment.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        
        
        hboxRight.getChildren().add(imageView);
        
        //display the treatment and its details
        VBox vbox = new VBox();
        for (int i = 0; i < treatment.size(); i++){
            Label treatmentLabel = new Label(treatment.get(i));
            treatmentLabel.setWrapText(true);
            treatmentLabel.setStyle("-fx-padding: 0;");
            vbox.getChildren().add(treatmentLabel);

        }

        // Style the VBox to remove spacing between lines of treatments
        vbox.setStyle("-fx-spacing: 0;");

        //vbox.getChildren().add(new Label(treatment));
        //vbox.getChildren().add(new Label("Data source: " + data_source));

        //style for the name of the treatment
        //vbox.getChildren().get(0).setStyle("-fx-text-fill: gray; -fx-font-weight: bold;");
        
        
        //no spacing betwen lines
        vbox.setStyle("-fx-spacing: 0; -fx-line-spacing: 0;");

        
        hboxRight.getChildren().add(vbox);

        //style the right part
        hboxRight.setStyle("-fx-background-color: #C0EFA7;-fx-background-radius: 20;");
        borderPane.setRight(hboxRight);
        

        //fill the width of the scrollpane
        borderPane.prefWidthProperty().bind(results.widthProperty());
        //center it
        borderPane.setStyle("-fx-alignment: center;");
        
        
        hboxLeft.prefWidthProperty().bind(results.widthProperty().divide(2.5));//width of the left part
        hboxRight.prefWidthProperty().bind(results.widthProperty().divide(2.5));//width of the left part

        
        //space on the left and the right of the borderpane
        //borderPane.setStyle("-fx-padding: 0 50 0 50;");

        

        return borderPane;
    }

}
