module com.example.labs {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.google.gson;
    requires java.sql;


    opens com.example.labs to javafx.fxml;
    opens com.example.labs.model to com.google.gson;
    exports com.example.labs;
}