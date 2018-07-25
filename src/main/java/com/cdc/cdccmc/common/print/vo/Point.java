package com.cdc.cdccmc.common.print.vo;


public class Point {

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Point copy(){
        Point p = new Point(this.getX(), this.getY());
        return p;
    }

    private int x;
    private int y;
}
