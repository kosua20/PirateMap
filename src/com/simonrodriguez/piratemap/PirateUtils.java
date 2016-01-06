package com.simonrodriguez.piratemap;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

/**
 * Created by simon on 05/12/2015.
 */
public class PirateUtils {

    public static BufferedImage combineImages(BufferedImage foreground, BufferedImage background, int width, int height){
        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = combined.getGraphics();
        g.drawImage(background, 0, 0, null);
        g.drawImage(foreground, 0, 0, null);
        return combined;
    }

    public static BufferedImage addImage(BufferedImage foreground, int x, int y, BufferedImage background){
        Graphics g = background.getGraphics();
        g.drawImage(foreground,x,y,null);
        return background;
    }

    public static BufferedImage addOverlay(BufferedImage foreground, int x, int y, double alpha, int width, int height, BufferedImage background){
        Graphics2D g = (Graphics2D)background.getGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float)alpha));
        g.drawImage(foreground,x,y,width,height,null);
        return background;
    }

    public static double mix(double x, double y, double a){
        return x + a * (y-x);
    }

    public static Color mix(Color c1, Color c2, double a){
        double red = c1.getRed()+a*(c2.getRed()-c1.getRed());
        double green = c1.getGreen()+a*(c2.getGreen()-c1.getGreen());
        double blue = c1.getBlue()+a*(c2.getBlue()-c1.getBlue());
        return new Color((int)red,(int)green,(int)blue);
    }

    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static float[] makeGaussianKernel(int radius, float sigma) {
        float[] kernel = new float[radius * radius];
        float sum = 0;
        for (int y = 0; y < radius; y++) {
            for (int x = 0; x < radius; x++) {
                int off = y * radius + x;
                int xx = x - radius / 2;
                int yy = y - radius / 2;
                kernel[off] = (float) Math.pow(Math.E, -(xx * xx + yy * yy)
                        / (2 * (sigma * sigma)));
                sum += kernel[off];
            }
        }
        for (int i = 0; i < kernel.length; i++)
            kernel[i] /= sum;
        return kernel;
    }

    public static Direction getDirection(double dx, double dy){
        double norm2 = dx*dx+dy*dy;
        if(norm2 == 0) {
            return Direction.Stop;
        } else if (norm2 != 1.0){
            double norm = Math.sqrt(norm2);
            dx /= norm;
            dy /= norm;
        }

        if(Math.abs(dx) <= Math.cos(3.0*Math.PI/8.0)) {
            return (dy <= 0) ? Direction.Up : Direction.Down;
        } else if (Math.abs(dx) >= Math.cos(Math.PI/8.0)){
            return (dx >= 0) ? Direction.Right : Direction.Left;
        } else {
            if (dy <= 0) {
                return (dx >= 0) ? Direction.UpRight : Direction.UpLeft;
            } else {
                return (dx >= 0) ? Direction.DownRight : Direction.DownLeft;
            }
        }

    }

    public static TerrainType getType(double height){
        int value = (int)(255*height);
        if (value <= 2) {
           return TerrainType.Water;
        } else if (value <= 7) {
            return TerrainType.Coast;
        } else if (value <= 25) {
            return TerrainType.Plain;
        } else if (value <= 150) {
            return TerrainType.Forest;
        } else if (value <= 165) {
            return TerrainType.Valley;
        } else {
            return TerrainType.Mountain;
        }
    }
}
