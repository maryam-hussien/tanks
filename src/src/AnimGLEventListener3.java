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

public class AnimGLEventListener3 extends AnimListener {
    int direction = 0 ; //0= right , 1 = left


    // Download enemy textures from https://craftpix.net/freebies/free-monster-2d-game-items/

    double y0 = 5 ;
    int bulletsNumber =10;
    long timer = 0;
    int animationIndex = 0;
    int maxWidth = 100;
    int maxHeight = 100;
    int x = maxWidth / 2, y = maxHeight / 2;
    ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    ArrayList<Bomb> Bomb = new ArrayList<Bomb>();
    ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    Player player   = new Player(x, 0, 100, false, "mahmoud");

    // Download enemy textures from https://craftpix.net/freebies/free-monster-2d-game-items/
    String textureNames[] = {"plane_default.png","Bomb_1.png","Bullet_1.png","tank.png", "tank right.png", "tank left.png", "tank up.png", "Back.png"};
    TextureReader.Texture texture[] = new TextureReader.Texture[textureNames.length];
    int textures[] = new int[textureNames.length];

    /*
     5 means gun in array pos
     x and y coordinate for gun
     */
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
        //gl.glOrtho(-600.0, 600.0, -600.0, 600.0, -1.0, 1.0);
    }

    public void display(GLAutoDrawable gld) {

        GL gl = gld.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);       //Clear The Screen And The Depth Buffer
        gl.glLoadIdentity();

        DrawBackground(gl);
        handleKeyPress();


//        DrawGraph(gl);
        //DrawSprite(gl, x, (int) y0, animationIndex, 1 , direction);
        animationIndex = animationIndex % 5;

//        DrawGraph(gl);
        DrawSprite(gl, player.x, player.y, 3, 2.8f);
        //DrawPlane(gl, 0, 0, 0, 2);

        if (timer %3 == 0) {
            shootBullet();

        }


        if (timer % 100 == 0){
            enemies.add(new Enemy(-10,80,10));
        }
        if (timer % 50 == 0) {
            for (int i = 0; i < enemies.size(); i++) {

                Bomb.add(new Bomb((int) enemies.get(i).x, (int) enemies.get(i).y, 10));


            }
        }
        for (int i = 0; i < enemies.size(); i++){

            movePlane(gl,i);

        }
        for (int i = 0; i < Bomb.size(); i++){

            moveBomb(gl,i);

        }
        for (int i = 0; i < enemies.size(); i++){

            if (enemies.get(i).x > 100){
                enemies.remove(i);
            }

        }

        for (int i = 0; i < Bomb.size(); i++){

            if (Bomb.get(i).y <= -2){
                Bomb.remove(i);
            }

        }

        //bomb enemy hits the player
        for (int i = 0; i < Bomb.size(); i++){
            if (isCollied(3,2, player.x, player.y,Bomb.get(i).x,Bomb.get(i).y)){
                    System.out.println("collied");
                    player.health-=Bomb.get(i).damage;
                    Bomb.remove(i);
                    System.out.println(player.health);

                    if (player.health <= 0){
                        player.x = 200;
                    }
            }
        }

        for(int i = 0; i < bullets.size(); i++){
            moveBullet(gl,i);
            if (bullets.get(i).y > 100){
                bullets.remove(i);

            }
        }

        for(int i = 0; i < bullets.size(); i++){
            for (int j = 0; j < enemies.size(); j++)
            if (isCollied(3,4, bullets.get(i).x, bullets.get(i).y,enemies.get(j).x,enemies.get(j).y)){
                System.out.println("hit");
                enemies.get(j).health -= bullets.get(i).damage;
                bullets.remove(i);

                if (enemies.get(j).health <= 0){
                    player.count+=1;
                    enemies.remove(j);
                }
            }
        }

       /* System.out.println(bullets.size() +" bullets");
        System.out.println(player.count+ "score");*/


        timer++;






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

    private void moveBomb(GL gl, int i){

        DrawBomb( gl,Bomb.get(i).x ,  (Bomb.get(i).y-=Bomb.get(i).speed) , 1, 0.08F);
    }
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

    private void shootBullet(){
        if(isKeyPressed(KeyEvent.VK_SPACE)){
            System.out.println("s");
            bullets.add(new Bullet(player.x, player.y,10));

        }
    }

    private void moveBullet(GL gl, int i){

        DrawBullet( gl,bullets.get(i).x ,  (bullets.get(i).y++)*bullets.get(i).speed, 2, 0.08F);
    }
    public void DrawBullet(GL gl, int x, int y, int index, float scale) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[index]);  // Turn Blending On

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
    public void DrawSprite(GL gl, int x, int y, int index, float scale , int dir) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[index+1]);	// Turn Blending On
        int angle = 0;
        switch(direction){
            case 0 : angle =0;break;
            case 1 : angle =180;break;
            case 2 : angle =90;break;
            default :angle=0;
        }
        gl.glPushMatrix();
        gl.glTranslated(x / (maxWidth / 2.0) - 0.9, y / (maxHeight / 2.0) - 0.9, 0);
        gl.glScaled(0.1 * scale, 0.1 * scale, 1);
        gl.glRotated(angle, 0, 0, 1);
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
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    public void movePlane(GL gl,int i ){

        DrawPlane(gl, (int) enemies.get(i).x++ , (int) enemies.get(i).y,0,2);
    }

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

    public void DrawSprite(GL gl, int x, int y, int index, float scale) {
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

    public void DrawBackground(GL gl) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[textureNames.length -1]);  // Turn Blending On

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

    /*
     * KeyListener
     */


    public BitSet keyBits = new BitSet(256);

    @Override
    public void keyPressed(final KeyEvent event) {
        int keyCode = event.getKeyCode();
        keyBits.set(keyCode);
    }

    @Override
    public void keyReleased(final KeyEvent event) {
        int keyCode = event.getKeyCode();
        keyBits.clear(keyCode);
    }

    @Override
    public void keyTyped(final KeyEvent event) {
        // don't care
    }

    public void handleKeyPress() {

        if (isKeyPressed(KeyEvent.VK_LEFT)) {
            if (x > 0) {
                player.x--;
            }
            //   animationIndex++;
            direction = 1;
        } else {
            if (isKeyPressed(KeyEvent.VK_RIGHT)) {
                if (x < maxWidth - 10) {
                    player.x++;
                }
                //   animationIndex++;
                direction = 0;
            } else {
                if (isKeyPressed(KeyEvent.VK_UP)) {
                    if (y < maxHeight - 10) {
                     //   y++;
                    }
                    direction = 2;
                }
            }
        }
    }

    public boolean isKeyPressed(final int keyCode) {
        return keyBits.get(keyCode);
    }

    /*public static void main(String[] args) {
        new Anim(new AnimGLEventListener3());
    }*/

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
