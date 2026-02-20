module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens com.example.demo to javafx.fxml;
    opens com.example.demo.lab1 to javafx.fxml;
    opens com.example.demo.lab2 to javafx.fxml;
    opens com.example.demo.lab3 to javafx.fxml;
    opens com.example.demo.lab4 to javafx.fxml;
    exports com.example.demo;
    exports com.example.demo.lab1;
    exports com.example.demo.lab2;
    exports com.example.demo.lab3;
    exports com.example.demo.lab4;
}