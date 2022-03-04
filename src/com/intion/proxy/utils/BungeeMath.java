package com.intion.proxy.utils;

public class BungeeMath {
    public BungeeMath() {
    }

    public static int floorDouble(double n) {
        int i = (int)n;
        return n >= (double)i ? i : i - 1;
    }

    public static int ceilDouble(double n) {
        int i = (int)(n + 1.0D);
        return n >= (double)i ? i : i - 1;
    }

    public static int floorFloat(float n) {
        int i = (int)n;
        return n >= (float)i ? i : i - 1;
    }

    public static int ceilFloat(float n) {
        int i = (int)(n + 1.0F);
        return n >= (float)i ? i : i - 1;
    }

    public static int randomRange(BungeeRandom random) {
        return randomRange(random, 0);
    }

    public static int randomRange(BungeeRandom random, int start) {
        return randomRange(random, 0, 2147483647);
    }

    public static int randomRange(BungeeRandom random, int start, int end) {
        return start + random.nextInt() % (end + 1 - start);
    }

    public static double round(double d) {
        return round(d, 0);
    }

    public static double round(double d, int precision) {
        return (double) java.lang.Math.round(d * java.lang.Math.pow(10.0D, (double)precision)) / java.lang.Math.pow(10.0D, (double)precision);
    }

    public static double clamp(double value, double min, double max) {
        return value < min ? min : (value > max ? max : value);
    }

    public static int clamp(int value, int min, int max) {
        return value < min ? min : (value > max ? max : value);
    }

    public static float clamp(float value, float min, float max) {
        return value < min ? min : (value > max ? max : value);
    }

    public static double getDirection(double diffX, double diffZ) {
        diffX = java.lang.Math.abs(diffX);
        diffZ = java.lang.Math.abs(diffZ);
        return java.lang.Math.max(diffX, diffZ);
    }
}

