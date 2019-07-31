package cn.ehanghai.routecalc.nav.domain;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = "nav_t_route_area")
public class RouteArea {

    private  Integer id;
    @Column(name = "code")
    private  String code;
    @Column(name = "name")
    private  String name;
    @Column(name = "lon_min")
    private  Float lonMin;
    @Column(name = "lon_max")
    private  Float lonMax;
    @Column(name = "lat_min")
    private  Float latMin;
    @Column(name = "lat_max")
    private  Float latMax;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getLonMin() {
        return lonMin;
    }

    public void setLonMin(Float lonMin) {
        this.lonMin = lonMin;
    }

    public Float getLonMax() {
        return lonMax;
    }

    public void setLonMax(Float lonMax) {
        this.lonMax = lonMax;
    }

    public Float getLatMin() {
        return latMin;
    }

    public void setLatMin(Float latMin) {
        this.latMin = latMin;
    }

    public Float getLatMax() {
        return latMax;
    }

    public void setLatMax(Float latMax) {
        this.latMax = latMax;
    }
}
