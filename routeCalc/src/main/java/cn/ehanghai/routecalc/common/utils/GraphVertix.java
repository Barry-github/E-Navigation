package cn.ehanghai.routecalc.common.utils;

public class GraphVertix {
    private   String code;
    private   Double distance;
    private   Double depth;

    private  Double ton;
    private  Double height;

    private  Boolean lane;

    private  String name;

    private  Double innerFactor;

    private  Double lon;
    private  Double lat;

    public GraphVertix() {
    }

    public GraphVertix(String code, Double distance, Double depth, Double ton, Double height, Boolean lane, String name) {
        this.code = code;
        this.distance = distance;
        this.depth = depth;
        this.ton = ton;
        this.height = height;
        this.lane = lane;
        this.name = name;
    }



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getDepth() {
        return depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }

    public Double getTon() {  return ton; }

    public void setTon(Double ton) {    this.ton = ton;  }

    public Double getHeight() {     return height;   }

    public void setHeight(Double height) {    this.height = height;   }


    public Boolean getLane() {
        return lane;
    }

    public void setLane(Boolean lane) {
        this.lane = lane;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Double getInnerFactor() {
        return innerFactor;
    }

    public void setInnerFactor(Double innerFactor) {
        this.innerFactor = innerFactor;
    }


    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }
}
