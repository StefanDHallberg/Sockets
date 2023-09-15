package GUI;

import clients.TimeClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import networking.NetworkClient;
import networking.NetworkClientListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TimeController {
    @FXML private Label timeLabel; // Label to display the server's time
    @FXML private TextArea ta; // TextArea for displaying server responses
    @FXML private TextField tfRadius; // TextField for user input
    private String serverAddress = "localhost";
    private int port;
    private NetworkClient networkClient;
    private List<NetworkClientListener> listeners = new ArrayList<>();

    public void addNetworkClientListener(NetworkClientListener listener) {
        listeners.add(listener);
        System.out.println("added listener: " + listener);
    }
    public void removeNetworkClientListener(NetworkClientListener listener) {
        listeners.remove(listener);
    }
    public List<NetworkClientListener> getNetworkClientListeners() {
        return listeners;
    }

    public TimeController(){
    }
    public void setServerInfo(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }
    public void initializeNetworkClient(NetworkClientListener listener) {
        networkClient = new NetworkClient(serverAddress, port);
        addNetworkClientListener(listener);
        if (listener != null) {
            //addNetworkClientListener(listener); // Use the method to add the listener
            listener.onNetworkClientListener(networkClient);
            System.out.println("network active " + networkClient);
            System.out.println("Listener active " + getNetworkClientListeners());
            if (serverAddress != null && port != 0) {
                // Create a thread for timer updates
                Thread timeUpdate = new Thread(this::runServerUpdateCode);
                timeUpdate.setDaemon(true);
                timeUpdate.start();
            }
        }
    }

    @FXML
    public void handleSendButtonAction() {
        try {
            double radius = Double.parseDouble(tfRadius.getText().trim());
            double area = networkClient.sendRadiusAndGetArea(radius);

            Platform.runLater(() -> {
                ta.appendText("Radius is " + radius + "\n");
                ta.appendText("Area received from the server is " + area + '\n');
            });
        } catch (NumberFormatException ex) {
            System.err.println("Invalid input: " + ex.getMessage());
        }
    }
    private void runServerUpdateCode() {
        while (true) {
            try {
                if (networkClient != null) {
//                    System.out.println("updating clock?");
                    String serverTime = networkClient.receiveServerTime();
                    Platform.runLater(() -> {
                        timeLabel.setText("Server Time: " + serverTime);
                    });
                }
                // Sleep = update rate (updates every second)
                Thread.sleep(1000);
            } catch (IOException e) { // Handle in-out exceptions0
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
