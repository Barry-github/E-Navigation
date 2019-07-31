package cn.ehanghai.routecheck.nav.domain;


import ch.hsr.geohash.GeoHash;
import cn.ehanghai.routecheck.common.domain.BaseDomain;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;


@Table(name = "nav_t_line_point_all_test")
public class LinePointAll extends BaseDomain {

    private static final long serialVersionUID = 2035103969714892144L;
    private String code;
    private String name;
    @Column(name = "en_name")
    private String enName;
    private Float lon;
    private Float lat;
    @Column(name = "need_broadcast")
    private Integer needBroadcast;
    private String remark;
    /**
     * 导入状态 0 导入 1已经调整
     */
    private Integer importState;
    @Transient
    private boolean harbour;
    /**
     * 孤立的
     */
    @Transient
    private boolean isolated;

    @Column(name = "geohash")
    private String  geohash;


    private Integer valid;// 有效性


    public LinePointAll() {

    }

    public LinePointAll(String code, String name, String enName, Float lon, Float lat, Integer needBroadcast, String remark, Integer importState, boolean harbour, boolean isolated, String geohash, Integer valid) {
        this.code = code;
        this.name = name;
        this.enName = enName;
        this.lon = lon;
        this.lat = lat;
        this.needBroadcast = needBroadcast;
        this.remark = remark;
        this.importState = importState;
        this.harbour = harbour;
        this.isolated = isolated;
        this.geohash = geohash;
        this.valid = valid;
    }

    public  boolean checkRange()
    {
        if(lon>=-180&&lon<=180&&lat>=-90&&lat<=90)
        {
            return true;
        }
        return false;

    }

    public  void calcGeoHash()
    {
        GeoHash geoHash = GeoHash.withCharacterPrecision(lat, lon, 12);
        geohash= geoHash.toBinaryString();
    }


    public LinePoint toLinePoint()
    {
        return new  LinePoint( code,  name,  enName,  lon,  lat,  needBroadcast,  remark,  importState,  harbour,  isolated,  geohash,  valid);
    }


    public String getGeohash() {
        return geohash;
    }

    public void setGeohash(String geohash) {
        this.geohash = geohash;
    }

    public boolean isIsolated() {
        return isolated;
    }

    public void setIsolated(boolean isolated) {
        this.isolated = isolated;
    }

    public boolean getHarbour() {
        return harbour;
    }

    public void setHarbour(boolean harbour) {
        this.harbour = harbour;
    }

    /**
     * 转折点编码
     */
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

    /**
     * 英文名称
     */
    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public Float getLon() {
        return lon;
    }

    public void setLon(Float lon) {
        this.lon = lon;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Integer getImportState() {
        return importState;
    }

    public void setImportState(Integer importState) {
        this.importState = importState;
    }

    /**
     * 备注
     *
     * @return
     */
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + ((enName == null) ? 0 : enName.hashCode());
        result = prime * result + ((lat == null) ? 0 : lat.hashCode());
        result = prime * result + ((lon == null) ? 0 : lon.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((remark == null) ? 0 : remark.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        LinePointAll other = (LinePointAll) obj;
        if (code == null) {
            if (other.code != null) {
                return false;
            }
        } else if (!code.equals(other.code)) {
            return false;
        }
        if (enName == null) {
            if (other.enName != null) {
                return false;
            }
        } else if (!enName.equals(other.enName)) {
            return false;
        }
        if (lat == null) {
            if (other.lat != null) {
                return false;
            }
        } else if (!lat.equals(other.lat)) {
            return false;
        }
        if (lon == null) {
            if (other.lon != null) {
                return false;
            }
        } else if (!lon.equals(other.lon)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (remark == null) {
            return other.remark == null;
        } else {
            return remark.equals(other.remark);
        }
    }

    public Integer getNeedBroadcast() {
        return needBroadcast;
    }

    public void setNeedBroadcast(Integer needBroadcast) {
        this.needBroadcast = needBroadcast;
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
