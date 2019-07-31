package cn.ehanghai.routecheck.common.routelinecheck;

import cn.ehanghai.routecheck.poi.domain.PoiAttr;
import cn.ehanghai.routecheck.poi.domain.PoiInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.ehanghai.routecheck.common.routelinecheck.PoiType.*;


public class PoiData {

    private    List<Epoint> linepoints;
    private    PoiType poiType;
    private PoiAttr poiAttr;
    private  int shapeType;
    private   String poiName;

    public PoiData() {
        this.linepoints =new ArrayList<>();
    }

    public String getPoiName() {
        return poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    public int getShapeType() {
        return shapeType;
    }

    public void setShapeType(int shapeType) {
        this.shapeType = shapeType;
    }

    public PoiAttr getPoiAttr() {
        return poiAttr;
    }

    public void setPoiAttr(PoiAttr poiAttr) {
        this.poiAttr = poiAttr;
    }

    public List<Epoint> getLinepoints() {
        return linepoints;
    }

    public void setLinepoints(List<Epoint> linepoints) {
        this.linepoints = linepoints;
    }

    public PoiType getPoiType() {
        return poiType;
    }

    public void setPoiType(PoiType poiType) {
        this.poiType = poiType;
    }

    private  static Map<PoiType,String> GetPoiTypeMap()
    {
        Map<PoiType,String> map=new HashMap<>();
        map.put(noArea,"空");
        map.put(noSailZone,"禁航区");
        map.put(militaryRestrictedZone,"军事禁区");
        map.put(missileGunFireZone,"导弹火炮射击区");
        map.put(anchorage,"锚地");
        map.put(suspectedMinefield,"疑存雷区");
        map.put(sunkenShip,"沉船");
        map.put(submergedReef,"暗礁");
        map.put(rockawash,"适淹礁");
        map.put(bridge,"大桥");
        map.put(aerialCable,"架空电缆");
        return map;
    }

    private  static Map<PoiType,Integer> GetPoiShapeMap()
    {
        Map<PoiType,Integer> map=new HashMap<>();
        map.put(noArea,-1);
        map.put(noSailZone,2);
        map.put(militaryRestrictedZone,2);
        map.put(missileGunFireZone,2);
        map.put(anchorage,2);
        map.put(suspectedMinefield,2);
        map.put(sunkenShip,0);
        map.put(submergedReef,0);
        map.put(rockawash,0);
        map.put(bridge,1);
        map.put(aerialCable,1);
        return map;
    }



    private static PoiType GetPoitype(String name)
    {

        Map<PoiType,String> map=   GetPoiTypeMap();

        for (PoiType type:
                map.keySet()) {

            if( map.get(type).equals(name))
            {
                return type;
            }

        }
        return noArea;
    }


    public static  PoiData ToPoiData(PoiInfo poiInfo,String name)
    {
        PoiType poiType= GetPoitype( name);
        Map<PoiType,Integer>  shapemap= GetPoiShapeMap();
        int shapetype=shapemap.get(poiType);
        if(shapetype!=poiInfo.getType()) return  null;

        List<Epoint> points=   GetPathPoints(poiInfo);
        PoiAttr poiAttr= poiInfo.GetAttr();
        PoiData poiData=new PoiData();
        poiData.setPoiType(poiType);
        poiData.setLinepoints(points);
        poiData.setPoiAttr(poiAttr);
        poiData.setShapeType(poiInfo.getType());
        poiData.setPoiName(poiInfo.getName());

        return poiData;

    }


    private static List<Epoint> GetPathPoints(PoiInfo poiInfo)
    {
        List<Epoint> points=new ArrayList<>();
        String []items=    poiInfo.getPath().split("\\|");
        for (String item :          items) {
            String[] pointstrs = item.split(",");
            if(pointstrs.length==2)
            {
                double x=Double.parseDouble(pointstrs[0]);
                double y=Double.parseDouble(pointstrs[1]);

                Epoint point=new Epoint(x,y);
                point= Epoint.LonLatToXY(point);
                points.add(point);
            }

        }

        return points;
    }
}
