package server;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TimeServer {
    private int port;
    private static String serverAddressField = "localhost";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private static List<Socket> clientSockets = new ArrayList<>();
    private List<Thread> clientThreads = new ArrayList<>();
    private ServerSocket serverSocket;
    private TextArea serverLog = new TextArea(); // ionvoke textarea else itll be null.
    //private static final String serverAddressField = "localhost"; // Setting server address to "localhost"
    public TimeServer() {
        this.port = generateRandomPort();
    }

    private int generateRandomPort() {
        // Generate a random port between 2000 and 6000
        return new Random().nextInt(4001) + 2000;
    }

    public int getPort() {
        return port;
    }
    public static String getAddress() {
        return serverAddressField;
    }

    public TextArea getServerLog() {
        return serverLog;
    }

    public void setServerLog(TextArea serverLog) {
        this.serverLog = serverLog;
    }
    public void startServerLogic() {
        try {
            serverSocket = new ServerSocket(port);
            logMessage("Server started at port " + port);

            while (true) {
                Socket socket = serverSocket.accept();

                // Add the client socket to the list
                clientSockets.add(socket);

                // Create a new thread for each connected client
                Thread clientThread = new Thread(() -> {
                    handleClient(socket);
                });

                // Store the client thread, TO-DO later.
                clientThreads.add(clientThread);

                clientThread.start();

                // Store the client thread (if needed)
                //clientThreads.add(clientThread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleClient(Socket socket) {
        try {
            // Get the client's address and port
            String clientAddress = socket.getInetAddress().getHostAddress();
            int clientPort = socket.getPort();

            // Get the current timestamp
            String timestamp = dateFormat.format(new Date());

            // Log that a client has connected with a timestamp
            logMessage("Client connected from " + clientAddress + ":" + clientPort + " at " + timestamp);

            DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
            DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());

            while (true) {
                try {
                    // Receive radius from the client
                    double radius = inputFromClient.readDouble();

                    // Mathin'.
                    double area = radius * radius * Math.PI;

                    // Sends area back to the client
                    outputToClient.writeDouble(area);

                    Platform.runLater(() -> {
                        serverLog.appendText("Radius received from client: " + radius + '\n');
                        serverLog.appendText("Area is: " + area + '\n');
                    });
                } catch (SocketException e) {
                    // Handle disconnection or other exceptions
                    handleClientSocketException(socket, e);
                    break; // Exit the loop when an exception occurs
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Client disconnected, remove the socket from the list
            removeClientSocket(socket); // Clears the server log as well when a client leaves
        }
    }

    public void logMessage(String message) {
        Platform.runLater(() -> {
            if (serverLog != null) {
                serverLog.appendText(message + "\n");
            }
        });
    }

    public static class SendTimeTask extends TimerTask {
        private final TimeServer timeServer;
        public SendTimeTask(TimeServer timeServer) {
            this.timeServer = timeServer;
        }

        public void run() {
            // Send the current time to all connected clients using threads
            TextArea serverLog = timeServer.getServerLog();
            serverLog.appendText("Sending clock update to clients.\n");
            String currentTime = dateFormat.format(new Date());

            for (Socket clientSocket : timeServer.clientSockets) {
                Thread clientUpdater = new Thread(() -> {
                    try {
                        DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                        dos.writeUTF("Current Time: " + currentTime);
                        dos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                // Start the thread to update this client
                clientUpdater.start();
            }
        }
    }

    public void shutdown() {
        // Close all open sockets
        for (Socket clientSocket : clientSockets) {
            try {
                System.out.println("shutting this down.");
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleClientSocketException(Socket socket, IOException e) {
        if (e.getMessage() != null &&
                (e.getMessage().contains("Connection reset by peer") ||
                        e.getMessage().contains("An established connection was aborted by the software in your host machine"))) {
            // Handle the exceptions for both cases
            // Remove the disconnected socket from the list of client sockets
            removeClientSocket(socket);
        } else {
            e.printStackTrace();
        }
    }

    private void removeClientSocket(Socket socket) {
        // Remove the socket from the list of client sockets
        clientSockets.remove(socket);

        // Append the disconnection message after clearing the log
        Platform.runLater(() -> {
            clearLog(); // Assuming serverLog is the TextArea where you display log messages.
            serverLog.appendText("Client disconnected: " + socket.getInetAddress() + ":" + socket.getPort() + '\n');
        });
    }

    private void clearLog() {
        Platform.runLater(() -> {
            String[] logMessages = serverLog.getText().split("\n"); // Split log messages by newline
            serverLog.clear(); // Clear the log

            // Append back the messages that don't contain "Sending clock update to clients"
            for (String message : logMessages) {
                if (!message.contains("Sending clock update to clients")) {
                    serverLog.appendText(message + '\n');
                }
            }
        });
    }
}
