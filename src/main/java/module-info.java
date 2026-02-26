module com.example.labs {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.labs to javafx.fxml;
    exports com.example.labs;
}