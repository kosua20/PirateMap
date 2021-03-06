package com.simonrodriguez.piratemap;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;


public class PirateMapPanel extends JPanel {

    private static final int WIDTH = 768;
    private static final int HEIGHT = 768;
    private BufferedImage background;

    public PirateMapPanel(){
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    private void createMap() {
        PirateMap map = new PirateMap(WIDTH, HEIGHT/*,1450079587*/);
        map.generate();
        map.print();
        background = map.getImageRepresentation();
        File f = new File("map_" + new Date().toString() + "_" + map.seed + ".png");
        try {
            BufferedImage newImage = new BufferedImage(background.getWidth(), background.getHeight(), BufferedImage.TYPE_INT_RGB);
            int[] rgb = background.getRGB(0, 0, background.getWidth(), background.getHeight(), null, 0, background.getWidth());
            newImage.setRGB(0, 0, background.getWidth(), background.getHeight(), rgb, 0, background.getWidth());
            ImageIO.write(newImage, "PNG", f);
        } catch (Exception e){
            System.out.println("Error writing.");
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g){
        g.drawImage(background, 0, 0, WIDTH, HEIGHT, this);
    }



    public static void main(String[] args){
        PirateMapPanel rt = new PirateMapPanel();
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame f = new JFrame();
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setDefaultLookAndFeelDecorated(true);
                f.setResizable(false);
                rt.createMap();
                f.add(rt, BorderLayout.CENTER);
                f.pack();
                f.setVisible(true);
            }
        });

    }

}
