package cn.ehanghai.routecheck.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LonLatUtil {
	
	public static Map<String, Object> getLaLoMap(String str) {
		str=str.replace("(", "").replace(")", "");
		String[] laloLs=str.split(",");
		Float longitudeStart=null;
		Float longitudeEnd=null;
		Float latitudeStart=null;
		Float latitudeEnd=null;
		ArrayList<Double> latitudeList=new ArrayList<>();
		ArrayList<Double> longitudeList=new ArrayList<>();
		for(String lalo:laloLs){
			String lo=lalo.split(":")[0];
			String la=lalo.split(":")[1];
			longitudeStart=getFloat(longitudeStart,lo,"gt");
			longitudeEnd=getFloat(longitudeEnd,lo,"lt");
			latitudeStart=getFloat(latitudeStart,la,"gt");
			latitudeEnd=getFloat(latitudeEnd,la,"lt");
			latitudeList.add(Double.valueOf(la));
			longitudeList.add(Double.valueOf(lo));
		}
		Map<String, Object> map= new HashMap<>();
		map.put("longitudeStart", longitudeStart);
		map.put("longitudeEnd", longitudeEnd);
		map.put("latitudeStart", latitudeStart);
		map.put("latitudeEnd", latitudeEnd);
		map.put("longitudeList", longitudeList);
		map.put("latitudeList", latitudeList);
		return map;
	}


	public static Float getFloat(Float value, String str, String contrast) {
		if(value==null){
			value=Float.valueOf(str);
		}else{
			if(contrast.equals("gt")&& value>Float.valueOf(str)){
				value=Float.valueOf(str);
			}else if(contrast.equals("lt")&& value<Float.valueOf(str)){
				value=Float.valueOf(str);
			}
		}
		return value;
	}
	
	/**  
     *  是否有 横断<br/>  
     *  参数为四个点的坐标  
     * @param px1  
     * @param py1  
     * @param px2  
     * @param py2  
     * @param px3  
     * @param py3  
     * @param px4  
     * @param py4  
     * @return    
     */  
    public static boolean isIntersect ( double px1 , double py1 , double px2 , double py2 , double px3 , double py3 , double px4 ,    
            double py4 )    
    {    
        boolean flag = false;    
        double d = (px2 - px1) * (py4 - py3) - (py2 - py1) * (px4 - px3);    
        if ( d != 0 )    
        {    
            double r = ((py1 - py3) * (px4 - px3) - (px1 - px3) * (py4 - py3)) / d;    
            double s = ((py1 - py3) * (px2 - px1) - (px1 - px3) * (py2 - py1)) / d;    
            if ( (r >= 0) && (r <= 1) && (s >= 0) && (s <= 1) )    
            {    
                flag = true;    
            }    
        }    
        return flag;    
    }   
    /**  
     *  目标点是否在目标边上边上<br/>  
     *    
     * @param px0 目标点的经度坐标  
     * @param py0 目标点的纬度坐标  
     * @param px1 目标线的起点(终点)经度坐标  
     * @param py1 目标线的起点(终点)纬度坐标  
     * @param px2 目标线的终点(起点)经度坐标  
     * @param py2 目标线的终点(起点)纬度坐标  
     * @return  
     */  
    public static boolean isPointOnLine ( double px0 , double py0 , double px1 , double py1 , double px2 , double py2 )    
    {    
        boolean flag = false;    
        double ESP = 1e-9;//无限小的正数  
        if ( (Math.abs(Multiply(px0, py0, px1, py1, px2, py2)) < ESP) && ((px0 - px1) * (px0 - px2) <= 0)    
                && ((py0 - py1) * (py0 - py2) <= 0) )    
        {    
            flag = true;    
        }    
        return flag;    
    }   
  
    public static double Multiply ( double px0 , double py0 , double px1 , double py1 , double px2 , double py2 )    
    {    
        return ((px1 - px0) * (py2 - py0) - (px2 - px0) * (py1 - py0));    
    }  
  
    /**  
     * 判断目标点是否在多边形内(由多个点组成)<br/>  
     *   
     * @param px 目标点的经度坐标  
     * @param py 目标点的纬度坐标  
     * @param polygonXA 多边形的经度坐标集合  
     * @param polygonYA 多边形的纬度坐标集合  
     * @return  
     */  
    public static boolean isPointInPolygon ( double px , double py , ArrayList<Double> polygonXA , ArrayList<Double> polygonYA )    
    {   
        boolean isInside = false;    
        double ESP = 1e-9;    
        int count = 0;    
        double linePoint1x;    
        double linePoint1y;    
        double linePoint2x = 180;    
        double linePoint2y;    
  
        linePoint1x = px;    
        linePoint1y = py;    
        linePoint2y = py;    
  
        for (int i = 0; i < polygonXA.size() - 1; i++)    
        {    
            double cx1 = polygonXA.get(i);    
            double cy1 = polygonYA.get(i);    
            double cx2 = polygonXA.get(i + 1);    
            double cy2 = polygonYA.get(i + 1);   
            //如果目标点在任何一条线上  
            if ( isPointOnLine(px, py, cx1, cy1, cx2, cy2) )    
            {    
                return true;    
            }  
            //如果线段的长度无限小(趋于零)那么这两点实际是重合的，不足以构成一条线段  
            if ( Math.abs(cy2 - cy1) < ESP )    
            {    
                continue;    
            }    
            //第一个点是否在以目标点为基础衍生的平行纬度线  
            if ( isPointOnLine(cx1, cy1, linePoint1x, linePoint1y, linePoint2x, linePoint2y) )    
            {    
                //第二个点在第一个的下方,靠近赤道纬度为零(最小纬度)  
                if ( cy1 > cy2 )    
                    count++;    
            }  
            //第二个点是否在以目标点为基础衍生的平行纬度线  
            else if ( isPointOnLine(cx2, cy2, linePoint1x, linePoint1y, linePoint2x, linePoint2y) )    
            {    
                //第二个点在第一个的上方,靠近极点(南极或北极)纬度为90(最大纬度)  
                if ( cy2 > cy1 )    
                    count++;    
            }  
            //由两点组成的线段是否和以目标点为基础衍生的平行纬度线相交  
            else if ( isIntersect(cx1, cy1, cx2, cy2, linePoint1x, linePoint1y, linePoint2x, linePoint2y) )    
            {    
                count++;    
            }    
        }    
        if ( count % 2 == 1 )    
        {    
            isInside = true;    
        }    
        
        return isInside;    
    }    
    
    /**
     * 根据输入的有序坐标获取重心点
     * @author hhb
     * @param lonLats
     * @return
     */
    public static Double[] getBarycenter(List<Double[]> lonLats){
    	int point_num = lonLats.size();
    	double area = 0;
    	double Gx = 0.0, Gy = 0.0;// 重心的x、y  
    	for(int i = 1; i <= point_num; ++i){
    		double temp = (lonLats.get(i % point_num)[0] * lonLats.get(i- 1)[1] - lonLats.get(i % point_num)[1] * lonLats.get(i -1)[0])/2.0; 
    		area += temp;
    		Gx += temp * (lonLats.get(i % point_num)[0] + lonLats.get(i - 1)[0]);  
        	Gy += temp * (lonLats.get(i % point_num)[1] + lonLats.get(i - 1)[1]);  
        	
    	}
        Double[] d = {Double.valueOf(String.format("%.3f",Gx/area/3.0)),Double.valueOf(String.format("%.3f",Gy/area/3.0))};
        return d;
    	
    }
    
  //计算任意多边形的面积，顶点按照顺时针或者逆时针方向排列
    public static  double ComputePolygonArea(List<Double[]> lonLats)
    {
    	int point_num = lonLats.size();
        if(point_num < 3)return 0.0;
        double s = lonLats.get(0)[1] * (lonLats.get(point_num-1)[0] - lonLats.get(1)[0]);
        for(int i = 1; i < point_num; ++i)
            s += lonLats.get(i)[1] * (lonLats.get(i-1)[0] - lonLats.get((i+1)%point_num)[0]);
        return Math.abs(s/2.0);
    }
    
    
    /**
	 * 获取两个经纬度之间的距离  
	 * @param LonA 经度A
	 * @param LatA 纬度A
	 * @param LonB 经度B
	 * @param LatB 经度B
	 * @return 距离（千米）
	 */
	public static double getDistance(double LonA, double LatA, double LonB, double LatB)  
	{  
	    // 东西经，南北纬处理，只在国内可以不处理(假设都是北半球，南半球只有澳洲具有应用意义)  
	    double MLonA = LonA;  
	    double MLatA = LatA;  
	    double MLonB = LonB;  
	    double MLatB = LatB;  
	    // 地球半径（千米）  
	    double R = 6371.004;  
	    double C = Math.sin(rad(MLatA)) * Math.sin(rad(MLatB)) + Math.cos(rad(MLatA)) * Math.cos(rad(MLatB)) * Math.cos(rad(MLonA - MLonB));  
	    return (R * Math.acos(C));  
	}  
	  
	private static double rad(double d)  
	{  
	    return d * Math.PI / 180.0;  
	}  
	
	
	/**
	 * 度分秒转换成度数
	 * @param latOrLon	要转换的经纬度
	 * @author 胡恒博
	 * 2017 11-14 调整传入的经纬度为南纬和西经的时候返回负数
	 * @return
	 */
	public static double toDegree(String latlng){
		double du=Double.parseDouble(latlng.substring(0, latlng.indexOf("°")));  
		double fen=Double.parseDouble(latlng.substring(latlng.indexOf("°")+1, latlng.indexOf("′")));  
//		double miao=Double.parseDouble(latlng.substring(latlng.indexOf("′")+1, latlng.indexOf("″")));  
		double miao=0d;
		if(du<0||latlng.indexOf("S")>0 || latlng.indexOf("W")>0)
			return -(Math.abs(du)+(fen+(miao/60))/60);  
		return du+(fen+(miao/60))/60;  
	}
	/**
	 * 度分秒转换成度数
	 * @param latOrLon	要转换的经纬度
	 * @author 胡恒博
	 * 2017 11-14 调整传入的经纬度为南纬和西经的时候返回负数
	 * @return
	 */
	public static double toDegreem(String latlng) throws Exception{
		if(latlng.indexOf("°")>-1){
			double du=Double.parseDouble(latlng.substring(0, latlng.indexOf("°")));  
			double fen=0.0;
			if(latlng.indexOf("′")>-1){
				fen=Double.parseDouble(latlng.substring(latlng.indexOf("°")+1, latlng.indexOf("′")));
				if(latlng.indexOf("″")<0 && latlng.indexOf("′")<latlng.length()-1){
					String miao=latlng.substring(latlng.indexOf("′")+1);
					if(miao.indexOf(".")>-1){
						miao="0"+miao;
					}
					miao=miao.replaceAll(" ","");
					if(miao.length()>0){
						fen=fen+(Double.valueOf(miao));
					}
				}
		    } 
			double miao=0.0;
			if(latlng.indexOf("″")>-1){
				miao=Double.parseDouble(latlng.substring(latlng.indexOf("′")+1, latlng.indexOf("″")));
			 } 
			if(du<0||latlng.indexOf("S")>0 || latlng.indexOf("W")>0)
				return -(Math.abs(du)+(fen+(miao/60))/60);  
			return du+(fen+(miao/60))/60;  
		}
		return Double.valueOf(latlng);
	}
	 /**
     * 功能：  度-->度分秒
     * @param d   传入待转化格式的经度或者纬度
     * @param isLon   是否是经度
     * @return
     * @author 胡恒博
     * 2017 11-14改为转化为 120°30.22′
     */
	public static String DDtoDMS(Double d,boolean isLon){
		String[] array=d.toString().split("[.]");
        String degrees=array[0];//得到度
        Double m=Double.parseDouble("0."+array[1])*60;
//        String[] array1=m.toString().split("[.]");
//        String minutes=array1[0];//得到分
//        Double s=Double.parseDouble("0."+array1[1])*60;
//        String[] array2=s.toString().split("[.]");
//        String seconds=array2[0];//得到秒
//        return degrees+"°"+minutes+"′"+seconds+"″";
        String dms = degrees+"°"+String.format("%.2f", m)+"′";
        if (isLon) {
        	if (d>=0) {
        		dms+="N";
        	}else {
        		dms+="S";
        	}
		}else {
			if (d>=0) {
				dms+="E";
			}else {
				dms+="W";
			}
		}	
        return dms;
	}
}
