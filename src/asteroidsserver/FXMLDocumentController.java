package asteroidsserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import asteroids.*;

public class FXMLDocumentController implements Initializable {

    private GameModel gameModel;
    private int playerNum = 0;
    @FXML
    private TextArea textArea;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // This object is shared between both clients and passed to each thread.
        gameModel = new GameModel();

        new Thread(() -> {
            try {
                // Create a server socket
                ServerSocket serverSocket = new ServerSocket(8080);

                while (true) {
                    // Listen for a new connection request
                    Socket socket = serverSocket.accept();

                    // Increment player number and number of people connected.
                    playerNum++;
                    gameModel.playerConnected();

                    Platform.runLater(() -> {
                        textArea.appendText("Client connected to server. \n");
                    });

                    new Thread(new HandleAPlayer(socket, playerNum, gameModel)).start();
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }).start();
    }

}

class HandleAPlayer implements Runnable, asteroids.AsteroidsConstants {

    private GameModel gameModel;
    private ShipModel playerShip;
    private Socket socket;
    private Lock lock = new ReentrantLock();
    private String shipImageFileName;
    private int playerNum;
    private ObjectOutputStream outputObjectToClient;
    private ObjectInputStream inputObjectFromClient;

    public HandleAPlayer(Socket socket, int playerNum, GameModel gameModel) {
        this.socket = socket;
        this.playerNum = playerNum;
        this.gameModel = gameModel;
    }

    @Override
    public void run() {
        try {
            // Create reading and writing streams
            BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outputToClient = new PrintWriter(socket.getOutputStream());
            outputObjectToClient = new ObjectOutputStream(socket.getOutputStream());
            inputObjectFromClient = new ObjectInputStream(socket.getInputStream());

            // Continuously serve the client
            while (true) {
                // Receive request code from the client
                int request = Integer.parseInt(inputFromClient.readLine());
                // Process request
                switch (request) {
                    case GET_SHIP_MODEL: {
                        lock.lock();
                        shipImageFileName = inputFromClient.readLine();
                        playerShip = new ShipModel(playerNum, 3, shipImageFileName);
                        outputObjectToClient.writeObject(playerShip);
                        outputObjectToClient.flush();
                        break;
                    }
                    case GET_PLAYER_NUM: {
                        lock.lock();
                        outputToClient.println(playerNum);
                        outputToClient.flush();
                        break;
                    }
                    case SEND_PLAYER1_ROT: {
                        lock.lock();
                        gameModel.setPlayer1Rotation(Double.parseDouble(inputFromClient.readLine()));
                        break;
                    }
                    case SEND_PLAYER2_ROT: {
                        lock.lock();
                        gameModel.setPlayer2Rotation(Double.parseDouble(inputFromClient.readLine()));
                        break;
                    }
                    case GET_PLAYER1_ROT: {
                        lock.lock();
                        outputToClient.println(gameModel.getPlayer1Rotation());
                        outputToClient.flush();
                        break;
                    }
                    case GET_PLAYER2_ROT: {
                        lock.lock();
                        outputToClient.println(gameModel.getPlayer2Rotation());
                        outputToClient.flush();
                        break;
                    }
                    case NUM_CONNECTED: {
                        lock.lock();
                        outputToClient.println(gameModel.getNumConnected());
                        outputToClient.flush();
                        break;
                    }
                    case DISCONNECT_PLAYER: {
                        lock.lock();
                        gameModel.playerDisconnected();
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
