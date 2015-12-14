package com.simonrodriguez.piratemap;

/**
 * Created by simon on 14/12/2015.
 */
public enum Direction {
    Up,
    UpRight,
    Right,
    DownRight,
    Down,
    DownLeft,
    Left,
    UpLeft,
    Stop;


    @Override
    public String toString(){
        switch(this){
            case Up:
                return "north";
            case UpRight:
                return "northeast";
            case Right:
                return "east";
            case DownRight:
                return "southeast";
            case Down:
                return "south";
            case DownLeft:
                return "southwest";
            case Left:
                return "west";
            case UpLeft:
                return "northwest";
            default:
                return "";
        }
    }
}
