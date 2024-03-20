module telecom.projet {
    requires javafx.controls;
    requires javafx.fxml;

    opens telecom.projet to javafx.fxml;
    exports telecom.projet;
}
