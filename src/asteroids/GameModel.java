package asteroids;

// All data that needs to be shared between the two clients exists in this class
// of which there is only ONE instance.

public class GameModel {
    
    private int connectedPlayers = 0;
   
    private double player1Rotation = 0.0;
    private double player2Rotation = 0.0;
    
    public void GameModel(){
    }
    
    public void playerConnected(){
        connectedPlayers++;
    }
    
    public void playerDisconnted(){
        connectedPlayers--;
    }
    
    public int getNumConnected(){
        return connectedPlayers;
    }
    
    public void setPlayer1Rotation(double rotation){
        player1Rotation = rotation;
    }
    
    public void setPlayer2Rotation(double rotation){
        player2Rotation = rotation;
    }
    
    public double getPlayer1Rotation(){
        return player1Rotation;
    }

    public double getPlayer2Rotation(){
        return player2Rotation;
    }
    
}
