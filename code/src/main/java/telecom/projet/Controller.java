package telecom.projet;

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

public class Controller {
    @FXML private Label label; // Label à mettre à jour

    @FXML private Label query;

    @FXML private CheckBox checkbox;

    @FXML private TextField symptoms;

    @FXML private ScrollPane results;

    private Data data; // Instance pour les données
    public Controller() {

    }

    @FXML
    public void search() {
        VBox vbox = new VBox();
        vbox.setStyle("-fx-alignment: center; -fx-spacing: 10;");

        if (symptoms.getText().isEmpty()){
            query.setText("Please enter a symptom");
        }else{
            //display of the results
            query.setText("Symptoms: " + symptoms.getText());

            //data
            ArrayList<String> treatments = new ArrayList<>();
            treatments.add("treatment1zzzzzzzzzzzzzzzzzzzzzzzzzzz");
            treatments.add("treatment2");
            treatments.add("treatment3");
            treatments.add("treatment4");
            
            vbox.getChildren().add(display("angine",treatments, "disease"));
            vbox.getChildren().add(display("grippe",treatments, "disease"));
        

            if (checkbox.isSelected()){
                vbox.getChildren().add(display("aspirin",treatments, "side_effect"));
                vbox.getChildren().add(display("ibuprofen",treatments, "side_effect"));

            }
            results.setContent(vbox);
        }
    }

    public BorderPane display(String disease, ArrayList<String> treatments, String type){
        //type = "disease" or "side_effect"

        //return a BorderPane composed of:
        //hboxLeft{ ImageView[image] | VBox[disease]} | ImageView[arrow]| hboxRight{ ImageView[image] | VBox[treatments]}

        BorderPane borderPane = new BorderPane();
        
        //-----------left
        HBox hboxLeft = new HBox();

        Image diseaseImage = new Image(getClass().getResource("images/disease.png").toExternalForm());
        
        if (type.equals("side_effect")){
            diseaseImage = new Image(getClass().getResource("images/meds.png").toExternalForm());
        }
        ImageView diseaseImageView = new ImageView(diseaseImage);
        diseaseImageView.setFitHeight(80);
        diseaseImageView.setFitWidth(80);
        diseaseImageView.setPreserveRatio(true);
        diseaseImageView.setStyle("-fx-padding: 10;");

        hboxLeft.getChildren().add(diseaseImageView);


        VBox diseaseBox = new VBox();
        if (type.equals("disease")){
            diseaseBox.getChildren().add(new Label("Disease:"));
            hboxLeft.setStyle("-fx-background-color: #FFC0CB;");
        }else{
            diseaseBox.getChildren().add(new Label("Drug causing a side effect:"));
            hboxLeft.setStyle("-fx-background-color: #F1E2BE;");

        }
        diseaseBox.getChildren().add(new Label(disease));
        
        diseaseBox.getChildren().get(0).setStyle("-fx-text-fill: gray; -fx-font-weight: bold;");
        hboxLeft.getChildren().add(diseaseBox);
        

        borderPane.setLeft(hboxLeft);

        //-----------center
        Image arrowImage = new Image(getClass().getResource("images/arrow.png").toExternalForm());
        ImageView arrowImageView = new ImageView(arrowImage);
        arrowImageView.setFitHeight(80);
        arrowImageView.setFitWidth(80);
        arrowImageView.setPreserveRatio(true);
        arrowImageView.setStyle("-fx-padding: 10;");
        borderPane.setCenter(arrowImageView);

        
        //----------right
        VBox vbox = new VBox();

        HBox hboxRight = new HBox();
        //add image
        Image image = new Image(getClass().getResource("images/treatment.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(80);
        imageView.setFitWidth(80);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-padding: 10;");
        hboxRight.getChildren().add(imageView);

        for (String treatment : treatments){
            vbox.getChildren().add(new Label(treatment));
        }
        
        hboxRight.getChildren().add(vbox);
        
        //hbox.setStyle("-fx-background-color: #F0F8FF; -fx-border-radius: 30; -fx-padding: 10; -fx-spacing: 10;");
        hboxRight.setStyle("-fx-background-color: #C0EFA7; -fx-padding: 10;");
        borderPane.setRight(hboxRight);
        borderPane.setStyle("-fx-background-color: #F0F8FF; -fx-border-radius: 30; -fx-padding: 30; -fx-spacing: 10;");
        

        //fill the width of the scrollpane
        borderPane.prefWidthProperty().bind(results.widthProperty());
        //center it
        borderPane.setStyle("-fx-alignment: center;");
        
        
        hboxLeft.prefWidthProperty().bind(results.widthProperty().divide(2.5));//width of the left part
        hboxRight.prefWidthProperty().bind(results.widthProperty().divide(2.5));//width of the left part

        
        //space on the left and the right of the borderpane
        borderPane.setStyle("-fx-padding: 0 50 0 50;");

        

        return borderPane;
    }

}
