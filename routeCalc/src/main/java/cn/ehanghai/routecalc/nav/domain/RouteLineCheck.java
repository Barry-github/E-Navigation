package cn.ehanghai.routecalc.nav.domain;

import cn.ehanghai.routecalc.common.domain.BaseDomain;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name="nav_t_route_line_check")
public class RouteLineCheck{

    @Column(name="line_id")
    private  String lineId;
    @Column(name="path")
    private String path;
    @Column(name="line_check")
    private  boolean lineCheck;
    @Column(name="line_type")
    private  Integer lineType;

    @Transient
    private  String color;
    @Transient
    private  Integer direction;

    public RouteLineCheck() {
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) { this.direction = direction; }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isLineCheck() {
        return lineCheck;
    }

    public void setLineCheck(boolean lineCheck) {
        this.lineCheck = lineCheck;
    }

    public Integer getLineType() {
        return lineType;
    }

    public void setLineType(Integer lineType) {
        this.lineType = lineType;
    }
}
