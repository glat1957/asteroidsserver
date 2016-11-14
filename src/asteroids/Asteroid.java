package asteroids;

import physics.*;

public class Asteroid {

    private int dX;
    private int dY;
    private Point asteroidCenter;
    private int asteroidRadius;
    private Ray ray;

    public void Asteroid(int x, int y, int radius, int dX, int dY) {
        this.asteroidCenter.x = x;
        this.asteroidCenter.y = y;
        this.asteroidRadius = radius;

        Vector velocity = new Vector(dX, dY);
        double speed = velocity.length();
        ray = new Ray(new Point(x, y), velocity, speed);
    }

    public boolean isHit(Point bulletCenter, int bulletRadius) {
        // Since asteroids and bullets are going to be represented using circles,
        // we can use the origin point and radius of each and the distance formula
        // to determine if they overlap.
        double distance = Math.pow((asteroidCenter.x - bulletCenter.x) * (asteroidCenter.x - bulletCenter.x)
                + (asteroidCenter.y - bulletCenter.y) * (asteroidCenter.y - bulletCenter.y), 0.5);

        if (distance < asteroidRadius + bulletRadius) {
            return true;
        } else if (distance > asteroidRadius + bulletRadius) {
            return false;
        } else {
            return true;
        }
    }

    public Point getAsteroidCenter() {
        return asteroidCenter;
    }

    public void moveAsteroid(int dy, int dx) {
        this.asteroidCenter.x += dx;
        this.asteroidCenter.y += dy;
    }

    public Ray getRay() {
        return ray;
    }

    public void setRay(Ray ray) {
        this.ray = ray;
    }
}
