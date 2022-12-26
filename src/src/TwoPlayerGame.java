import Textures.AnimListener;
import Textures.TextureReader;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;


public class TwoPlayerGame extends AnimListener {
    int animationIndex = 0;
    long timer = 0;
    double y0 = 8 ;
    int direction = 0; //0= right , 1 = left
    int direction1 = 1;
    int maxWidth = 100;
    int maxHeight = 100;
    int x = maxWidth / 2, y = maxHeight / 2;

    ArrayList<Bullet> bulletsPlayer1 = new ArrayList<Bullet>();
    ArrayList<Bullet> bulletsPlayer2 = new ArrayList<Bullet>();

    Player player1 = new Player(90, 5,100,false,"mahmoud");
    Player player2 = new Player(10, 5, 100, false, "maryam");

    ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    ArrayList<Bomb> Bomb = new ArrayList<Bomb>();

    String textureNames[] = { "tankp2.png","tankp1.png","plane.png", "Bomb_1.png","Bullet_1.png", "Back.png"};
    TextureReader.Texture texture[] = new TextureReader.Texture[textureNames.length];
    int textures[] = new int[textureNames.length];

    /*
     5 means gun in array pos
     x and y coordinate for gun
     */
    @Override
    public void init(GLAutoDrawable gld) {

        GL gl = gld.getGL();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);    //This Will Clear The Background Color To Black

        gl.glEnable(GL.GL_TEXTURE_2D);  // Enable Texture Mapping
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glGenTextures(textureNames.length, textures, 0);

        for (int i = 1; i < textureNames.length; i++) {
            try {
                texture[i] = TextureReader.readTexture(assetsFolderName + "//" + textureNames[i], true);
                gl.glBindTexture(GL.GL_TEXTURE_2D, textures[i]);

//                mipmapsFromPNG(gl, new GLU(), texture[i]);
                new GLU().gluBuild2DMipmaps(
                        GL.GL_TEXTURE_2D,
                        GL.GL_RGBA, // Internal Texel Format,
                        texture[i].getWidth(), texture[i].getHeight(),
                        GL.GL_RGBA, // External format from image,
                        GL.GL_UNSIGNED_BYTE,
                        texture[i].getPixels() // Imagedata
                );
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }

        for (int i = 0; i < textureNames.length; i++) {
            try {
                texture[i] = TextureReader.readTexture(assetsFolderName + "//" + textureNames[i], true);
                gl.glBindTexture(GL.GL_TEXTURE_2D, textures[i]);

//                mipmapsFromPNG(gl, new GLU(), texture[i]);
                new GLU().gluBuild2DMipmaps(
                        GL.GL_TEXTURE_2D,
                        GL.GL_RGBA, // Internal Texel Format,
                        texture[i].getWidth(), texture[i].getHeight(),
                        GL.GL_RGBA, // External format from image,
                        GL.GL_UNSIGNED_BYTE,
                        texture[i].getPixels() // Imagedata
                );
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        gl.glOrtho(0.0, 0.0, 0.0, 0.0, -1.0, 1.0);
    }

    @Override
    public void display(GLAutoDrawable gld) {

        GL gl = gld.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);       //Clear The Screen And The Depth Buffer
        gl.glLoadIdentity();
        //45

        DrawBackground(gl);
        handleKeyPress();

        animationIndex = animationIndex % 4;

        //Draw players
        DrawSprite(gl, player1.x, player1.y, 0, 1.2f);
        DrawSprite(gl, player2.x, player2.y, 1, 1.2f);

        // moving players
        movePlayer1();
        movePlayer2();


        // handle shoot for players
        if (timer %10 == 0) {
            shootBulletPlayer1();
            shootBulletPlayer2();

        }

        // player 1 bullets handler (move and remove the bullet)
        for(int i = 0; i < bulletsPlayer1.size(); i++){
            moveBulletPlayer1(gl,i);
            if (bulletsPlayer1.get(i).y > 100){
                bulletsPlayer1.remove(i);

            }
        }

        // player 2 bullets handler (move and remove the bullet)
        for(int i = 0; i < bulletsPlayer2.size(); i++){
            moveBulletPlayer2(gl,i);
            if (bulletsPlayer2.get(i).y > 100){
                bulletsPlayer2.remove(i);

            }
        }

        // adding new enemies
        if (timer % 100 == 0){
            enemies.add(new Enemy(-10,80,10));
            enemies.add(new Enemy(110,80,10));

        }

        // adding bombs to every enemy
        if (timer % 50 == 0) {
            for (int i = 0; i < enemies.size(); i++) {

                Bomb.add(new Bomb((int) enemies.get(i).x, (int) enemies.get(i).y, 10));


            }
        }

        // moving enemies (planes)
        for (int i = 0; i < enemies.size(); i++){

            movePlane(gl,i);

        }
        // moving enemie's bomb
        for (int i = 0; i < Bomb.size(); i++){

            moveBomb(gl,i);

        }
        // remove enemies > 100 on x
        for (int i = 0; i < enemies.size(); i++){

            if (enemies.get(i).x > 100){
                enemies.remove(i);
            }

        }
        // remove bomb < -2 on y
        for (int i = 0; i < Bomb.size(); i++){

            if (Bomb.get(i).y <= -2){
                Bomb.remove(i);
            }

        }
        // handle bomb hits th players
        for (int i = 0; i < Bomb.size(); i++){
            if (isCollied(5,2, player1.x, player1.y,Bomb.get(i).x,Bomb.get(i).y)){
                System.out.println("collied player 1");
                player1.health-=Bomb.get(i).damage;
                Bomb.remove(i);
                System.out.println(player1.health);

                if (player1.health <= 0){
                    player1.x = 200;
                }
            }
        }
        for (int i = 0; i < Bomb.size(); i++){
            if (isCollied(5,2, player2.x, player2.y,Bomb.get(i).x,Bomb.get(i).y)){
                System.out.println("collied player 2");
                player2.health-=Bomb.get(i).damage;
                Bomb.remove(i);
                System.out.println(player2.health);

                if (player2.health <= 0){
                    player2.x = 200;
                }
            }
        }

        // handle bullets p1 hits enemies
        for(int i = 0; i < bulletsPlayer1.size(); i++){
            for (int j = 0; j < enemies.size(); j++)
                if (isCollied(5,3, bulletsPlayer1.get(i).x, bulletsPlayer1.get(i).y,enemies.get(j).x,enemies.get(j).y)){
                    System.out.println("hit p1");
                    enemies.get(j).health -= bulletsPlayer1.get(i).damage;
                    bulletsPlayer1.remove(i);

                    if (enemies.get(j).health <= 0){
                        player1.count+=1;
                        enemies.remove(j);
                    }
                }
        }

        // handle bullets p2 hits enemies
        for(int i = 0; i < bulletsPlayer2.size(); i++){
            for (int j = 0; j < enemies.size(); j++)
                if (isCollied(5,3, bulletsPlayer2.get(i).x, bulletsPlayer2.get(i).y,enemies.get(j).x,enemies.get(j).y)){
                    System.out.println("hit p2");
                    enemies.get(j).health -= bulletsPlayer2.get(i).damage;
                    bulletsPlayer2.remove(i);

                    if (enemies.get(j).health <= 0){
                        player2.count+=1;
                        enemies.remove(j);
                    }
                }
        }





        timer++;

    }
    // move bombs
    private void moveBomb(GL gl, int i){

        DrawBomb( gl,Bomb.get(i).x ,  (Bomb.get(i).y-=Bomb.get(i).speed) , 3, 0.08F);
    }

    // draw bomb
    public void DrawBomb(GL gl, int x, int y, int index, float scale) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[index]);  // Turn Blending On

