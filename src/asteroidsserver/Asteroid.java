package asteroidsserver;


public class Asteroid{

    final int ASTEROID_GENERATION = 5;
    final int ROCKET_ONE_X = 5;
    final int ROCKET_ONE_Y = 5;
    final int ROCKET_TWO_X = 5;
    final int ROCKET_TWO_Y = 5;
    int asteroidVelocity = 5;
    int xBullet = 5;
    int yBullet = 5;
    int xPosition;
    int yPosition;

    public boolean hitRocket(int x, int y) {
        getPosition(x,y);
        if (returnX()== ROCKET_ONE_X && returnY() == ROCKET_ONE_Y){
            return true;
        }else if (returnX()== ROCKET_TWO_X && returnY() == ROCKET_TWO_Y){
            return true;
        }else{
            return false;
        }
    }
    
    public boolean isHit(int x, int y, int a, int b){
        getPosition(x,y);
        //getBulletLocation(a,b);
        if (returnX()== bulletX() && returnY() == bulletY()){
            return true;
        }else{
            return false;
        }
    }
    
    public void getPosition(int x, int y) {
        // Gets X and Y coordinates
        xPosition = (x - 5) / asteroidVelocity; //placeholder formula for now
        yPosition = (y - 5) / asteroidVelocity; //placeholder formula for now
    }

    public int returnX() {
        return xPosition;
    }

    public int returnY() {
        return yPosition;
    }
    
    public int bulletX(){
        return xBullet;   
    }
    
    public int bulletY(){
        return yBullet;   
    }
    
}
