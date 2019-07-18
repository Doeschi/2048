package model;

public enum JColor {
    VALUE_2(255, 197, 5),
    VALUE_4(255, 173, 5),
    VALUE_8(255, 157, 5),
    VALUE_16(255, 130, 5),
    VALUE_32(231, 124, 5),
    VALUE_64(231, 106, 5),
    VALUE_128(231, 92, 5),
    VALUE_256(231, 76 , 5),
    VALUE_512(231, 51,  5),
    VALUE_1024(231, 31, 5),
    VALUE_2048(219, 0, 5),
    VALUE_OVER(245, 0, 115);


    private final int red;
    private final int green;
    private final int blue;

    JColor(int red, int green, int blue){
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    public int getRed(){
        return this.red;
    }

    public int getGreen(){
        return this.green;
    }

    public int getBlue(){
        return this.blue;
    }
}
