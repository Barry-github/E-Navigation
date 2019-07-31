package cn.ehanghai.route.nav.domain;


import cn.ehanghai.route.common.domain.BaseDomain;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name="nav_t_route_line_check")
public class RouteLineCheck extends BaseDomain {

    @Column(name="line_id")
    private  String lineId;
    private String path;
    @Column(name="line_check")
    private  boolean lineCheck;
    @Column(name="line_type")
    private  Integer lineType;


    private  String color;


    private  Integer direction;


    public RouteLineCheck() {
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


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
