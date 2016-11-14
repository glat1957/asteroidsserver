package asteroids;

import java.util.Random;

public class Asteroid {
    
    private int radius;
    
    private int ROCKET_ONE_X;
    private int ROCKET_ONE_Y;
    private int ROCKET_TWO_X;
    private int ROCKET_TWO_Y;
    
    private int dX;
    private int dY;
    private int asteroidX;
    private int asteroidY;
    private int intercept;
    private int slope;
   

    //Bullet newBullet = new Bullet();
    //Random r = new Random();

    /*public boolean isHit() {
        if (asteroidX == newBullet.returnBulletX() && asteroidY == newBullet.returnBulletY()) {
            return true;
        } else {
            return false;
        }
    }*/

    public int getAsteroidX() {
        return asteroidX;
    }

    public int getAsteroidY() {
        return asteroidY;
    }

    public void generateAsteroidEquation(){
        //intercept = r.nextInt(20); //What should the bounds be?
        //slope = r.nextInt(20);   
    }
}
