package cn.ehanghai.routecheck.poi.domain;

import java.io.Serializable;

public class PoiAttr implements Serializable {

   private String name;
    private   double length;
    private  double high;

    public PoiAttr() {
    }

    public PoiAttr(String name, double length, double high) {
        this.name = name;
        this.length = length;
        this.high = high;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }
}
