package com.intion.proxy.utils;

public class Math {

    public static int floorDouble(double n) {
        int i = (int) n;
        return n >= i ? i : i - 1;
    }

    public static int ceilDouble(double n) {
        int i = (int) (n + 1);
        return n >= i ? i : i - 1;
    }

    public static int floorFloat(float n) {
        int i = (int) n;
        return n >= i ? i : i - 1;
    }

    public static int ceilFloat(float n) {
        int i = (int) (n + 1);
        return n >= i ? i : i - 1;
    }

    public static int randomRange(BungeeRandom random) {
        return randomRange(random, 0);
    }

    public static int randomRange(BungeeRandom random, int start) {
        return randomRange(random, 0, 0x7fffffff);
    }

    public static int randomRange(BungeeRandom random, int start, int end) {
        return start + (random.nextInt() % (end + 1 - start));
    }

    public static double round(double d) {
        return round(d, 0);
    }

    public static double round(double d, int precision) {
        return ((double) Math.round(d * java.lang.Math.pow(10, precision))) / java.lang.Math.pow(10, precision);
    }

    public static double clamp(double check, double min, double max) {
        return check > max ? max : (check < min ? min : check);
    }

    public static double getDirection(double d0, double d1) {
        if (d0 < 0.0D) {
            d0 = -d0;
        }

        if (d1 < 0.0D) {
            d1 = -d1;
        }

        return d0 > d1 ? d0 : d1;
    }

}
