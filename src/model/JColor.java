package model;

public enum JColor {
    VALUE_2(238, 228, 218),
    VALUE_4(237, 224, 200),
    VALUE_8(242, 177, 121),
    VALUE_16(245, 149, 99),
    VALUE_32(246, 124, 95),
    VALUE_64(246, 94, 59),
    VALUE_128(237, 207, 114),
    VALUE_256(237, 204, 97),
    VALUE_512(237, 200, 80),
    VALUE_1024(237, 197, 63),
    VALUE_2048(237, 194, 46),
    VALUE_OVER(245, 0, 115),
    GAMEOVER(222, 20, 20);

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
