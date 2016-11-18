package asteroids;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import physics.*;
import java.util.Random;

public class Asteroid {

    Random random = new Random();
    private int asteroidRadius;
    private Ray directionRay;
    Asteroid temp = null;

    public Asteroid() {
        double angle = random.nextDouble() * 2 * Math.PI;
        int w= 700, h=500;
        int x = random.nextInt(w)+1;
        int y = random.nextInt(h)+1;
        if(angle >= 0 && angle < Math.PI / 2){
            if(random.nextBoolean())
                x = 0;
            else
                y = 0;
        }else if(angle < Math.PI){
            if(random.nextBoolean())
                x = w;
            else
                y = 0;
        }else if(angle < Math.PI / 2 * 3){
            if(random.nextBoolean())
                x = 0;
            else
                y = h;
        }else{
            if(random.nextBoolean())
                x = w;
            else
                y = h;
        }
        
        asteroidRadius = 5 + random.nextInt(10);

        Vector velocity = new Vector(Math.cos(angle), Math.sin(angle));
        double speed = 6 + random.nextDouble() * 6;
        directionRay = new Ray(new Point(x, y), velocity, speed);
    }
    
    public void move(double time) {
        directionRay = new Ray(directionRay.endPoint(time), directionRay.v, directionRay.speed);
    }
    
    public Asteroid(ObjectInputStream in) throws IOException{
        asteroidRadius = in.readInt();
        directionRay = new Ray(in);
    }
    
    public void writeTo(ObjectOutputStream out) throws IOException{
        out.writeInt(asteroidRadius);
        directionRay.writeTo(out);
    }

    public Ray getRay() {
        return directionRay;
    }

    public void setRay(Ray ray) {
        this.directionRay = ray;
    }
    
    public int returnX(){
        return (int) directionRay.origin.x;
    }
    
    public int returnY(){
        return (int) directionRay.origin.y;
    }
    
    public int returnRadius(){
        return asteroidRadius;
    }

}
