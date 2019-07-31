package cn.ehanghai.routecheck.common.routelinecheck;

public class Epoint {
private double x;
private double y;
public double getX() {
	return x;
}
public void setX(double x) {
	this.x = x;
}
public double getY() {
	return y;
}
public void setY(double y) {
	this.y = y;
}
	public Epoint() {

	}
public Epoint(double x, double y) {
	super();
	this.x = x;
	this.y = y;
}

public static Epoint  LonLatToXY(Epoint point) {
	Mercator mercator=new Mercator();
	double x=mercator.ToX(point.getX());
	double y=mercator.ToY(point.getY());
	return new Epoint(x, y);
}


public static Epoint XYToLonLat(Epoint point) {
	Mercator mercator=new Mercator();
	double lon=mercator.ToLon(point.getX());
	double lat=mercator.ToLat(point.getY());
	return new Epoint(lon,lat);
}


public static double Distance(Epoint p1,Epoint p2){
	
	double deltax=p1.getX()-p2.getX();
	double deltay=p1.getY()-p2.getY();
	double distance=Math.sqrt(deltax*deltax+deltay*deltay);
	return distance;
	
}
public static  boolean PointInSegment(Epoint p1, Epoint p2, Epoint po)
{
	double oadis = Distance(po, p1);
	double obdis = Distance(po, p2);
	double abdis = Distance(p1, p2);
	return Math.abs(oadis + obdis - abdis) < 1e-5;
}

}
