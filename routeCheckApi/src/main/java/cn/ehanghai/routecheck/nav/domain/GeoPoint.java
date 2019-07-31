package cn.ehanghai.routecheck.nav.domain;

public class GeoPoint {
    private  double x;
    private  double y;
    private  double z;

    private  double distance;

    public GeoPoint(double x, double y, double z,double dis) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.distance=dis;
    }

    public GeoPoint() {
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
