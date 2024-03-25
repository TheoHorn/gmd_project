package telecom.projet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Doctoflop");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main_page.fxml"));
        loader.setController(new Controller());
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root, 900, 900));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}