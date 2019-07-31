 package cn.ehanghai.routecalc.nav.domain;

 public class GeoPoint
 {
   private double x;
   private double y;
   private double z;
   private double distance;

   public GeoPoint(double x, double y, double z, double dis)
   {
     this.x = x;
     this.y = y;
     this.z = z;
     this.distance = dis;
   }

   public GeoPoint() {
   }

   public double getDistance() {
     return this.distance;
   }

   public void setDistance(double distance) {
     this.distance = distance;
   }

   public double getX() {
     return this.x;
   }

   public void setX(double x) {
     this.x = x;
   }

   public double getY() {
     return this.y;
   }

   public void setY(double y) {
     this.y = y;
   }

   public double getZ() {
     return this.z;
   }

   public void setZ(double z) {
     this.z = z;
   }
 }

