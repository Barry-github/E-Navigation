 package cn.ehanghai.routecalc.nav.domain;

 import cn.ehanghai.routecalc.common.domain.BaseDomain;
 import javax.persistence.Table;

 @Table(name="nav_t_water_depth")
 public class WaterDepth extends BaseDomain
 {
   private double lon;
   private double lat;
   private double depth;
   private String geohash;
   private String resolution;

   public WaterDepth()
   {
   }

   public WaterDepth(double lon, double lat, double depth, String geohash, String resolution)
   {
     this.lon = lon;
     this.lat = lat;
     this.depth = depth;
     this.geohash = geohash;
     this.resolution = resolution;
   }

   public double getLon() {
     return this.lon;
   }

   public void setLon(double lon) {
     this.lon = lon;
   }

   public double getLat() {
     return this.lat;
   }

   public void setLat(double lat) {
     this.lat = lat;
   }

   public double getDepth() {
     return this.depth;
   }

   public void setDepth(double depth) {
     this.depth = depth;
   }

   public String getGeohash() {
     return this.geohash;
   }

   public void setGeohash(String geohash) {
     this.geohash = geohash;
   }

   public String getResolution() {
     return this.resolution;
   }

   public void setResolution(String resolution) {
     this.resolution = resolution;
   }
 }