        gl.glPushMatrix();
        gl.glTranslated(x / (maxWidth / 2.0) - 0.9, y / (maxHeight / 2.0) - 0.9, 0);
        gl.glScaled( 0.02,   scale, 1);
        //System.out.println(x +" " + y);
        gl.glBegin(GL.GL_QUADS);
        // Front Face
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glEnd();
        gl.glPopMatrix();

        gl.glDisable(GL.GL_BLEND);
    }

    //  move planes(enemy)
    public void movePlane(GL gl,int i ){

        DrawPlane(gl, (int) enemies.get(i).x++ , (int) enemies.get(i).y,2,2);
    }
    // draw planes (enemy)
    public void DrawPlane(GL gl, int x, int y, int index, float scale) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[index]);  // Turn Blending On

        gl.glPushMatrix();
        gl.glTranslated(x / (maxWidth / 2.0) - 0.9, y / (maxHeight / 2.0) - 0.9, 0);
        gl.glScaled(0.1 * scale, 0.1 * scale, 1);
        //System.out.println(x +" " + y);
        gl.glBegin(GL.GL_QUADS);
        // Front Face
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glEnd();
        gl.glPopMatrix();

        gl.glDisable(GL.GL_BLEND);
    }

    private void shootBulletPlayer1() {
        if (isKeyPressed(KeyEvent.VK_SPACE)) {
            System.out.println("s");
            bulletsPlayer1.add(new Bullet(player1.x, player1.y, 10));

        }
    }

    private void shootBulletPlayer2() {
        if (isKeyPressed(KeyEvent.VK_NUMPAD0)) {
            System.out.println("0");
            bulletsPlayer2.add(new Bullet(player2.x, player2.y, 10));

        }
    }
    private boolean isCollied(double r1, double r2 , double x1, double y1, double x2, double y2){

        if (dist(x1, y1, x2, y2) < r1+r2 ){

            return true;
        }
        else {

            return false;
        }
    }
    private double dist(double x1, double y1, double x2, double y2){
        double x = x1 - x2;
        double y = y1 - y2;

        return Math.sqrt((x*x)+(y*y));
    }
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    @Override
    public void displayChanged(GLAutoDrawable glAutoDrawable, boolean b, boolean b1) {

    }


    public void DrawSprite(GL gl, int x, int y, int index, float scale ) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[index]);    // Turn Blending On
        // int angle = 0;
        //  switch(direction){
        // case 0 : angle =0;break;
        // case 1 : angle =180;break;
        //case 2 : angle =90;break;
        //  default :angle=0;
        // }
        gl.glPushMatrix();
        gl.glTranslated(x / (maxWidth / 2.0) - 1, y / (maxHeight / 2.0) -1, 0);
        gl.glScaled(0.25 * scale, 0.25 * scale, 1);
        //gl.glRotated(angle, 0, 0, 1);
        //System.out.println(x +" " + y);
        gl.glBegin(GL.GL_QUADS);

        // Front Face
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glEnd();
        gl.glPopMatrix();

        gl.glDisable(GL.GL_BLEND);
    }
    public void DrawSprite2(GL gl, int x, int y, int index, float scale, int direction) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[index+2]);    // Turn Blending On
        // int angle = 0;
        //  switch(direction){
        // case 0 : angle =0;break;
        // case 1 : angle =180;break;
        //case 2 : angle =90;break;
        //  default :angle=0;
        // }
        gl.glPushMatrix();
        gl.glTranslated(x / (maxWidth / 2.0) -1, y / (maxHeight / 2.0) - 1, 0);
        gl.glScaled(0.15 * scale, 0.15 * scale, 1);
        //gl.glRotated(angle, 0, 0, 1);
        //System.out.println(x +" " + y);
        gl.glBegin(GL.GL_QUADS);
        // Front Face
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glEnd();
        gl.glPopMatrix();

        gl.glDisable(GL.GL_BLEND);
    }
    public void DrawBullet(GL gl, int x, int y, int index, float scale) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[textureNames.length-2]);  // Turn Blending On

        gl.glPushMatrix();
        gl.glTranslated(x / (maxWidth / 2.0) - 0.9, y / (maxHeight / 2.0) - 0.9, 0);
        gl.glScaled(0.01,  scale, 1);
        //System.out.println(x +" " + y);
        gl.glBegin(GL.GL_QUADS);
        // Front Face
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glEnd();
        gl.glPopMatrix();

        gl.glDisable(GL.GL_BLEND);
    }

    // moving bullet for p1
    private void moveBulletPlayer1(GL gl, int i){

        DrawBullet( gl,bulletsPlayer1.get(i).x ,  (bulletsPlayer1.get(i).y++)*bulletsPlayer1.get(i).speed, 2, 0.08F);


    }

    // moving bullet for p1
    private void moveBulletPlayer2(GL gl, int i){

        DrawBullet( gl,bulletsPlayer2.get(i).x ,  (bulletsPlayer2.get(i).y++)*bulletsPlayer2.get(i).speed, 2, 0.08F);


    }


    public void DrawBackground(GL gl) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[textureNames.length - 1]);  // Turn Blending On

        gl.glPushMatrix();
        gl.glBegin(GL.GL_QUADS);


        // Front Face
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glEnd();
        gl.glPopMatrix();

        gl.glDisable(GL.GL_BLEND);
    }

    public BitSet keyBits = new BitSet(256);

    @Override
    public void keyTyped(KeyEvent ke) {

    }

    @Override
    public void keyPressed(final KeyEvent event) {
        int keyCode = event.getKeyCode();
        keyBits.set(keyCode);
    }
    public void movePlayer1(){
        if (isKeyPressed(KeyEvent.VK_A)) {
            if (x > 0) {
                player1.x--;
            }
            //   animationIndex++;

        } else {
            if (isKeyPressed(KeyEvent.VK_D)) {
                if (x > 0) {
                    player1.x++;
                }
                //   animationIndex++;


            }
        }
    }

    public void movePlayer2(){
        if (isKeyPressed(KeyEvent.VK_LEFT)) {
            if (x > 0) {
                player2.x--;
            }
            //   animationIndex++;

        } else {
            if (isKeyPressed(KeyEvent.VK_RIGHT)) {
                if (x > 0) {
                    player2.x++;
                }
                //   animationIndex++;


            }
        }
    }
    public void handleKeyPress() {



        if (isKeyPressed(KeyEvent.VK_1)) {
            if (x > 0) {
                x--;
            }
            //   animationIndex++;
            direction = 1;
        } else {
            if (isKeyPressed(KeyEvent.VK_2)) {
                if (x < maxWidth - 10) {
                    x++;
                }
                //   animationIndex++;
                direction = 0;
            } else {
                if (isKeyPressed(KeyEvent.VK_3)) {
                    if (y < maxHeight - 10) {
                        y++;
                    }
                    direction = 2;
                }
            }
        }
    }

    public boolean isKeyPressed( int keyCode) {
        return keyBits.get(keyCode);
    }

    public static void main(String[] args) {
        new anim2(new TwoPlayerGame());
    }



    @Override
    public void keyReleased(final KeyEvent event) {
        int keyCode = event.getKeyCode();
        keyBits.clear(keyCode);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}