package com.simonrodriguez.piratemap;

import java.util.Random;

/**
 * Created by simon on 05/12/2015.
 */
public class RandomSuite {

    private Random mainRandom;
    private PerlinNoiseGenerator perlin;

    public RandomSuite(int seed, int size){
            mainRandom = new Random(seed);
            perlin = new PerlinNoiseGenerator(mainRandom.nextInt(),size);
    }

    public int nextInt(){
        return mainRandom.nextInt();
    }

    public float nextFloat(){
        return mainRandom.nextFloat();
    }

    public double nextDouble(){
        return mainRandom.nextDouble();
    }

    public int nextInt(int bound){
        return mainRandom.nextInt(bound);
    }

    public int nextIntIn(int lower, int upper){

        return lower + mainRandom.nextInt(upper - lower + 1);
    }

    public double perlin(int x, int y, int z){
        return  perlin.perlin(x,y,z);
    }

    public double perlin(int x, int y){
        return  perlin.perlin(x,y,0);
    }

    public double noise(int x, int y){
        return perlin.noise(x,y);
    }

}
