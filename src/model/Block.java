package model;

import processing.core.PVector;

import java.util.Random;

public class Block {

    private int[] startValues = {2, 4};
    private int value;

    private PVector position;
    private PVector nextPosition;

    private boolean inAnimation;
    private boolean mergeRemove = false;
    private boolean unMerged = true;

    private JColor color;

    public Block(PVector position){
        this.position = position;
        this.nextPosition = position;
        this.value = startValues[new Random().nextInt(2)];
        changeColor();
    }

    public Block(int value, PVector position, PVector nextPosition, boolean unMerged, JColor color){
        this.value = value;
        this.position = position;
        this.nextPosition = nextPosition;
        this.unMerged = unMerged;
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

    public PVector getNextPosition() {
        return this.nextPosition;
    }

    public void setNextPosition(PVector nextPosition) {
        this.nextPosition = nextPosition;
    }

    public boolean isInAnimation() {
        return this.inAnimation;
    }

    public void setInAnimation(boolean inAnimation) {
        this.inAnimation = inAnimation;
    }

    public boolean isMergeRemove() {
        return mergeRemove;
    }

    public void setMergeRemove(boolean mergeRemove) {
        this.mergeRemove = mergeRemove;
    }

    public boolean isUnMerged() {
        return this.unMerged;
    }

    public void setUnMerged(boolean unMerged) {
        this.unMerged = unMerged;
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
