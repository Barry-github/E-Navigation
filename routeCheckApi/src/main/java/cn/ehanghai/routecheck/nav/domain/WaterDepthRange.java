package cn.ehanghai.routecheck.nav.domain;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Table(name = "nav_t_water_depth_range")
public class WaterDepthRange {

    private  long id;
    @Column(name = "max_lon")
    private  double maxLon;
    @Column(name = "min_lon")
    private  double minLon;
    @Column(name = "max_lat")
    private  double maxLat;
    @Column(name = "min_lat")
    private  double minLat;

    private  String resolution;

    public   String toPath()
    {
        List<String> paths=new ArrayList<>();

        paths.add(String.format("%f,%f",minLon,minLat));
        paths.add(String.format("%f,%f",minLon,maxLat));
        paths.add(String.format("%f,%f",maxLon,maxLat));
        paths.add(String.format("%f,%f",maxLon,minLat));
        paths.add(String.format("%f,%f",minLon,minLat));

        return String.join(",",paths);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getMaxLon() {
        return maxLon;
    }

    public void setMaxLon(double maxLon) {
        this.maxLon = maxLon;
    }

    public double getMinLon() {
        return minLon;
    }

    public void setMinLon(double minLon) {
        this.minLon = minLon;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public void setMaxLat(double maxLat) {
        this.maxLat = maxLat;
    }

    public double getMinLat() {
        return minLat;
    }

    public void setMinLat(double minLat) {
        this.minLat = minLat;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
}
