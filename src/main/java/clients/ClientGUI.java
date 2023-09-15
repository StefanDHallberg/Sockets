package clients;

import GUI.ClientGUIController;
import GUI.TimeController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import networking.NetworkClient;
import networking.NetworkClientListener;
import networking.RunnableApp;

import java.io.IOException;

public class ClientGUI implements RunnableApp {

    private Stage apps;
    private NetworkClientListener listener;

    public ClientGUI(Stage apps) {
        this.apps = apps;
    }

    @Override
    public void start() {
        // Use Platform.runLater() to set up the UI on the JavaFX Application Thread
        Platform.runLater(() -> {
            try {
                // Create a new Stage for the client GUI
                Stage clientStage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/ClientControllerGUI.fxml"));
                Parent root = loader.load();

                // Get the controller instance
                ClientGUIController clientGUIController = loader.getController();

                // Call the custom initialization method
                clientGUIController.CustomInitialize();

                // Set up the scene
                Scene scene = new Scene(root, 800, 600);
                clientStage.setTitle("Client GUI");
                clientStage.setScene(scene);
                clientStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
