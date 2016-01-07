package com.simonrodriguez.piratemap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

        indices.put(TerrainType.Water,nextInt(6));
        indices.put(TerrainType.Coast,nextInt(6));
        indices.put(TerrainType.Plain,nextInt(6));
        indices.put(TerrainType.Forest,nextInt(6));
        indices.put(TerrainType.Valley,nextInt(6));
        indices.put(TerrainType.Mountain ,nextInt(6));


        events.put(TerrainType.Water, new String[]{"You'll reach an old buoy.","This is where my ship sank.","Here be sharks.","You'll be above an old shipwreck.","Beware of the mermaids in this area!","There are reefs here."});
        events.put(TerrainType.Coast,new String[]{"You want to avoid the crabs.","Avoid the quicksands on the left.","Coconuts!","Cross the creek.","A key is hidden under the root of the big palm tree.","Dig here, you'll find another map."});
        events.put(TerrainType.Plain,new String[]{"Cross the river.","Under the tree here is buried my first mate.","Don't eat the berries from the bushes in this area.","Avoid the river, full of piranhas.","Salted beef in a barrel!","The hanging tree..."});
        events.put(TerrainType.Forest,new String[]{"Beware of the traps!","You'll reach the cannibals village.","Under the mossy tree, you'll find an old bottle of rum!","Don't touch the sacred totem.","Don't disturb the monkeys in the trees.","We killed a boar once here."});
        events.put(TerrainType.Valley,new String[]{"There is an old man in a hut, avoid him.","Under the piled rocks, I've hidden a key.","We put traps here, beware.","The valley of Snakes, we called it. Run.","You just reached our secret hideout!","Ten gallons of rums!"});
        events.put(TerrainType.Mountain,new String[]{"In the cave on the right there is a wild beast.","You will have to climb the cliff.","Follow the ledge.","Beware of rockfalls.","The slope is steep, but it'll be worth it.","Enjoy the view!"});

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

    public double nextDouble(double lower, double upper){

        return lower + mainRandom.nextDouble()*(upper - lower);
    }

    public boolean nextBool(){
        return nextDouble() < 0.5;
    }

    public double perlin(int x, int y, int z, int cells){
        return  perlin.perlin(x,y,z,cells);
    }

    public double perlin(int x, int y, int z){
        return  perlin.perlin(x,y,z);
    }

    public double fBm(int x, int y){
        return perlin.fBm(x,y);
    }

    public double perlin(int x, int y){
        return  perlin.perlin(x,y,0);
    }

    public double noise(int x, int y){
        return perlin.noise(x,y);
    }

    public ArrayList<int[]> poissonGrid(int width, int height, int r){
        double size = r / Math.sqrt(2);
        double r2 = r*r;
        int k = 30;
        double A = 2.0/(3.0*r2);
        int gridHeight = (int)Math.ceil(height/size);
        int gridWidth = (int)Math.ceil(width/size);
        int[][][] grid = new int[2][gridHeight][gridWidth];
        //System.out.println(gridWidth + "x" + gridHeight);

        for(int t=0; t < 2; t++){
            for(int s=0; s < gridHeight; s++){
                for(int q=0; q < gridWidth; q++){
                    grid[t][s][q] = -1;
                }
            }
        }


        ArrayList<int[]> activeList = new ArrayList<>();

        //Draw x0 at random
        int x0 = nextInt(width);
        int y0 = nextInt(height);
        //Compute in which cell it is
        int i0 = (int)Math.floor(x0 / size);
        int j0 = (int)Math.floor(y0 / size);


        grid[0][j0][i0] = x0;
        grid[1][j0][i0] = y0;

        activeList.add(new int[]{i0,j0});

        while (!activeList.isEmpty()){
            int index = nextInt(activeList.size());
            int[] drawIndices = activeList.get(index);

            int ik = drawIndices[0];
            int jk = drawIndices[1];
            int xk = grid[0][jk][ik];
            int yk = grid[1][jk][ik];

            int trials = 0;
            trial: while(trials < k){
                trials++;
                double theta = nextDouble(0.0,2.0*Math.PI);
                double radius = Math.sqrt(nextDouble()*3.0*r2 + r2);
                int xn = (int)(xk + radius * Math.cos(theta));
                int yn = (int)(yk + radius * Math.sin(theta));
                //Compute in which cell it is
                int in = (int)Math.floor(xn / size);
                int jn = (int)Math.floor(yn / size);
                if(jn < 0 || in < 0 || in >= gridWidth || jn >= gridHeight){
                    continue;
                }
                if(grid[0][jn][in] != -1){//ooops, already a sample in this cell
                    continue;
                }

                for(int i = Math.max(0,in-1); i < Math.min(gridWidth,in+2); i++){
                    for(int j = Math.max(0,jn-1); j < Math.min(gridHeight,jn+2); j++) {
                        if(j!=jn && i!=in){//Ignore the cell containing (xn,yn)

                            if(grid[0][j][i] != -1){//there is a sample in this cell
                                double distance = Math.pow(xn-grid[0][j][i],2) + Math.pow(yn-grid[1][j][i],2);
                                if(distance < r2){
                                    continue trial;
                                }
                            }
                        }
                    }
                }
                //If we reached this point, this means we have a point far enough from all other points
                grid[0][jn][in] = xn;
                grid[1][jn][in] = yn;
                activeList.add(new int[]{in,jn});
            }
            if(trials>=30){
                //Not found a satisfying element
                activeList.remove(index);
            }
        }

        ArrayList<int[]> finalList = new ArrayList<>();
        for(int i = 0; i <gridWidth; i++){
            for(int j = 0; j <gridHeight; j++){
                int xk = grid[0][j][i];
                int yk = grid[1][j][i];
                if(xk >=0 && yk >=0 && xk < width && yk < height){
                    //We have an sample point
                    finalList.add(new int[]{xk,yk});
                }
            }
        }

        return finalList;

    }

    private HashMap<TerrainType, String[]> events = new HashMap<>();
    private HashMap<TerrainType, Integer> indices = new HashMap<>();

    public String getRandomEvent(TerrainType type){
        indices.put(type, (indices.get(type)+1)%6);
        return events.get(type)[indices.get(type)];
    }
}
