package cn.ehanghai.routecheck.poi.domain;

import cn.ehanghai.routecheck.common.domain.BaseDomain;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.persistence.Table;



@Table(name = "nav_focus_t_poi")
public class PoiInfo extends BaseDomain {

    private   String name;
    private     String path;
    private     String others;
    private    int type;
    public PoiInfo() {
    }

    public PoiInfo(String name, String path, String others, int type) {
        this.name = name;
        this.path = path;
        this.others = others;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public PoiAttr GetAttr()  {
        if(others!=null&&!others.isEmpty())
        {
            JSONObject obj =JSON.parseObject(others);  //将json字符串转换为json对象，jsonStr为一个json字符串
        return JSONObject.toJavaObject(obj,PoiAttr.class);
        }

        return null;
    }



}
