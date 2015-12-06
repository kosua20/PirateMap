package com.simonrodriguez.piratemap;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

/**
 * Created by simon on 05/12/2015.
 */
public class PirateUtils {

    public static double mix(double x, double y, double a){
        return x + a * (y-x);
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
}
