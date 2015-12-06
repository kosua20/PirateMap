package com.simonrodriguez.piratemap;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.Date;
import java.util.Random;

public class PirateMap {

    //TODO: multiple backgrounds
    //TODO: Creatures in the sea
    //TODO: Waves and compass
    //TODO: Mountains at highest points
    //TODO: Red X
    //TODO: Path to X
    //TODO: Rivers
    //TODO: plants, trees, ruins
    //TODO: print directions
    //TODO: names of places
    //TODO: detailed directions

    private int width;
    private int height;
    private double[][] map;
    private int seed;
    private RandomSuite random;

    public PirateMap(int width, int height){
        this(width, height,(int)(new Date().getTime()/1000));
    }

    public PirateMap(int width, int height, int seed){
        this.width = width;
        this.height = height;
        this.seed = seed;
        this.random = new RandomSuite(seed, width);
        map = new double[height][width];

    }

    public void populateWithNoise(){
        for(int x=0; x < width; x++){
            for(int y = 0; y < height ; y++){
                map[y][x] = random.noise(x,y);
            }
        }
    }


    private BufferedImage rawImage(){
        BufferedImage raw = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < raw.getWidth(); x++) {
            for (int y = 0; y < raw.getHeight(); y++) {
                float value = (float)(map[y][x]);
                raw.setRGB(x,y,(new Color(value,value,value)).getRGB());
            }
        }
        return raw;
    }


    private BufferedImage getColoredMap(boolean postprocess){
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int color1 = new Color(0.1f, 0.1f, 0.5f).getRGB();
        int color2 = new Color(0.1f, 0.3f, 0.8f).getRGB();
        int color3 = new Color(0.8f, 0.7f, 0.3f).getRGB();
        int color4 = new Color(0.1f, 0.6f, 0.2f).getRGB();
        int color5 = new Color(0.3f, 0.2f, 0.0f).getRGB();

        BufferedImage raw = this.rawImage();

        if(postprocess){
            float[] kernelMatrix = PirateUtils.makeGaussianKernel(5,1.7f);
            BufferedImageOp blurFilter = new ConvolveOp(new Kernel(5, 5, kernelMatrix), ConvolveOp.EDGE_NO_OP, null);
            raw = blurFilter.filter(raw,null);
        }

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int value = new Color(raw.getRGB(x,y)).getRed();
                if (value <= 2) {
                    image.setRGB(x, y, color1);
                } else if (value <= 7) {
                    image.setRGB(x, y, color2);
                } else if (value <= 25) {
                    image.setRGB(x, y, color3);
                } else if (value <= 150) {
                    image.setRGB(x, y, color4);
                } else if (value <= 165) {
                    image.setRGB(x, y, color2);
                } else if (value <= 170) {
                    image.setRGB(x, y, color3);
                } else {
                    image.setRGB(x, y, color5);
                }
            }
        }

        return  image;
    }

    public BufferedImage getImageRepresentation() {
        BufferedImage image = getColoredMap(true);
        Color baseColor = new Color(87,73,64);
        LineFilter lif = new LineFilter(image, baseColor);
        BufferedImage drawing =  lif.renforce(lif.filter(true),4,0.1f);
        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        try {
            BufferedImage bg = ImageIO.read(new File("ressources/bg1.jpg"));

            Graphics g = combined.getGraphics();
            g.drawImage(bg, 0, 0, null);
            g.drawImage(drawing, 0, 0, null);
        } catch (IOException e){
            System.out.println("Error loading backgrounds");
        }
        return combined;

    }
/*float[] matrix = { 1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f,  1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f };

        background = colorize
        */


}
