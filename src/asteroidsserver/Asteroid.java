package asteroidsserver;

import java.util.Random;

public class Asteroid {

    final int ASTEROID_GENERATION = 5;
    final int ROCKET_ONE_X = 5;
    final int ROCKET_ONE_Y = 5;
    final int ROCKET_TWO_X = 5;
    final int ROCKET_TWO_Y = 5;
    int velocity;
    int xPosition;
    int yPosition;
    int intercept;
    int slope;
   

    Bullet newBullet = new Bullet();
    Random r = new Random();

    public boolean isHit() {
        if (returnX() == newBullet.returnBulletX() && returnY() == newBullet.returnBulletY()) {
            return true;
        } else {
            return false;
        }
    }

    public int returnX() {
        return xPosition;
    }

    public int returnY() {
        return yPosition;
    }

    
    public void generateAsteroid(){
        intercept = r.nextInt(20); //What should the bounds be?
        slope = r.nextInt(20);
        velocity = r.nextInt(10);
         
        
    }
}
