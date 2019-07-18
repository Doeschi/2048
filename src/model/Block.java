package model;

import processing.core.PVector;

import java.util.Random;

public class Block {

    private int[] startValues = {2, 4};
    private int value;
    private PVector position;
    private boolean unmerged = true;
    private JColor color;

    public Block(PVector position){
        this.position = position;
        this.value = startValues[new Random().nextInt(2)];
        changeColor();
    }

    public Block(int value, PVector position, boolean unmerged, JColor color){
        this.value = value;
        this.position = position;
        this.unmerged = unmerged;
        this.color = color;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value){
        this.value = value;
        changeColor();
    }

    public PVector getPosition() {
        return this.position;
    }

    public void setPosition(PVector position) {
        this.position = position;
    }

    public boolean isUnmerged() {
        return this.unmerged;
    }

    public void setUnmerged(boolean unmerged) {
        this.unmerged = unmerged;
    }

    public JColor getColor() {
        return this.color;
    }

    private void changeColor(){
        switch (value){
            case 2:
                color = JColor.VALUE_2;
                break;
            case 4:
                color = JColor.VALUE_4;
                break;
            case 8:
                color = JColor.VALUE_8;
                break;
            case 16:
                color = JColor.VALUE_16;
                break;
            case 32:
                color = JColor.VALUE_32;
                break;
            case 64:
                color = JColor.VALUE_64;
                break;
            case 128:
                color = JColor.VALUE_128;
                break;
            case 256:
                color = JColor.VALUE_256;
                break;
            case 512:
                color = JColor.VALUE_512;
                break;
            case 1024:
                color = JColor.VALUE_1024;
                break;
            case 2048:
                color = JColor.VALUE_2048;
                break;
            default:
                color = JColor.VALUE_OVER;
        }
    }
}
