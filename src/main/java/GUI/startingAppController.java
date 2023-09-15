package GUI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import networking.RunnableApp;
import clients.ClientGUI;
import server.TimeServer;

import java.util.ArrayList;
import java.util.List;

public class startingAppController {

    private RunnableApp[] apps;
     private List<Thread> serverThreads;
     private List<Thread> clientThreads;
     private Stage primaryStage;

    @FXML private TextArea serverLogTextArea;
    TimeServer timeServer = new TimeServer();
    int port = timeServer.getPort();
    private TimeServerGUI timeServerGUI;

    public startingAppController() {
        // Initialize apps, serverThreads, and clientThreads here
        apps = new RunnableApp[2];

        // Create an instance of TimeServer and pass it to TimeServerGUI
        apps[0] = new TimeServerGUI();
        timeServerGUI = new TimeServerGUI();

        apps[1] = new ClientGUI(primaryStage);

        serverThreads = new ArrayList<>(); // Initialize as lists
        clientThreads = new ArrayList<>();
    }
    @FXML
    private void startServers() {
        // Generate a random port between 2000 and 8000
        port = 2000 + (int) (Math.random() * 6001);

        // Set the serverLog (assuming serverLogTextArea is the TextArea you want to use)
        timeServer.setServerLog(serverLogTextArea);

        // Start the TimeServer GUI in a background thread
        Thread guiThread = new Thread(() -> {
            startServerGUI();
        });

        guiThread.start();

        // Optionally, provide feedback that the server GUI is starting
        Platform.runLater(() -> {
            serverLogTextArea.appendText("Server GUI starting...\n");
        });
    }

    // This method starts the TimeServer GUI in a background thread
    private void startServerGUI() {
        Platform.runLater(() -> {
            timeServerGUI.start(); // Call the start() method on the initialized timeServerGUI
        });
    }
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML private void startingServer(ActionEvent event) {
        startServers();
    }
    @FXML private void stoppingServer(ActionEvent event) {
        stopServers();
    }

    @FXML private void startingClient(ActionEvent event) {
        startClients();
    }
    @FXML private void stoppingClient(ActionEvent event) {
        stopClients();
    }
    @FXML private void stopAll(ActionEvent event) {
        stop();
    }

    @FXML private void stopServers() {
        int lastIndex = serverThreads.size() - 1;

        if (lastIndex >= 0) {
            Thread lastServerThread = serverThreads.get(lastIndex);

            if (lastServerThread != null) {
                lastServerThread.interrupt();
            }
        }
    }

    @FXML private void startClients() {
        // Check if the client threads have already been created
        if (clientThreads.isEmpty()) {
            Thread clientThread = new Thread(() -> {
                apps[1].start(); // Start the Client
            });
            clientThreads.add(clientThread); // Add the thread to the list
            clientThread.start();
        }
        // Optionally, provide feedback that clients are starting
        Platform.runLater(() -> {
            serverLogTextArea.appendText("Client(s) starting...\n");
        });
    }


    @FXML private void stopClients() {
        for (Thread thread : clientThreads) {
            if (thread != null) {
                thread.interrupt();
            }
        }
    }

    //  A proper stop for JavaFX when application is exit.
    @FXML public void stop() {
        stopServers();
        stopClients();
        Platform.exit();
    }
}

