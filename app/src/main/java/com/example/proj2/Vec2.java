package com.example.proj2;

public class Vec2 {
    public double x;
    public double y;
    public Vec2(){
        this.x = 0.;
        this.y = 0.;
    }
    public Vec2(double x, double y){
        this.x = x;
        this.y = y;
    }
    public double distanceTo(Vec2 v){
        return Math.sqrt((x-v.x)*(x-v.x) + (y-v.y)*(y-v.y));
    }
}
