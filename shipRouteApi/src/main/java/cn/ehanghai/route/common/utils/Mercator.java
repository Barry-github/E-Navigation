package cn.ehanghai.route.common.utils;

public class Mercator {

	private final double mercatorMinX = -20037508.342787;
	private final double mercatorMinY = -20037508.342787;
	private final double mercatorMaxX = 20037508.342787;
	private final double mercatorMaxY = 20037508.342787;

	public double ToX(double lon) {
		return lon * mercatorMaxX / 180.0;
	}

	public double ToY(double lat) {
		double y = Math.log(Math.tan((90.0 + lat) * Math.PI / 360.0)) / (Math.PI / 180.0);
		y = y * mercatorMaxY / 180.0;
		return y;
	}

	public double ToLon(double x) {
		return x / mercatorMaxX * 180.0;
	}

	public double ToLat(double y) {
		double lat = y / mercatorMaxY * 180.0;
		lat = 180.0 / Math.PI * (2.0 * Math.atan(Math.exp(lat * Math.PI / 180.0)) - Math.PI / 2.0);
		return lat;
	}
}
