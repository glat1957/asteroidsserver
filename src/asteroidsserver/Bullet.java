package asteroidsserver;

public class Bullet {

    int bulletX = 0;
    int bulletY = 0;

    public void getBulletLocation(int x, int y) {
        bulletX = x;
        bulletY = y;
    }

    public int returnBulletX() {
        return bulletX;
    }

    public int returnBulletY() {
        return bulletY;
    }

}
