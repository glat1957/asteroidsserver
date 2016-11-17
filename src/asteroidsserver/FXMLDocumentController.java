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
import java.util.List;

public class FXMLDocumentController implements Initializable {

    private GameModel gameModel;
    private Simulation sim;
    private int playerNum = 0;
    @FXML
    private TextArea textArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // These objects are shared between both players and passed to each thread.
        gameModel = new GameModel();
        sim = new Simulation();
        
        new Thread(() -> {
            try {
                // Create a server socket
                ServerSocket serverSocket = new ServerSocket(8080);

                while (playerNum < 2) {
                    // Listen for a new connection request
                    Socket socket = serverSocket.accept();

                    // Increment player number and number of people connected.
                    playerNum++;
                    gameModel.playerConnected();

                    Platform.runLater(() -> {
                        textArea.appendText("Player connected to server. \n");
                    });

                    new Thread(new HandleAPlayer(socket, playerNum, gameModel, sim)).start();
                }
                
                new Thread(new Simulate(sim)).start();
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }).start();
        
        
    }

}

class HandleAPlayer implements Runnable, asteroids.AsteroidsConstants {

    private GameModel gameModel;
    private Simulation sim;
    private ShipModel playerShip;
    private Socket socket;
    private Lock lock = new ReentrantLock();
    private String shipImageFileName;
    private int playerNum;
    private ObjectOutputStream outputObjectToClient;
    private ObjectInputStream inputObjectFromClient;

    public HandleAPlayer(Socket socket, int playerNum, GameModel gameModel, Simulation sim) {
        this.socket = socket;
        this.playerNum = playerNum;
        this.gameModel = gameModel;
        this.sim = sim;
    }

    @Override
    public void run() {
        try {
            // Create reading and writing streams
            BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outputToClient = new PrintWriter(socket.getOutputStream());
            outputObjectToClient = new ObjectOutputStream(socket.getOutputStream());
            inputObjectFromClient = new ObjectInputStream(socket.getInputStream());

            // Continuously serve the player
            while (true) {
                // Receive request code from the player
                int request = Integer.parseInt(inputFromClient.readLine());
                // Process request
                switch (request) {
                    case GET_SHIP_MODEL: {
                        lock.lock();
                        shipImageFileName = inputFromClient.readLine();
                        playerShip = new ShipModel(playerNum, shipImageFileName);
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
                    case SET_PLAYER1_ROT: {
                        lock.lock();
                        gameModel.setPlayer1Rotation(Double.parseDouble(inputFromClient.readLine()));
                        break;
                    }
                    case SET_PLAYER2_ROT: {
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
                    case INC_SCORE: {
                        lock.lock();
                        gameModel.incrementScore();
                        break;
                    }
                    case GET_SCORE: {
                        lock.lock();
                        outputToClient.println(gameModel.getScore());
                        outputToClient.flush();
                        break;
                    }
                    case SET_PLAYER1_LIVES: {
                        lock.lock();
                        gameModel.setPlayer1Lives(Integer.parseInt(inputFromClient.readLine()));
                        break;
                    }
                    case SET_PLAYER2_LIVES: {
                        lock.lock();
                        gameModel.setPlayer2Lives(Integer.parseInt(inputFromClient.readLine()));
                        break;
                    }
                    case GET_PLAYER1_LIVES: {
                        lock.lock();
                        outputToClient.println(gameModel.getPlayer1Lives());
                        outputToClient.flush();
                        break;
                    }
                    case GET_PLAYER2_LIVES: {
                        lock.lock();
                        outputToClient.println(gameModel.getPlayer2Lives());
                        outputToClient.flush();
                        break;
                    }
                    case PLAYER_NEW_BULLET: {
                        lock.lock();
                        double x = Double.parseDouble(inputFromClient.readLine());
                        double y = Double.parseDouble(inputFromClient.readLine());
                        double rotation = Double.parseDouble(inputFromClient.readLine());
                        sim.playerAddBullet(x, y, rotation);
                        break;
                    }
                    case PLAYER_GET_BULLETS: {
                        lock.lock();
                        outputToClient.println(sim.getPlayerBullets().size());
                        outputToClient.flush();
                        
                        for(int i = 0; i < sim.getPlayerBullets().size(); i++){
                            outputObjectToClient.writeObject(sim.getPlayerBullets().get(i));
                        }
                        outputObjectToClient.flush();
                        //outputObjectToClient.writeObject(sim.getPlayerBullets());
                        //outputObjectToClient.flush();
                        System.out.println("Sent list of length: " + sim.getPlayerBullets().size());
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

// Thread to simulate movement of asteroids and bullets.
class Simulate implements Runnable, asteroids.AsteroidsConstants {

    private final Lock lock = new ReentrantLock();
    private final Simulation sim;
    private List<Bullet> playerBulletsInScene;
    //private List<Asteroid> asteroidsInScene;

    public Simulate(Simulation sim) {
        this.sim = sim;
        this.playerBulletsInScene = sim.getPlayerBullets();
        //this.asteroidsInScene = asteroidsInScene;
    }

    @Override
    public void run() {
        try {
            lock.lock();
            while (true) {
                
                sim.evolve(1.0);
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
