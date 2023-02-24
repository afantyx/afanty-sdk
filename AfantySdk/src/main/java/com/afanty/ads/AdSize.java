package com.afanty.ads;


public final class AdSize {
    public static final AdSize BANNER = new AdSize(320, 50);
    public static final AdSize MEDIUM_RECTANGLE = new AdSize(300, 250);

    private final int width;
    private final int height;

    private AdSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return width + "*" + height;
    }

}
