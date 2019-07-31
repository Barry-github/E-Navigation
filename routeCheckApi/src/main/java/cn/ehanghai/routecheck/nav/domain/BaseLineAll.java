package cn.ehanghai.routecheck.nav.domain;



import cn.ehanghai.routecheck.common.domain.BaseDomain;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Table(name = "nav_t_base_line_all_test")
public class BaseLineAll extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @Column(name = "name")
    private String name;

    /**
     * 类型 1内航线，2外航线,3 中航线，4内河航线
     */
    private Integer type;

    /**
     * 开始转向点编号
     */
    @Column(name = "start_code")
    private String startCode;
    @Transient
    private LinePoint startLinePoint;

    @Transient
    private LinePoint endLinePoint;
    /**
     * 结束转向点编号
     */
    @Column(name = "end_code")
    private String endCode;

    @Column(name = "distance")
    private Long distance;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 导入状态 0 导入 1已经调整
     */
    @Column(name = "import_state")
    private Integer importState;

    /**
     * 最大吃水
     */
    private Double draught;

    /**
     * 最大高度
     */
    private Double high;

    /**
     * 最大吨位
     */
    private Integer tonnage;
    @Transient
    private List<BaseLine> childNode;

    /**
     * 是否是单行线
     */
    @Column(name = "one_way_street")
    private Integer oneWayStreet;

    /**
     * 航宽
     * */
    @Column(name = "waterway_width")
    private Double waterwayWidth;

    /**
     * 是否是定线制
     */
    @Column(name = "lane")
    private Boolean lane;


    private Integer valid;// 有效性


    public BaseLineAll() {

    }

    public BaseLineAll(String name, Integer type, String startCode, LinePoint startLinePoint, LinePoint endLinePoint, String endCode, Long distance, String remark, Integer importState, Double draught, Double high, Integer tonnage, List<BaseLine> childNode, Integer oneWayStreet, Double waterwayWidth, Boolean lane, Integer valid) {
        this.name = name;
        this.type = type;
        this.startCode = startCode;
        this.startLinePoint = startLinePoint;
        this.endLinePoint = endLinePoint;
        this.endCode = endCode;
        this.distance = distance;
        this.remark = remark;
        this.importState = importState;
        this.draught = draught;
        this.high = high;
        this.tonnage = tonnage;
        this.childNode = childNode;
        this.oneWayStreet = oneWayStreet;
        this.waterwayWidth = waterwayWidth;
        this.lane = lane;
        this.valid = valid;
    }

    public  BaseLine toBaseLine()
    {
        return new BaseLine( name,  type,  startCode,  startLinePoint,  endLinePoint,  endCode,  distance,  remark,  importState,  draught,  high,  tonnage,  childNode,  oneWayStreet,  waterwayWidth,  lane,  valid);
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getOneWayStreet() {
        return oneWayStreet;
    }

    public void setOneWayStreet(Integer oneWayStreet) {
        this.oneWayStreet = oneWayStreet;
    }

    public Double getWaterwayWidth() {
        return waterwayWidth;
    }

    public void setWaterwayWidth(Double waterwayWidth) {
        this.waterwayWidth = waterwayWidth;
    }

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

    public String getStartCode() {
        return startCode;
    }

    public void setStartCode(String startCode) {
        this.startCode = startCode;
    }

    public String getEndCode() {
        return endCode;
    }

    public void setEndCode(String endCode) {
        this.endCode = endCode;
    }

    public LinePoint getStartLinePoint() {
        return startLinePoint;
    }

    public void setStartLinePoint(LinePoint startLinePoint) {
        this.startLinePoint = startLinePoint;
    }

    public LinePoint getEndLinePoint() {
        return endLinePoint;
    }

    public void setEndLinePoint(LinePoint endLinePoint) {
        this.endLinePoint = endLinePoint;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<BaseLine> getChildNode() {
        return childNode;
    }

    public void setChildNode(List<BaseLine> childNode) {
        this.childNode = childNode;
    }

    public Double getDraught() {
        return draught;
    }

    public void setDraught(Double draught) {
        this.draught = draught;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Integer getTonnage() {
        return tonnage;
    }

    public void setTonnage(Integer tonnage) {
        this.tonnage = tonnage;
    }

    public Integer getImportState() {
        return importState;
    }

    public void setImportState(Integer importState) {
        this.importState = importState;
    }

    @Override
    public Integer getValid() {
        return valid;
    }

    @Override
    public void setValid(Integer valid) {
        this.valid = valid;
    }
}
