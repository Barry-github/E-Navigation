package cn.ehanghai.route.nav.domain;

import java.util.List;

public class BackupData {
    private   List<Harbour> harbours ;
    private  List<LinePoint> linePoints ;
    private  List<BaseLine> baseLines ;


    public BackupData(List<Harbour> harbours, List<LinePoint> linePoints, List<BaseLine> baseLines) {
        this.harbours = harbours;
        this.linePoints = linePoints;
        this.baseLines = baseLines;

    }

    public BackupData() {
    }

    public List<Harbour> getHarbours() {
        return harbours;
    }

    public void setHarbours(List<Harbour> harbours) {
        this.harbours = harbours;
    }

    public List<LinePoint> getLinePoints() {
        return linePoints;
    }

    public void setLinePoints(List<LinePoint> linePoints) {
        this.linePoints = linePoints;
    }

    public List<BaseLine> getBaseLines() {
        return baseLines;
    }

    public void setBaseLines(List<BaseLine> baseLines) {
        this.baseLines = baseLines;
    }


}
