package com.simonrodriguez.piratemap;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.Date;

public class PirateMap {

    //DONE: multiple pictures
    //TODO: Creatures in the sea
    //TODO: Compass
    //DONE: Waves
    //DONE: Mountains at highest points
    //DONE: Red X
    //DONE: Path to X
    //TODO: Rivers
    //TODO: plants, trees, ruins
    //TODO: print directions
    //TODO: names of places
    //TODO: detailed directions

    private int width;
    private int height;
    private double[][] map;
    public int seed;
    private RandomSuite random;
    private int[][] mnt;
    private ArrayList<int[]> waves;
    private int[] end = new int[]{0,0};
    private int[] start = new int[]{0,0};
    private ArrayList<int[]> path;
    private int mountainsCount;

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

    public void generate(){
        this.populateWithNoise();
        mountainsCount = 5 + random.nextInt(10);
        this.findMountains(1500);
        this.addWaves();
        this.placeX();
        this.findPath();
    }

    private void populateWithNoise(){
        for(int x=0; x < width; x++){
            for(int y = 0; y < height ; y++){
                map[y][x] = random.noise(x,y);
            }
        }
    }

    private void addWaves() {
        waves = random.poissonGrid(width,height,140);
    }

    private void findMountains(int threshold) {
        mnt = new int[mountainsCount][2];

        for(int c = 0; c<mountainsCount;c++) {
            double currentMax = 0.0;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if(map[y][x] > currentMax) {//between 0 and 1;
                        boolean good = true;
                        for(int p = 0; p < c; p++){
                            if(Math.pow((mnt[p][0]-x),2) + Math.pow((mnt[p][1]-y),2)<threshold){
                                good = false;
                                break;
                            }
                        }
                        if(good){
                            currentMax = map[y][x];
                            mnt[c][0] = x;
                            mnt[c][1] = y;
                        }
                    }

                }
            }
        }

    }

    private void placeX(){
        int x, y, x0, y0;
        do {
            x = random.nextInt(width);
            y = random.nextInt(height);
        } while(waterAround(x,y,40,40));
        end[0] = x;
        end[1] = y;
        double distance = 400.0;
        do {
            x0 = random.nextInt(width);
            y0 = random.nextInt(height);
            distance -= 0.2;
        } while(waterAround(x0,y0,0,0) || closeTo(x0,y0,x,y,distance));
        start[0] = x0;
        start[1] = y0;
        System.out.println(x0 + ", " + y0);
    }




    private void findPath(){
        //We want to go from start to end
        path = new ArrayList<int[]>();
        path.add(start);
        int diffusion = 30;

        int interestCount = 1;
        for(int c = 0; c < interestCount+1;c++) {
            //We pick points of interest randomly on the map
            int x0,y0;
            int distance = 120;

            if(c==interestCount) {
                x0 = end[0];
                y0 = end[1];
            } else {
                do {
                    x0 = random.nextInt(width);
                    y0 = random.nextInt(height);
                    distance -= 0.02;
                } while (waterAround(x0, y0, 0, 0)
                        || Math.abs(x0 - path.get(path.size()-1)[0])< diffusion
                        || Math.abs(y0 - path.get(path.size()-1)[1])< diffusion
                        || closeTo(x0, y0, path.get(path.size()-1)[0], path.get(path.size()-1)[1], distance)
                        || closeTo(x0, y0, end[0], end[1], distance) || closeTo(x0, y0, start[0], start[1], distance)
                        );
            }
            double dx0 = path.get(path.size()-1)[0];
            double dy0 = path.get(path.size()-1)[1];
            double dx = x0 - dx0;
            double dy = y0 - dy0;
            double distance2 = dx*dx + dy*dy;
            double factorDistance = distance2/((height+width)*(height+width)*0.5*0.5);

            int borne = (int)Math.floor(5*((factorDistance-0.02)/0.4)+1)+1;
            System.out.println("D: " + borne);
            int shift = 40;
           for (int i = 1; i < borne; i++) {
                int xx = (int) (dx0 + (dx / (double) borne) * i);
                int yy = (int) (dy0 + (dy / (double) borne) * i);
                //if(map[yy][xx] > 0){
                    xx += random.nextIntIn(-shift, shift);
                  yy += random.nextIntIn(-shift, shift);
                //}

                path.add(new int[]{xx,yy});
            }
            path.add(new int[]{x0,y0});
        }

        int[] p0 = path.get(0);

        for(int i = 1; i < path.size();i++){
            int[] p1 = path.get(i);
            int dx = p1[0]-p0[0];
            int dy = p1[1]-p0[1];
            Direction dir = PirateUtils.getDirection(dx,dy);
            //double avgState = (map[p0[1]][p0[0]] + map[p1[1]][p1[0]])*0.5;
            double avgState = map[(p0[1]+p1[1])/2][(p0[0]+p1[0])/2];
            System.out.println("Go " + dir.toString() + " for " + (int)(Math.sqrt(dx*dx+dy*dy)*0.5) + " steps." + "("+ avgState +")");

            p0 = path.get(i);
        }
    }



    private boolean groundAround(int x, int y, int w, int h){
        //quick shortcut at center
        if (y >= 0 && x>=0 && y < height && x < width && map[y][x] > 0.0){
            return true;
        }
        //the full check
        for(int px = Math.max(0,x - w); px <= Math.min(width-1,x + w); px++){
            for(int py = Math.max(0,y - h); py <= Math.min(height-1,y + h); py++){
                if (map[py][px] > 0.0){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean waterAround(int x, int y, int w, int h){
        //quick shortcut at center
        if (y >= 0 && x>=0 && y < height && x < width && map[y][x] <= 0.0){
            return true;
        }
        //the full check
        for(int px = Math.max(0,x - w); px <= Math.min(width-1,x + w); px++){
            for(int py = Math.max(0,y - h); py <= Math.min(height-1,y + h); py++){
                if (map[py][px] <= 0.0){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean closeTo(int x1, int y1, int x2, int y2, double margin){
        return Math.pow(x1-x2,2) + Math.pow(y1-y2,2) < Math.pow(margin,2);
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


    private BufferedImage getBackground() throws IOException {
        BufferedImage bg = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        int count = 5;
        int[] randomOctaves = new int[count];
        for(int i = 0; i < count; i++){
            randomOctaves[i] = random.nextIntIn(3,8);
        }
        Color[] colors = {new Color(245,228,201),new Color(251,240,193),new Color(222,209,150),new Color(222,196,171),new Color(251,240,187)};

        for (int x = 0; x < bg.getWidth(); x++) {
            for (int y = 0; y < bg.getHeight(); y++) {
                double value;
                Color col = new Color(247,236,200);
                for(int i = 0; i < count; i ++){
                    value  = Math.min(1.0,Math.max(0.0,random.perlin(x,y,i+1, randomOctaves[i])));
                    col = PirateUtils.mix(col,colors[i],value);
                }
                bg.setRGB(x,y,col.getRGB());
            }
        }

        BufferedImage trame = ImageIO.read(new File("ressources/bg/bg_" + random.nextIntIn(1,6) + ".jpg"));
        bg = PirateUtils.addOverlay(trame,0,0,0.5,width,height,bg);
        return bg;

    }

    public BufferedImage getImageRepresentation() {
        BufferedImage result = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        BufferedImage image = getColoredMap(true);
        Color baseColor = new Color(87,73,64);
        LineFilter lif = new LineFilter(image, baseColor);
        int lineWidth = 4;
        BufferedImage drawing =  lif.renforce(lif.filter(true),lineWidth,0.1f);

        try {
            BufferedImage bg = this.getBackground();
            result = PirateUtils.combineImages(drawing,bg,width,height);
            ArrayList<BufferedImage> mounts = new ArrayList<>();
            for(int i = 1; i < 8; i++){
                BufferedImage mount = ImageIO.read(new File("ressources/mount/mount_" + i + ".png"));
                mounts.add(mount);
            }

            int moutainShift = 8;
            int size = mounts.get(0).getWidth()/2;
            for(int i = 0; i < mountainsCount;i++){
                PirateUtils.addImage(mounts.get(random.nextInt(mounts.size())),mnt[i][0]-size+random.nextIntIn(-moutainShift,moutainShift),mnt[i][1]-size+random.nextIntIn(-moutainShift,moutainShift),result);
            }


            BufferedImage[] wavesPic = new BufferedImage[7];
            for(int i = 0; i< wavesPic.length; i++){
                wavesPic[i] = ImageIO.read(new File("ressources/wave/wave_" + (i+1) + ".png"));
            }


            for(int[] w: waves){
                BufferedImage wave = wavesPic[random.nextInt(wavesPic.length)];
                int waveW = (int)(wave.getWidth()*0.5);
                int waveH = (int)(wave.getHeight()*0.5);
                int cx = w[0]-waveW;
                int cy = w[1] - waveH;
                if(!groundAround(cx,cy,waveW+2*lineWidth,waveH+2*lineWidth)) {
                    PirateUtils.addImage(wave, cx,cy, result);
                }
            }

            BufferedImage startPic = ImageIO.read(new File("ressources/spot1.png"));
            PirateUtils.addImage(startPic,start[0]-startPic.getWidth()/2,start[1]-startPic.getHeight()/2,result);

            BufferedImage crossPic = ImageIO.read(new File("ressources/cross1.png"));
            PirateUtils.addImage(crossPic,end[0]-crossPic.getWidth()/2,end[1]-crossPic.getHeight()/2,result);

            Graphics2D g = (Graphics2D)result.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(173,17,0));


            int[] xPoints = new int[path.size()];
            int[] yPoints = new int[path.size()];
            float[] dash = new float[2*path.size()];

            for(int i = 0; i <path.size() ; i++){
                xPoints[i] = path.get(i)[0];
                yPoints[i] = path.get(i)[1];
                dash[2*i] = (float)random.nextIntIn(15,25);
                dash[2*i+1] = (float)random.nextIntIn(10,20);

               //g.drawRect(path.get(i)[0],path.get(i)[1],10,10);
               // System.out.println(i+": ("+path.get(i)[0]+","+path.get(i)[1]+")");

            }
            g.setStroke(new BasicStroke(7.0f,                     // Line width
                    BasicStroke.CAP_ROUND,    // End-cap style
                    BasicStroke.JOIN_ROUND, 10.0f, dash,0.0f));

            /*
            double scale = 0.0;
            for(int i = 0; i <path.size()-1 ; i++) {
                QuadCurve2D q = new QuadCurve2D.Float();

                double[] normal = {path.get(i)[1]-path.get(i+1)[1], path.get(i+1)[0]-path.get(i)[0]};
                double norm = Math.sqrt(normal[0]*normal[0]+normal[1]*normal[1]);
                normal[0] /= norm;
                normal[1] /= norm;
                q.setCurve(path.get(i)[0], path.get(i)[1], (path.get(i)[0] + path.get(i + 1)[0]) * 0.5 + normal[0]*scale, (path.get(i)[1] + path.get(i + 1)[1]) * 0.5 + normal[1]*scale, path.get(i + 1)[0], path.get(i + 1)[1]);
                g.draw(q);
            }*/


            g.drawPolyline(xPoints,yPoints,path.size());




        }catch (IOException e){
            System.out.println("Error loading backgrounds");
        }

        return result;

    }



}
