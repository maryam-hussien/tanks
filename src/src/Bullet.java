public class Bullet {
    public double x;
    public double y;
    public int dir;
    long  start,lifeTime;
    float initX, initY;
    boolean fired;

    public Bullet(double x, double y, int dir ,int lifetime) {

        this.x = x;
        this.y = y;
        this.dir = dir;
        start = System.currentTimeMillis();
        lifeTime = lifetime;
        this.fired = true;

    }
    public void invalidate(){
        fired = start+lifeTime > System.currentTimeMillis();
        int dX = (int) ((initX - x) * (initX - x));
        int dY = (int) ((initY - y) * (initY - y));
        this.fired = dX + dY < 150;
    }
}