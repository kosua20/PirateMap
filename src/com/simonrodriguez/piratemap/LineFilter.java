package com.simonrodriguez.piratemap;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.nio.Buffer;

/**
 * Created by simon on 05/12/2015.
 */
public class LineFilter {

    private BufferedImage im;
    private Color color;

    public LineFilter(BufferedImage image, Color baseColor){
        this.im = image;
        this.color = baseColor;
    }



    public BufferedImage filter(boolean noisy){
        BufferedImage gray = new BufferedImage(im.getWidth(),im.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int yes = color.getRGB();
        int half = new Color(color.getRed(),color.getGreen(),color.getBlue(),128).getRGB();
        int no = new Color(0,0,0,0).getRGB();

        int backgroundColor = im.getRGB(0,0);

        for(int x = 1; x < im.getWidth()-1; x++){
            for(int y = 1; y < im.getHeight()-1; y++){
                //Look at the neighbours
                gray.setRGB(x,y,no);
                testNeigh : for(int i=-1;i<2;i++){
                    for(int j=-1;j<2;j++){
                        if(i==0 && j==0){continue;}
                        if(im.getRGB(x,y) != im.getRGB(x+i,y+j)){
                            if(im.getRGB(x,y) == backgroundColor || im.getRGB(x+i,y+j) == backgroundColor){
                                gray.setRGB(x,y,yes);
                            } else {
                                if(!noisy || Math.random() < 0.9) {
                                    gray.setRGB(x, y, half);
                                }
                            }
                            break testNeigh;
                        }
                    }
                }
            }
        }

        return gray;
    }

    public BufferedImage renforce(BufferedImage gray, int count, float noiseFactor){

        for(int i = 0; i < count; i++){
            gray = renforce(gray, noiseFactor * i);
        }
        return gray;
    }


    public BufferedImage renforce(BufferedImage gray, float noiseFactor){
        int yes = color.getRGB();
        BufferedImage gray2 = PirateUtils.deepCopy(gray);
        int backgroundColor = im.getRGB(0,0);

        for(int x = 1; x < im.getWidth()-1; x++){
            for(int y = 1; y < im.getHeight()-1; y++){
                //Look at the neighbours
                testNeigh : for(int i=-1;i<2;i++){
                    for(int j=-1;j<2;j++){
                        if(i==0 && j==0){continue;}
                        if(gray.getRGB(x,y) != gray.getRGB(x+i,y+j) && im.getRGB(x,y) == backgroundColor){
                            if(Math.random() < 1.0 - noiseFactor) {
                                gray2.setRGB(x, y, yes);
                            }
                            break testNeigh;
                        }
                    }
                }
            }
        }

        return gray2;
    }



}
