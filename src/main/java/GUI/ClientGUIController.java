package GUI;

import clients.TimeClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import networking.NetworkClient;
import networking.NetworkClientListener;
import java.util.List;

public class ClientGUIController implements NetworkClientListener {
    @FXML
    private TextField serverAddressField;
    @FXML
    private TextField portField;
    @FXML
    private TextArea logArea;
    private NetworkClient networkClient;
    private String serverAddress;
    private NetworkClientListener disconnectListener;
    private TimeController timeController;

    public void CustomInitialize() {
//       System.out.println("customInitialize called");
        timeController = new TimeController();

//       System.out.println("ClientGUIController instance: " + this);
//       System.out.println("TimeController instance: " + timeController);
    }

    @FXML
    private void connectButtonAction(ActionEvent event) {
        // Get the server address and port from the text fields
        serverAddress = serverAddressField.getText();
        String portStr = portField.getText();

        try {
            int port = Integer.parseInt(portStr);

            // Set the server address and port in the TimeController
            timeController.setServerInfo(serverAddress, port);

            // Connect to the server using the networkClient instance (already done in the constructor)
            logArea.appendText("Connected to server at " + serverAddress + ":" + port + "\n");

            // Updates serverAddressField and portField
            serverAddressField.setText(serverAddress);
            portField.setText(portStr);

//            System.out.println("TEST " + serverAddress + " " + port);
            launchTimeClient(serverAddress, port);
        } catch (NumberFormatException ex) {
            logArea.appendText("Invalid port number: " + portStr + "\n");
        } catch (Exception ex) {
            logArea.appendText("Error connecting to the server: " + ex.getMessage() + "\n");
        }
    }

    @FXML
    private void launchTimeClient(String serverAddress, int port) {
        // Create an instance of TimeClient and run its associated GUI
        TimeClient timeClient = new TimeClient(serverAddress, port);
        timeClient.start();
    }

    //WIP.
    @FXML
    private void disconnectButtonAction(ActionEvent event) {
        System.out.println('\n' + "timeController " + timeController);

        System.out.println("getNetworkClientListeners() " + timeController.getNetworkClientListeners() + '\n');
        System.out.println("client?? " );
        List<NetworkClientListener> listeners = timeController.getNetworkClientListeners();
        for (NetworkClientListener listener : listeners) {
            listener.onNetworkClientDisconnected();
        }
        logArea.appendText("Disconnected from the server.\n");
    }

    @Override
    public void onNetworkClientListener(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }


    @Override
    public void onNetworkClientDisconnected() {
        System.out.println("onNetworkClientDisconnected() called");

        if (networkClient != null) {
//            timeController.removeNetworkClientListener(NetworkClientListener listener);
            timeController.removeNetworkClientListener(disconnectListener);
            networkClient.close();
            System.out.println("Network client closed.");
            logArea.appendText("Disconnected from the server.\n");
        } else {
            System.out.println("Network client is not initialized.");
            logArea.appendText("Network client is not initialized.\n");
        }
    }
}