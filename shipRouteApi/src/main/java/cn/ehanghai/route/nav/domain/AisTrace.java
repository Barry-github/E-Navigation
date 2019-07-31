package cn.ehanghai.route.nav.domain;

import java.util.List;

public class AisTrace {
    private  String mmsi;
    private List<AisPath> paths;

    public String getMmsi() {
        return mmsi;
    }

    public void setMmsi(String mmsi) {
        this.mmsi = mmsi;
    }

    public List<AisPath> getPaths() {
        return paths;
    }

    public void setPaths(List<AisPath> paths) {
        this.paths = paths;
    }
}
