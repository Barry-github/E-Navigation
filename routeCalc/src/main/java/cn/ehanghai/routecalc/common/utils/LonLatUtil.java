 package cn.ehanghai.routecalc.common.utils;

 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;


 public class LonLatUtil
 {
   public static Map<String, Object> getLaLoMap(String str)
   {
     str = str.replace("(", "").replace(")", "");
     String[] laloLs = str.split(",");
     Float longitudeStart = null;
     Float longitudeEnd = null;
     Float latitudeStart = null;
     Float latitudeEnd = null;
     ArrayList latitudeList = new ArrayList();
     ArrayList longitudeList = new ArrayList();
     for (String lalo : laloLs) {
       String lo = lalo.split(":")[0];
       String la = lalo.split(":")[1];
       longitudeStart = getFloat(longitudeStart, lo, "gt");
       longitudeEnd = getFloat(longitudeEnd, lo, "lt");
       latitudeStart = getFloat(latitudeStart, la, "gt");
       latitudeEnd = getFloat(latitudeEnd, la, "lt");
       latitudeList.add(Double.valueOf(la));
       longitudeList.add(Double.valueOf(lo));
     }
     Object map = new HashMap();
     ((Map)map).put("longitudeStart", longitudeStart);
     ((Map)map).put("longitudeEnd", longitudeEnd);
     ((Map)map).put("latitudeStart", latitudeStart);
     ((Map)map).put("latitudeEnd", latitudeEnd);
     ((Map)map).put("longitudeList", longitudeList);
     ((Map)map).put("latitudeList", latitudeList);
     return (Map<String, Object>)map;
   }

   public static Float getFloat(Float value, String str, String contrast)
   {
     if (value == null) {
       value = Float.valueOf(str);
     }
     else if ((contrast.equals("gt")) && (value.floatValue() > Float.valueOf(str).floatValue()))
       value = Float.valueOf(str);
     else if ((contrast.equals("lt")) && (value.floatValue() < Float.valueOf(str).floatValue())) {
       value = Float.valueOf(str);
     }

     return value;
   }

   public static boolean isIntersect(double px1, double py1, double px2, double py2, double px3, double py3, double px4, double py4)
   {
     boolean flag = false;
     double d = (px2 - px1) * (py4 - py3) - (py2 - py1) * (px4 - px3);
     if (d != 0.0D)
     {
       double r = ((py1 - py3) * (px4 - px3) - (px1 - px3) * (py4 - py3)) / d;
       double s = ((py1 - py3) * (px2 - px1) - (px1 - px3) * (py2 - py1)) / d;
       if ((r >= 0.0D) && (r <= 1.0D) && (s >= 0.0D) && (s <= 1.0D))
       {
         flag = true;
       }
     }
     return flag;
   }

   public static boolean isPointOnLine(double px0, double py0, double px1, double py1, double px2, double py2)
   {
     boolean flag = false;
     double ESP = 1.E-009D;
     if ((Math.abs(Multiply(px0, py0, px1, py1, px2, py2)) < ESP) && ((px0 - px1) * (px0 - px2) <= 0.0D) && ((py0 - py1) * (py0 - py2) <= 0.0D))
     {
       flag = true;
     }
     return flag;
   }

   public static double Multiply(double px0, double py0, double px1, double py1, double px2, double py2)
   {
     return (px1 - px0) * (py2 - py0) - (px2 - px0) * (py1 - py0);
   }

   public static boolean isPointInPolygon(double px, double py, ArrayList<Double> polygonXA, ArrayList<Double> polygonYA)
   {
     boolean isInside = false;
     double ESP = 1.E-009D;
     int count = 0;

     double linePoint2x = 180.0D;

     double linePoint1x = px;
     double linePoint1y = py;
     double linePoint2y = py;

     for (int i = 0; i < polygonXA.size() - 1; i++)
     {
       double cx1 = ((Double)polygonXA.get(i)).doubleValue();
       double cy1 = ((Double)polygonYA.get(i)).doubleValue();
       double cx2 = ((Double)polygonXA.get(i + 1)).doubleValue();
       double cy2 = ((Double)polygonYA.get(i + 1)).doubleValue();

       if (isPointOnLine(px, py, cx1, cy1, cx2, cy2))
       {
         return true;
       }

       if (Math.abs(cy2 - cy1) < ESP)
       {
         continue;
       }

       if (isPointOnLine(cx1, cy1, linePoint1x, linePoint1y, linePoint2x, linePoint2y))
       {
         if (cy1 > cy2) {
           count++;
         }
       }
       else if (isPointOnLine(cx2, cy2, linePoint1x, linePoint1y, linePoint2x, linePoint2y))
       {
         if (cy2 > cy1)
           count++;
       }
       else {
         if (!isIntersect(cx1, cy1, cx2, cy2, linePoint1x, linePoint1y, linePoint2x, linePoint2y))
           continue;
         count++;
       }
     }
     if (count % 2 == 1)
     {
       isInside = true;
     }

     return isInside;
   }

   public static Double[] getBarycenter(List<Double[]> lonLats)
   {
     int point_num = lonLats.size();
     double area = 0.0D;
     double Gx = 0.0D; double Gy = 0.0D;
     for (int i = 1; i <= point_num; i++) {
       double temp = (((Double[])lonLats.get(i % point_num))[0].doubleValue() * ((Double[])lonLats.get(i - 1))[1].doubleValue() - ((Double[])lonLats.get(i % point_num))[1].doubleValue() * ((Double[])lonLats.get(i - 1))[0].doubleValue()) / 2.0D;
       area += temp;
       Gx += temp * (((Double[])lonLats.get(i % point_num))[0].doubleValue() + ((Double[])lonLats.get(i - 1))[0].doubleValue());
       Gy += temp * (((Double[])lonLats.get(i % point_num))[1].doubleValue() + ((Double[])lonLats.get(i - 1))[1].doubleValue());
     }

     Double[] d = { Double.valueOf(String.format("%.3f", new Object[] { Double.valueOf(Gx / area / 3.0D) })), Double.valueOf(String.format("%.3f", new Object[] { Double.valueOf(Gy / area / 3.0D) })) };
     return d;
   }

   public static double ComputePolygonArea(List<Double[]> lonLats)
   {
     int point_num = lonLats.size();
     if (point_num < 3) return 0.0D;
     double s = ((Double[])lonLats.get(0))[1].doubleValue() * (((Double[])lonLats.get(point_num - 1))[0].doubleValue() - ((Double[])lonLats.get(1))[0].doubleValue());
     for (int i = 1; i < point_num; i++)
       s += ((Double[])lonLats.get(i))[1].doubleValue() * (((Double[])lonLats.get(i - 1))[0].doubleValue() - ((Double[])lonLats.get((i + 1) % point_num))[0].doubleValue());
     return Math.abs(s / 2.0D);
   }

   public static double getDistance(double LonA, double LatA, double LonB, double LatB)
   {
     double MLonA = LonA;
     double MLatA = LatA;
     double MLonB = LonB;
     double MLatB = LatB;

     double R = 6371.0039999999999D;
     double C = Math.sin(rad(MLatA)) * Math.sin(rad(MLatB)) + Math.cos(rad(MLatA)) * Math.cos(rad(MLatB)) * Math.cos(rad(MLonA - MLonB));
     return R * Math.acos(C);
   }

   private static double rad(double d)
   {
     return d * 3.141592653589793D / 180.0D;
   }

   public static double toDegree(String latlng)
   {
     double du = Double.parseDouble(latlng.substring(0, latlng.indexOf("°")));
     double fen = Double.parseDouble(latlng.substring(latlng.indexOf("°") + 1, latlng.indexOf("′")));

     double miao = 0.0D;
     if ((du < 0.0D) || (latlng.indexOf("S") > 0) || (latlng.indexOf("W") > 0))
       return -(Math.abs(du) + (fen + miao / 60.0D) / 60.0D);
     return du + (fen + miao / 60.0D) / 60.0D;
   }

   public static double toDegreem(String latlng)
     throws Exception
   {
     if (latlng.indexOf("°") > -1) {
       double du = Double.parseDouble(latlng.substring(0, latlng.indexOf("°")));
       double fen = 0.0D;
       if (latlng.indexOf("′") > -1) {
         fen = Double.parseDouble(latlng.substring(latlng.indexOf("°") + 1, latlng.indexOf("′")));
         if ((latlng.indexOf("″") < 0) && (latlng.indexOf("′") < latlng.length() - 1)) {
           String miao = latlng.substring(latlng.indexOf("′") + 1);
           if (miao.indexOf(".") > -1) {
             miao = "0" + miao;
           }
           miao = miao.replaceAll(" ", "");
           if (miao.length() > 0) {
             fen += Double.valueOf(miao).doubleValue();
           }
         }
       }
       double miao = 0.0D;
       if (latlng.indexOf("″") > -1) {
         miao = Double.parseDouble(latlng.substring(latlng.indexOf("′") + 1, latlng.indexOf("″")));
       }
       if ((du < 0.0D) || (latlng.indexOf("S") > 0) || (latlng.indexOf("W") > 0))
         return -(Math.abs(du) + (fen + miao / 60.0D) / 60.0D);
       return du + (fen + miao / 60.0D) / 60.0D;
     }
     return Double.valueOf(latlng).doubleValue();
   }

   public static String DDtoDMS(Double d, boolean isLon)
   {
     String[] array = d.toString().split("[.]");
     String degrees = array[0];
     Double m = Double.valueOf(Double.parseDouble("0." + array[1]) * 60.0D);

     String dms = degrees + "°" + String.format("%.2f", new Object[] { m }) + "′";
     if (isLon) {
       if (d.doubleValue() >= 0.0D)
         dms = dms + "N";
       else {
         dms = dms + "S";
       }
     }
     else if (d.doubleValue() >= 0.0D)
       dms = dms + "E";
     else {
       dms = dms + "W";
     }

     return dms;
   }
 }
