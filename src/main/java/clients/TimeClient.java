package clients;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import networking.NetworkClient;
import GUI.TimeController;
import networking.NetworkClientListener;
import networking.RunnableApp;

import java.io.IOException;

public class TimeClient implements RunnableApp, NetworkClientListener {
    private NetworkClient networkClient;
    private NetworkClientListener disconnectListener;
    private String serverAddress;
    private int port;
    @FXML private TimeController controller;

    public TimeClient(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }

    @Override
    public void onNetworkClientListener(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    @Override
    public void onNetworkClientDisconnected() {
    }

    @Override
    public void start() {
        Platform.runLater(() -> {
            try {
                System.out.println("Launching - TEST " + serverAddress + " " +  port);
                // Create a new Stage for the TimeClient GUI
                Stage primaryStage = new Stage();

                // Load the FXML file with the controller.
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/TimeController.fxml"));
                Parent root = loader.load();

                // Get the controller instance
                controller = loader.getController();
                controller.setServerInfo(serverAddress, port);

                controller.initializeNetworkClient(this);

                // Create the scene and stage
                Scene scene = new Scene(root, 450, 200);
                primaryStage.setTitle("TimeClient");
                primaryStage.setScene(scene);
                primaryStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}