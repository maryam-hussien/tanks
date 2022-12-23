/*public class Bullet {
    long start, lifeTime;
    Directions direction;
    float x, y;
    boolean fired;
    float initX, initY;
    Bullet bullet;

    public Bullet(Directions direction, float x, float y, int lifetime){
        this.direction = direction;
        this.x = initX = x;
        this.y = initY = y;
        this.fired = true;
        start = System.currentTimeMillis();
        lifeTime = lifetime;

        public void invalidate(){
            fired = start+lifeTime > System.currentTimeMillis();
            int dX = (int) ((initX - x) * (initX - x));
            int dY = (int) ((initY - y) * (initY - y));
            this.fired = dX + dY < 150;
        }
}*/
