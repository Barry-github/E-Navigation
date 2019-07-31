 package cn.ehanghai.routecalc.common.math;

 public class Epoint
 {
   private double x;
   private double y;

   public double getX()
   {
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

   public Epoint() {
   }

   public Epoint(double x, double y) {
     this.x = x;
     this.y = y;
   }

   public static Epoint LonLatToXY(Epoint point) {
     Mercator mercator = new Mercator();
     double x = mercator.ToX(point.getX());
     double y = mercator.ToY(point.getY());
     return new Epoint(x, y);
   }

   public static Epoint XYToLonLat(Epoint point)
   {
     Mercator mercator = new Mercator();
     double lon = mercator.ToLon(point.getX());
     double lat = mercator.ToLat(point.getY());
     return new Epoint(lon, lat);
   }
   public static double MDistance(Epoint p1, Epoint p2)
   {
     p1=LonLatToXY(p1);
     p2=LonLatToXY(p2);

     return  Distance(p1,p2);

   }
   public static double Distance(Epoint p1, Epoint p2)
   {
     double deltax = p1.getX() - p2.getX();
     double deltay = p1.getY() - p2.getY();
     double distance = Math.sqrt(deltax * deltax + deltay * deltay);
     return distance;
   }

   public static boolean PointInSegment(Epoint p1, Epoint p2, Epoint po)
   {
     double oadis = Distance(po, p1);
     double obdis = Distance(po, p2);
     double abdis = Distance(p1, p2);
     return Math.abs(oadis + obdis - abdis) < 1.E-005D;
   }
   public static Epoint MVec(Epoint p1,Epoint p2)
   {
     Epoint pa=LonLatToXY(p1);
     Epoint pb=LonLatToXY(p2);
     double x=pb.getX()-pa.getX();
     double y=pb.getY()-pa.getY();
     return  new Epoint(x,y);
   }
   public static Epoint Vec(Epoint pa,Epoint pb)
   {
     double x=pb.getX()-pa.getX();
     double y=pb.getY()-pa.getY();
     return  new Epoint(x,y);
   }
   public static double VecLen(Epoint vec)
   {
     double distance = Math.sqrt(vec.getX() * vec.getX() + vec.getY() * vec.getY() );
     return distance;
   }

   public  static  double dot(Epoint va,Epoint vb)
   {

     return va.getX()*vb.getX()+va.getY()*vb.getY();
   }
 }

