package cn.ehanghai.route.nav.domain;


import cn.ehanghai.route.common.domain.BaseDomain;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;
import java.util.Objects;

/**
 * 航线
 * @author 胡恒博
 * @create 2018-03-19 14:13
 **/
@Table(name = "nav_t_line_test ")
public class NavLine extends BaseDomain {

    private String name;
    /**
     * 航线编码（暂定为各个转向点拼接组成）
     */
    @Column(name="line_code")
    private String lineCode;
    /**
     * 开始港口的ID
     */
    @Column(name="start_harbour_id")
    private Long startHarbourId;

    @Transient
    private String startHarbourName;
    @Transient
    private String endHarbourName;

    /**
     * 结束港口的ID
     */
    @Column(name="end_harbour_id")
    private Long endHarbourId;
    /**
     * 距离：单位米
     */
    private Double distance;
    /**
     * 航线类型 类型 暂定0：自动生成
     */
    private Integer type;
    private String remark;
    @Transient
    private List<NavLineInfo> navLineInfos;

    public String getStartHarbourName() {
        return startHarbourName;
    }

    public void setStartHarbourName(String startHarbourName) {
        this.startHarbourName = startHarbourName;
    }

    public String getEndHarbourName() {
        return endHarbourName;
    }

    public void setEndHarbourName(String endHarbourName) {
        this.endHarbourName = endHarbourName;
    }

    public List<NavLineInfo> getNavLineInfos() {
        return navLineInfos;
    }

    public void setNavLineInfos(List<NavLineInfo> navLineInfos) {
        this.navLineInfos = navLineInfos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public Long getStartHarbourId() {
        return startHarbourId;
    }

    public void setStartHarbourId(Long startHarbourId) {
        this.startHarbourId = startHarbourId;
    }

    public Long getEndHarbourId() {
        return endHarbourId;
    }

    public void setEndHarbourId(Long endHarbourId) {
        this.endHarbourId = endHarbourId;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NavLine navLine = (NavLine) o;
        return Objects.equals(name, navLine.name) &&
                Objects.equals(startHarbourId, navLine.startHarbourId) &&
                Objects.equals(endHarbourId, navLine.endHarbourId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, startHarbourId, endHarbourId);
    }
}
