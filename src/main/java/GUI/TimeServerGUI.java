package GUI;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import networking.RunnableApp;
import server.TimeServer;

import java.util.Timer;

public class TimeServerGUI implements RunnableApp {
    private Timer timer = new Timer(true);
    private TimeServer timeServerInstance;

    public TimeServerGUI() {
        // Initialize the timeServerInstance with the hardcoded "localhost" address
        this.timeServerInstance = new TimeServer();
    }

    @Override
    public void start() {
        // Run UI-related code on the JavaFX application thread
        Platform.runLater(() -> {
            // Create a new stage for the TimeServer GUI
            Stage timeServerStage = new Stage();

            // Get the serverLog from the TimeServer instance
            TextArea serverLog = timeServerInstance.getServerLog();

            // Create a scene for the server GUI
            Scene scene = new Scene(new ScrollPane(serverLog), 450, 200);

            timeServerStage.setTitle("Server");
            timeServerStage.setScene(scene);
            timeServerStage.show();

            // Other GUI setup and logic...

            // For example, if you want to display a message when the server is started:
            System.out.println("Server started on port: " + timeServerInstance.getPort() + " " + timeServerInstance.getAddress());

            // Register a shutdown hook to close sockets and release resources
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        });

        // Start the server logic in a separate thread
        Thread serverThread = new Thread(() -> {
            timeServerInstance.startServerLogic();
        });
        serverThread.start();

        // Create a SendTimeTask with both TimeServer and TextArea
        TimeServer.SendTimeTask sendTimeTask = new TimeServer.SendTimeTask(timeServerInstance);

        // Schedule the timer task to send clock updates every second in milliseconds
        timer.scheduleAtFixedRate(sendTimeTask, 0, 1000);
    }

    // Implement the shutdown method to call TimeServer's shutdown
    private void shutdown() {
        if (timeServerInstance != null) {
            timeServerInstance.shutdown();
        }
    }
}
