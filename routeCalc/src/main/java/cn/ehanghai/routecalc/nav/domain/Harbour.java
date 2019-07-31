package cn.ehanghai.routecalc.nav.domain;


import cn.ehanghai.routecalc.common.domain.BaseDomain;
import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Column;
import javax.persistence.Table;


/**
 * @author 胡恒博
 */
@Table(name = "nav_t_harbour_test")
public class Harbour extends BaseDomain {

    private static final long serialVersionUID = 1L;

    //
    @JSONField(name = "text")
    private String name;

    //
    @Column(name = "code")
    private String code;
    
    //父类Id
    @Column(name = "pid")
    private Long pid;

    //
    @Column(name = "area")
    private  String area;

    @Column(name="overlay")
    private String overlay;

    @Column(name="name_lon")
    private Double nameLon;
    @Column(name="name_lat")
    private Double nameLat;
    
    @Column(name="line_point_code")
    private String linePointCode;

    @Column(name="harbour_type")
    private  Integer harbourType;

    private Integer valid;// 有效性

    public String getOverlay() {
        return overlay;
    }

    public void setOverlay(String overlay) {
        this.overlay = overlay;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setCode(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }

    public void setArea(String area){
        this.area = area;
    }

    public String getArea(){
        return area;
    }

    public Double getNameLon() {
        return nameLon;
    }

    public void setNameLon(Double nameLon) {
        this.nameLon = nameLon;
    }

    public Double getNameLat() {
        return nameLat;
    }

    public void setNameLat(Double nameLat) {
        this.nameLat = nameLat;
    }

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getLinePointCode() {
		return linePointCode;
	}

	public void setLinePointCode(String linePointCode) {
		this.linePointCode = linePointCode;
	}

    @Override
    public Integer getValid() {
        return valid;
    }

    @Override
    public void setValid(Integer valid) {
        this.valid = valid;
    }

    public Integer getHarbourType() {
        return harbourType;
    }

    public void setHarbourType(Integer harbourType) {
        this.harbourType = harbourType;
    }
}
