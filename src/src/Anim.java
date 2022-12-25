import Textures.AnimListener;
import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;


import javax.media.opengl.GLCanvas;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//import java.awt.event.ActionEvent
   public class Anim extends JFrame implements ActionListener {

    public Anim() {


    }

    private void setLocationRelativeTo(int i) {
    }

    private void add(JButton me) {
        
    }

    public static void main(String[] args) {
        new Anim(new AnimGLEventListener3());
    }
    //}

    public Anim(AnimListener aListener)  {
        GLCanvas glcanvas;
        Animator animator;

        AnimListener listener = aListener;
        glcanvas = new GLCanvas();
        glcanvas.addGLEventListener(listener);
        glcanvas.addKeyListener(listener);
        glcanvas.addMouseListener(listener);
        getContentPane().add(glcanvas, BorderLayout.CENTER);
        animator = new FPSAnimator(15);
        animator.add(glcanvas);
        animator.start();

        setTitle("Anim Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setVisible(true);
        setFocusable(true);
        glcanvas.requestFocus();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }


}
