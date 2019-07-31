package cn.ehanghai.route.nav.domain;

import cn.ehanghai.route.common.domain.BaseDomain;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @program: app-trunk
 * @description: 航线详细信息
 * @author: 胡恒博
 * @create: 2018-03-19 14:20
 **/
@Table(name = "nav_t_line_info_test")
public class NavLineInfo extends BaseDomain {
    /**
     * 航线ID
     */
    @Column(name = "line_id")
    private Long lineId;
    /**
     * 航线转向点排序
     */
    private Integer sort;
    /**
     * 转向点编号
     */
    @Column(name = "line_point_code")
    private String linePointCode;

    /**
     * 和上一个节点的距离
     */
    private Double distance;
    @Transient
    private Double lat;
    @Transient
    private Double lon;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Long getLineId() {
        return lineId;
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getLinePointCode() {
        return linePointCode;
    }

    public void setLinePointCode(String linePointCode) {
        this.linePointCode = linePointCode;
    }
}
