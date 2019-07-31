package cn.ehanghai.route.common.excelimport;

import cn.ehanghai.route.common.utils.LonLatUtil;

public class ImportRouteNode {


    private   String name;
    private   int nodeIndex;
    private   double angle;
    private   double len;
    private   String remark;
    private   double Lon;
    private   double Lat;


    public  static  double ParseAngle(String angle)
    {
        if(angle.isEmpty()) return -9999;

        try{


        double du=0;
        double fen=0;
        double miao=0;

        String []items=     angle.replace("°","#").split("#");
        if(items.length>0)
        {
            du=Double.parseDouble(items[0]);
        }

        if(items.length==2)
        {
            items=     items[1]
                    .replace("′","#")
                    .replace("‘","#")
                    .split("#");


        }
        else
        {
            double result=du+fen/60+miao/3600;
            return  result;
        }

        if(items.length>0)
        {
            String tmpstr=items[0].replace(".","");
            if(!tmpstr.isEmpty())
                fen=Double.parseDouble(tmpstr);
        }
        if(items.length==2)
        {
            items=     items[1].replace("″","#")
                    .replace("“","#")
                    .replace("\"","#").split("#");
        }
        else
        {
            double result=du+fen/60+miao/3600;
            return  result;
        }

        if(items.length>0)
        {
            String tmpstr=items[0].replace(".","");
            if(!tmpstr.isEmpty())
            miao=Double.parseDouble(tmpstr);
        }

        double result=du+fen/60+miao/3600;

        //System.out.println("du:"+du+" fen:"+fen+" miao:"+miao+" angle:"+angle);
        return  result;
        }
        catch (Exception ex)
        {
            return -9999;
        }
    }

    public ImportRouteNode() {
    }

    public ImportRouteNode(String name, int nodeIndex, double angle, double len, String remark, double lon, double lat) {
        this.name = name;
        this.nodeIndex = nodeIndex;
        this.angle = angle;
        this.len = len;
        this.remark = remark;
        Lon = lon;
        Lat = lat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNodeIndex() {
        return nodeIndex;
    }

    public void setNodeIndex(int nodeIndex) {
        this.nodeIndex = nodeIndex;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getLen() {
        return len;
    }

    public void setLen(double len) {
        this.len = len;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public double getLon() {
        return Lon;
    }

    public void setLon(double lon) {
        Lon = lon;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }
}
