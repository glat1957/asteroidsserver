package asteroids;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Simulation {

    private Lock lock;
    private List<Bullet> playerBulletsInScene = Collections.synchronizedList(new ArrayList<>());
    private List<Asteroid> asteroidsInScene = Collections.synchronizedList(new ArrayList<>());

    public Simulation() {
        lock = new ReentrantLock();
    }

    public void evolve(double time) {
        for (int i = 0; i < playerBulletsInScene.size(); i++) {
            playerBulletsInScene.get(i).move(time);
        }
        for (int i = 0; i < asteroidsInScene.size(); i++) {
            asteroidsInScene.get(i).move(time);
        }
       
    }

    public List<Bullet> getPlayerBullets() {
        return playerBulletsInScene;
    }
   
    public List<Asteroid> getAsteroids() {
        return asteroidsInScene;
    }

    public void playerRemoveBullet(int i) {
        playerBulletsInScene.remove(i);
    }

    public void removeAsteroid(int i) {
        asteroidsInScene.remove(i);
    }

    public void playerAddBullet(double x, double y, double playerRotation) {
        playerBulletsInScene.add(new Bullet(x, y,
                10, 10, Math.cos(Math.toRadians(playerRotation - 90)), Math.sin(Math.toRadians(playerRotation - 90))));
    }

    public boolean isHitBA(Bullet bullet, Asteroid asteroid) {
        // Since asteroids and bullets are going to be represented using circles,
        // we can use the origin point and radius of each and the distance formula
        // to determine if they overlap.
        double distance = Math.pow((bullet.getRay().origin.x - asteroid.getRay().origin.x) * (bullet.getRay().origin.x - asteroid.getRay().origin.x)
                + (bullet.getRay().origin.y - asteroid.getRay().origin.y) * (bullet.getRay().origin.y - asteroid.getRay().origin.y), 0.5);

        if (distance < bullet.getRadius() + asteroid.returnRadius()) {
            return true;
        } else if (distance > bullet.getRadius() + asteroid.returnRadius()) {
            return false;
        } else {
            return true;
        }
    }
    public boolean isHitS1A(Asteroid asteroid) {
        double distance = Math.pow((305 - asteroid.getRay().origin.x) * (305 - asteroid.getRay().origin.x)
                + (250 - asteroid.getRay().origin.y) * (250 - asteroid.getRay().origin.y), 0.5);

        if (distance < 32 + asteroid.returnRadius()) {
            return true;
        } else if (distance > 32 + asteroid.returnRadius()) {
            return false;
        } else {
            return true;
        }
    }
    public boolean isHitS2A(Asteroid asteroid) {
        double distance = Math.pow((395 - asteroid.getRay().origin.x) * (395 - asteroid.getRay().origin.x)
                + (250 - asteroid.getRay().origin.y) * (250 - asteroid.getRay().origin.y), 0.5);

        if (distance < 32 + asteroid.returnRadius()) {
            return true;
        } else if (distance > 32 + asteroid.returnRadius()) {
            return false;
        } else {
            return true;
        }
    }
}
