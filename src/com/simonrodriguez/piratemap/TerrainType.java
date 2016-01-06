package com.simonrodriguez.piratemap;

/**
 * Created by simon on 14/12/2015.
 */
public enum TerrainType {
    Water,
    Coast,
    Plain,
    Forest,
    Valley,
    Mountain;



    @Override
    public String toString(){
        return toString(false);
    }

    public String toString(boolean detailed) {
        switch (this) {
            case Water:
                return detailed ? "across the sea" : "sea";
            case Coast:
                return detailed ? "along the coast" : "coast";
            case Plain:
                return detailed ? "across the plain" : "plain";
            case Forest:
                return detailed ? "through the forest" : "forest";
            case Valley:
                return detailed ? "in the valley" : "valley";
            case Mountain:
                return detailed ? "across the mountains" : "mountains";
            default:
                return "";
        }
    }
}

