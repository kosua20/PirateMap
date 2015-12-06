package com.simonrodriguez.piratemap;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;


public class PirateMapPanel extends JPanel {

    private static final int WIDTH = 768;
    private static final int HEIGHT = 768;
    private BufferedImage background;

    public PirateMapPanel(){
        setPreferredSize(new Dimension(WIDTH, HEIGHT));


        /*for(int i = 0 ; i < 4; i++){
            int seedi = (int)Math.ceil(Math.random()*100000.0);
            PerlinNoiseGenerator geni = new PerlinNoiseGenerator(seedi, WIDTH,HEIGHT);
            BufferedImage map = geni.generateNoiseMap();
            //BufferedImage res = blurFilter.filter(map, null);
            File f = new File("map_" + seedi + ".png");
            try {
                ImageIO.write(map, "PNG", f);
            } catch (Exception e){
                System.out.println("Error writing.");
            }
        }*/
    }

    private void createMap() {
        PirateMap map = new PirateMap(WIDTH, HEIGHT);
        map.populateWithNoise();
        map.findMountains();
        map.addWaves();
        background = map.getImageRepresentation();
        File f = new File("output/map_" + new Date().toString() + ".png");
        try {
            ImageIO.write(background, "PNG", f);
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
