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
    Moutain;



    @Override
    public String toString() {
        switch (this) {
            case Water:
                return "across the sea";
            case Coast:
                return "along the coast";
            case Plain:
                return "across the plain";
            case Forest:
                return "through the forest";
            case Valley:
                return "in the valley";
            case Moutain:
                return "across the mountains";
            default:
                return "";
        }
    }
}

