package cn.ehanghai.route.nav.domain;

public class ShipTrace {
    private String mmsi;
    private double lon;
    private double lat;
    private String time;
    private String geohash;
    private double courseOverGround;
    private double rateOfTurn;
    private double speedOverGround;

    private  int traceIndex;




    public ShipTrace() {

    }

    public ShipTrace(String mmsi, double lon, double lat, String time, String geohash, double courseOverGround, double rateOfTurn, double speedOverGround) {
        this.mmsi = mmsi;
        this.lon = lon;
        this.lat = lat;
        this.time = time;
        this.geohash = geohash;
        this.courseOverGround = courseOverGround;
        this.rateOfTurn = rateOfTurn;
        this.speedOverGround = speedOverGround;
    }

    public String getGeohash() {
        return geohash;
    }

    public void setGeohash(String geohash) {
        this.geohash = geohash;
    }

    public String getMmsi() {
        return mmsi;
    }

    public void setMmsi(String mmsi) {
        this.mmsi = mmsi;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getCourseOverGround() {
        return courseOverGround;
    }

    public void setCourseOverGround(double courseOverGround) {
        this.courseOverGround = courseOverGround;
    }

    public double getRateOfTurn() {
        return rateOfTurn;
    }

    public void setRateOfTurn(double rateOfTurn) {
        this.rateOfTurn = rateOfTurn;
    }

    public double getSpeedOverGround() {
        return speedOverGround;
    }

    public void setSpeedOverGround(double speedOverGround) {
        this.speedOverGround = speedOverGround;
    }


    public int getTraceIndex() {
        return traceIndex;
    }

    public void setTraceIndex(int traceIndex) {
        this.traceIndex = traceIndex;
    }
}
