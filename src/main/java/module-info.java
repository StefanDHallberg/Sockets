module com {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    exports networking;
    opens networking to javafx.fxml;
    exports GUI;
    opens GUI to javafx.fxml;
    exports clients;
    opens clients to javafx.fxml;
    exports server;
    opens server to javafx.fxml;
}