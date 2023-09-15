package networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetworkClient {
    private Socket socket;
    private DataOutputStream toServer;
    private DataInputStream fromServer;

    public NetworkClient(String serverAddress, int serverPort) {
        initializeSocket(serverAddress, serverPort);
    }

    public void initializeSocket(String serverAddress, int serverPort) {
        try {
            // Initialize the socket and connect to the server
            socket = new Socket(serverAddress, serverPort);
            toServer = new DataOutputStream(socket.getOutputStream());
            fromServer = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendData(String data) {
        try {
            // Send data to the server
            toServer.writeUTF(data);
            toServer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String receiveData() {
        try {
            // Receive data from the server
            return fromServer.readUTF();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void close() {
        try {
            // Close the socket and streams when done
            if (socket != null) {
                System.out.println("shutting down connection socket port: " + socket);
                socket.close();
            }
            if (toServer != null) {
                System.out.println("shutting down connection toServer: " + toServer);
                toServer.close();
            }
            if (fromServer != null) {
                System.out.println("shutting down connection fromServer: " + fromServer);
                fromServer.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public double sendRadiusAndGetArea(double radius) {
        try {
            toServer.writeDouble(radius);
            toServer.flush();
            return fromServer.readDouble();
        } catch (NumberFormatException | IOException ex) {
            System.err.println("Invalid input: " + ex.getMessage());
            return 0;
        }
    }

    public String receiveServerTime() throws IOException {
        if (fromServer != null) {
            String serverTime = fromServer.readUTF();
//            System.out.println(serverTime); // Print the received time for debugging
            return serverTime;
        } else {
            throw new IOException("Not connected to the server.");
        }
    }
}