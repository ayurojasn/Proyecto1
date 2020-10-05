package Interface;

import javax.swing.*;


public class Principal {
    private static Pantalla Pantall;

    public static void main(String[] foo) throws Exception {
        //Se inicia la pantalla
        Pantall = new Pantalla();
        JFrame frame = new JFrame("Principal");
        frame.setContentPane(new Pantalla().getPanel1());
        frame.setSize(1080,720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
