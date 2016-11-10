package asteroidsserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

public class FXMLDocumentController implements Initializable {
    
    private int playerNum = 0;
    @FXML
    private TextArea textArea;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        new Thread(() -> {
            try {
                // Create a server socket
                ServerSocket serverSocket = new ServerSocket(80);

                while (true) {
                    // Listen for a new connection request
                    Socket socket = serverSocket.accept();
                    
                    playerNum++;
                    
                    Platform.runLater(() -> {
                        textArea.appendText("Client connected to server.");
                    });
                    
                 new Thread(new HandleAPlayer(socket, playerNum)).start();
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }).start();
    }    
    
}

class HandleAPlayer implements Runnable, asteroids.AsteroidsConstants {
    
    private Ship ship;
    private Socket socket;
    private Lock lock = new ReentrantLock();
    private String shipImageFileName;
    private int playerNum;
    
    
    
    public HandleAPlayer(Socket socket, int playerNum){
        this.socket = socket;
        this.playerNum = playerNum;
    }
    
    @Override
    public void run(){
        try {
            // Create reading and writing streams
            BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outputToClient = new PrintWriter(socket.getOutputStream());

            // Continuously serve the client
            while (true) {
                // Receive request code from the client
                int request = Integer.parseInt(inputFromClient.readLine());
                // Process request
                switch (request) {
                    case SET_SHIP: {
                        lock.lock();
                        shipImageFileName = inputFromClient.readLine();
                        ship = new Ship(playerNum, shipImageFileName);
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}