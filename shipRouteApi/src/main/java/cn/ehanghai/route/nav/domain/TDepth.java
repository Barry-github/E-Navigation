package cn.ehanghai.route.nav.domain;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = "TDepth")
public class TDepth {
    @Column(name = "lon")
    private  double lon;
    @Column(name = "lat")
    private  double lat;
    @Column(name = "depth")
    private  double depth;
    private  String geohash;
    private  String resolution;

    public TDepth() {
    }

    public TDepth(double lon, double lat, double depth, String geohash, String resolution) {
        this.lon = lon;
        this.lat = lat;
        this.depth = depth;
        this.geohash = geohash;
        this.resolution = resolution;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public String getGeohash() {
        return geohash;
    }

    public void setGeohash(String geohash) {
        this.geohash = geohash;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

}
