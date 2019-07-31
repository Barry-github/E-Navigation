package cn.ehanghai.routecalc.nav.domain;

public class RouteCatalog {

    private  String startCode;

    private  String startName;

    private  String startLon;
    private  String startLat;

    private String endCode;
    private  String endName;
    private  String endLon;
    private  String endLat;

    private  String initials;

    private  int pathLen;

    public RouteCatalog() {
    }

    public RouteCatalog(String startCode, String startName, String startLon, String startLat, String endCode, String endName, String endLon, String endLat, String initials, int pathLen) {
        this.startCode = startCode;
        this.startName = startName;
        this.startLon = startLon;
        this.startLat = startLat;
        this.endCode = endCode;
        this.endName = endName;
        this.endLon = endLon;
        this.endLat = endLat;
        this.initials = initials;
        this.pathLen = pathLen;
    }

    @Override
    public String toString() {
        return  initials+","+ startCode+"," + startName +
                "," + startLon+"," + startLat+
                "," + endCode +"," + endName+
                "," + endLon+"," + endLat;
    }

    public String getStartCode() {
        return startCode;
    }

    public void setStartCode(String startCode) {
        this.startCode = startCode;
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    public String getStartLon() {
        return startLon;
    }

    public void setStartLon(String startLon) {
        this.startLon = startLon;
    }

    public String getStartLat() {
        return startLat;
    }

    public void setStartLat(String startLat) {
        this.startLat = startLat;
    }

    public String getEndCode() {
        return endCode;
    }

    public void setEndCode(String endCode) {
        this.endCode = endCode;
    }

    public String getEndName() {
        return endName;
    }

    public void setEndName(String endName) {
        this.endName = endName;
    }

    public String getEndLon() {
        return endLon;
    }

    public void setEndLon(String endLon) {
        this.endLon = endLon;
    }

    public String getEndLat() {
        return endLat;
    }

    public void setEndLat(String endLat) {
        this.endLat = endLat;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public int getPathLen() {
        return pathLen;
    }

    public void setPathLen(int pathLen) {
        this.pathLen = pathLen;
    }
}
