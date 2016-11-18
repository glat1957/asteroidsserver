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
import java.util.ArrayList;
import java.util.Iterator;
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

                new Thread(new Simulate(sim, gameModel)).start();
                new Thread(new GenerateAsteroids(sim)).start();
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
            outputObjectToClient = new ObjectOutputStream(socket.getOutputStream());
            inputObjectFromClient = new ObjectInputStream(socket.getInputStream());

            // Continuously serve the player
            while (true) {
                // Receive request code from the player
                int request = inputObjectFromClient.readInt();
                // Process request
                switch (request) {
                    case GET_SHIP_MODEL: {
                        shipImageFileName = inputObjectFromClient.readUTF();
                        playerShip = new ShipModel(playerNum, shipImageFileName);
                        outputObjectToClient.writeObject(playerShip);
                        outputObjectToClient.flush();
                        break;
                    }
                    case GET_PLAYER_NUM: {
                        outputObjectToClient.writeInt(playerNum);
                        outputObjectToClient.flush();
                        break;
                    }
                    case SET_PLAYER1_ROT: {
                        gameModel.setPlayer1Rotation(inputObjectFromClient.readDouble());
                        break;
                    }
                    case SET_PLAYER2_ROT: {
                        gameModel.setPlayer2Rotation(inputObjectFromClient.readDouble());
                        break;
                    }
                    case GET_PLAYER1_ROT: {
                        outputObjectToClient.writeDouble(gameModel.getPlayer1Rotation());
                        outputObjectToClient.flush();
                        break;
                    }
                    case GET_PLAYER2_ROT: {
                        outputObjectToClient.writeDouble(gameModel.getPlayer2Rotation());
                        outputObjectToClient.flush();
                        break;
                    }
                    case NUM_CONNECTED: {
                        outputObjectToClient.writeInt(gameModel.getNumConnected());
                        outputObjectToClient.flush();
                        break;
                    }
                    case DISCONNECT_PLAYER: {
                        gameModel.playerDisconnected();
                        break;
                    }
                    case INC_SCORE: {
                        gameModel.incrementScore();
                        break;
                    }
                    case GET_SCORE: {
                        outputObjectToClient.writeInt(gameModel.getScore());
                        outputObjectToClient.flush();
                        break;
                    }
                    case SET_PLAYER1_LIVES: {
                        gameModel.setPlayer1Lives(inputObjectFromClient.readInt());
                        break;
                    }
                    case SET_PLAYER2_LIVES: {
                        gameModel.setPlayer2Lives(inputObjectFromClient.readInt());
                        break;
                    }
                    case GET_PLAYER1_LIVES: {
                        outputObjectToClient.writeInt(gameModel.getPlayer1Lives());
                        outputObjectToClient.flush();
                        break;
                    }
                    case GET_PLAYER2_LIVES: {
                        outputObjectToClient.writeInt(gameModel.getPlayer2Lives());
                        outputObjectToClient.flush();
                        break;
                    }
                    case PLAYER_NEW_BULLET: {
                        double x = inputObjectFromClient.readDouble();
                        double y = inputObjectFromClient.readDouble();
                        double rotation = inputObjectFromClient.readDouble();
                        sim.playerAddBullet(x, y, rotation);
                        break;
                    }
                    case PLAYER_GET_BULLETS: {
                        ArrayList<Bullet> bullets = new ArrayList<>();

                        synchronized (sim.getPlayerBullets()) {
                            bullets.addAll(sim.getPlayerBullets());
                        }
                        outputObjectToClient.writeInt(bullets.size());
                        for (Bullet b : bullets) {
                            b.writeTo(outputObjectToClient);
                        }

                        outputObjectToClient.flush();
                        System.out.println("Sent bullet list of length: " + sim.getPlayerBullets().size());
                        break;
                    }
                    case PLAYER_GET_ASTEROIDS: {
                        ArrayList<Asteroid> asteroids = new ArrayList<>();

                        synchronized (sim.getAsteroids()) {
                            asteroids.addAll(sim.getAsteroids());
                        }
                        outputObjectToClient.writeInt(asteroids.size());
                        for (Asteroid a : asteroids) {
                            a.writeTo(outputObjectToClient);
                        }

                        outputObjectToClient.flush();
                        System.out.println("Sent asteroid list of length: " + sim.getAsteroids().size());
                        break;
                    }

                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

// Thread to simulate movement of asteroids and bullets.
class GenerateAsteroids implements Runnable, asteroids.AsteroidsConstants {

    private final Simulation sim;
    private List<Asteroid> asteroidsInScene;

    public GenerateAsteroids(Simulation sim) {
        this.sim = sim;
        this.asteroidsInScene = sim.getAsteroids();
    }

    @Override
    public void run() {
        try {
            while (true) {
                synchronized (asteroidsInScene) {
                    for(int i = 2+(int)(Math.random()*6); i>0; i--)
                        asteroidsInScene.add(new Asteroid());
                }
                try {
                    Thread.sleep(300 + (int)(Math.random() * 1700));
                } catch (InterruptedException ex) {
                }
            }
        } catch (Exception ex) {

        }
    }
}

// Thread to simulate movement of asteroids and bullets.
class Simulate implements Runnable, asteroids.AsteroidsConstants {

    private final Lock lock = new ReentrantLock();
    private final Simulation sim;
    private List<Bullet> playerBulletsInScene;
    private List<Asteroid> playerAsteroidsInScene;
    private boolean negativeOutOfBounds;
    private boolean positiveOutOfBounds;
    private GameModel gameModel;

    public Simulate(Simulation sim, GameModel gameModel) {
        this.sim = sim;
        this.playerBulletsInScene = sim.getPlayerBullets();
        this.playerAsteroidsInScene = sim.getAsteroids();
        this.gameModel = gameModel;
    }

    @Override
    public void run() {
        try {
            lock.lock();
            while (!(gameModel.getPlayer1Lives() == 0) || !(gameModel.getPlayer2Lives() == 0)) {

                synchronized (playerBulletsInScene) {
                    synchronized (playerAsteroidsInScene) {
                        sim.evolve(1.0);
                        for (Iterator<Bullet> i = playerBulletsInScene.iterator(); i.hasNext();) {
                            Bullet b = i.next();
                            negativeOutOfBounds = b.getRay().origin.x < 0 || b.getRay().origin.y < 0;
                            positiveOutOfBounds = b.getRay().origin.x > 700 || b.getRay().origin.y > 500;
                            if (positiveOutOfBounds || negativeOutOfBounds) {
                                i.remove();
                            }
                        }

                        for (Iterator<Asteroid> i = playerAsteroidsInScene.iterator(); i.hasNext();) {
                            Asteroid a = i.next();
                            negativeOutOfBounds = a.getRay().origin.x < 0 || a.getRay().origin.y < 0;
                            positiveOutOfBounds = a.getRay().origin.x > 700 || a.getRay().origin.y > 500;
                            if (positiveOutOfBounds || negativeOutOfBounds) {
                                i.remove();
                            } else if (sim.isHitS1A(a)) {
                                i.remove();
                                gameModel.setPlayer1Lives(gameModel.getPlayer1Lives() - 1);
                            } else if (sim.isHitS2A(a)) {
                                i.remove();
                                gameModel.setPlayer2Lives(gameModel.getPlayer2Lives() - 1);
                            }
                        }

                        for (Iterator<Bullet> ib = playerBulletsInScene.iterator(); ib.hasNext();) {
                            Bullet b = ib.next();
                            for (Iterator<Asteroid> ia = playerAsteroidsInScene.iterator(); ia.hasNext();) {
                                Asteroid a = ia.next();
                                if (sim.isHitBA(b, a)) {
                                    ib.remove();
                                    ia.remove();
                                    gameModel.incrementScore();
                                }
                            }
                        }
                    }
                }

                try {
                    Thread.sleep(50);
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
