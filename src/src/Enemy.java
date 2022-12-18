public class Enemy {
    private double x;
    private double y;
    private int health;

    public Enemy(double x, double y, int health) {
        this.x = x;
        this.y = y;
        this.health = health;
    }

    public double getX() {
        return x;
    }

    public int getHealth() {
        return health;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
