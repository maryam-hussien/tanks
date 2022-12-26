/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hp
 */
public class Player {

    public int x;
    public int y;
    public int health;
    public int count;
    public boolean isDead;

    public String name;

    public Player(int x, int y , int health, boolean isDead,String name) {
        this.x = x;
        this.y = y;
        this.health = health;
        this.name = name;
        this.count = 0;
        this.isDead = isDead;

    }
    
}
