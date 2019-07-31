 package cn.ehanghai.routecalc.common.math;

//yulj:经纬度与墨卡托投影坐标之间的转换
 public class Mercator
 {
   private final double mercatorMinX = -20037508.342787001D;
   private final double mercatorMinY = -20037508.342787001D;
   private final double mercatorMaxX = 20037508.342787001D;
   private final double mercatorMaxY = 20037508.342787001D;

   public double ToX(double lon) {
     return lon * 20037508.342787001D / 180.0D;
   }

   public double ToY(double lat) {
     double y = Math.log(Math.tan((90.0D + lat) * 3.141592653589793D / 360.0D)) / 0.0174532925199433D;
     y = y * 20037508.342787001D / 180.0D;
     return y;
   }

   public double ToLon(double x) {
     return x / 20037508.342787001D * 180.0D;
   }

   public double ToLat(double y) {
     double lat = y / 20037508.342787001D * 180.0D;
     lat = 57.295779513082323D * (2.0D * Math.atan(Math.exp(lat * 3.141592653589793D / 180.0D)) - 1.570796326794897D);
     return lat;
   }
 }

