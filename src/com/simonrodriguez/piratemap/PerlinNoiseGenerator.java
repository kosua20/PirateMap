package com.simonrodriguez.piratemap;

import java.util.Random;

public class PerlinNoiseGenerator {

    private static final int PERMUTATIONS = 256;
    private int[] permutations;

    private int number_cell = 4;
    private int octaves = 4;
    private double lacunarity = 1.2;
    private double h = 0.9;
    private double offset = 1.7;
    private double gain = 1.0;

    public int size;


    public PerlinNoiseGenerator(int seed, int size){
        Random random = new Random(seed);
        this.size = size;

        permutations = new int[PERMUTATIONS];
        for(int i = 0; i < PERMUTATIONS; i++) {
            permutations[i] = random.nextInt(256);
        }
    }

    public double noise(int x, int y){
        double noiseValue = fBm(x,y)+0.1;
        noiseValue = customLerp(x,y,0.2,0.4)*noiseValue;
        noiseValue = Math.min(1.0,Math.max(0.0,noiseValue));
        return noiseValue;
    }

    public double perlin(int xpos, int ypos, int zpos){
        double ratio = size / (double)number_cell;//<number of pixels per unit cell (length)

        //Compute coordinates of pixel in cells units
        double x = xpos / ratio;
        double y = ypos / ratio;
        double z = zpos / ratio;
        int j0 = (int)x;
        int i0 = (int)y;
        int k0 = (int)z;
        int i1 = i0 + 1;
        int j1 = j0 + 1;
        int k1 = k0 + 1;

        //Interpolation
        double f_x = f(x-j0);
        double f_y = f(y-i0);
        double f_z = f(z-k0);

        //Mixing gradients
        double x1 = grad(permutation(permutation(j0)+i0)+k0, x-j0 , y-i0  , z-k0);
        double x2 = grad(permutation(permutation(j1)+i0)+k0, x-j1, y-i0 , z-k0);
        double y1 = PirateUtils.mix(x1,x2,f_x);
        x1 =  grad(permutation(permutation(j0)+i1)+k0, x-j0  , y-i1, z-k0);
        x2 = grad(permutation(permutation(j1)+i1)+k0, x-j1, y-i1, z-k0   );
        double y2 = PirateUtils.mix(x1,x2,f_x);
        double z1 = PirateUtils.mix(y1,y2,f_y);
        x1 = grad(permutation(permutation(j0)+i0)+k1, x-j0  , y-i0  , z-k1 );
        x2 = grad(permutation(permutation(j1)+i0)+k1, x-j1, y-i0  , z-k1 );
        y1 = PirateUtils.mix(x1,x2,f_x);
        x1 = grad(permutation(permutation(j0)+i1)+k1, x-j0, y-i1, z-k1 );
        x2 = grad(permutation(permutation(j1)+i1)+k1, x-j1, y-i1, z-k1 );
        y2 = PirateUtils.mix(x1,x2,f_x);
        double z2 = PirateUtils.mix(y1,y2,f_y);

        return PirateUtils.mix(z1,z2,f_z);
    }

    private double fBm(int x, int y){
        double total = 0.0;
        for(int i = 0; i < octaves; i++){
            total += perlin(x,y,i) * Math.pow(lacunarity, -h * i);
            x *= lacunarity;
            y *= lacunarity;
        }
        return total;
    }

    private int permutation(int x){
        return permutations[x%256];
    }

    //Interpolation function
    private double f(double x) {
        return 6.0 * Math.pow(x,5) - 15.0 * Math.pow(x,4) + 10.0 * Math.pow(x,3);
    }

    //Returns a gradient based on the value of the hash
    //They all have the same length
    private double grad(int hash, double x, double y, double z) {
        switch(hash & 0xF){
            case 0x0: return  x + y;
            case 0x1: return -x + y;
            case 0x2: return  x - y;
            case 0x3: return -x - y;
            case 0x4: return  x + z;
            case 0x5: return -x + z;
            case 0x6: return  x - z;
            case 0x7: return -x - z;
            case 0x8: return  y + z;
            case 0x9: return -y + z;
            case 0xA: return  y - z;
            case 0xB: return -y - z;
            case 0xC: return  y + x;
            case 0xD: return -y + z;
            case 0xE: return  y - x;
            case 0xF: return -y - z;
            default: return 0.0;
        }
    }

    private double customLerp(int x, int y, double min, double max) {
        double norm = (((double) x / size) - 0.5) * (((double) x / size) - 0.5) + (((double) y / size) - 0.5) * (((double) y / size) - 0.5);
        norm = Math.sqrt(norm);
        double shift = perlin(x, y, -10);
        double alpha = 0.5;
        double newMax = max * (1 - (shift) * alpha);
        double newMin = min * (1 - (shift) * alpha);
        if (norm < newMin) {
            return 1.0;
        } else if (norm > newMax) {
            return 0.0;
        } else {
            return 1.0 * (newMax - norm) / (newMax - newMin);
        }
    }

}
