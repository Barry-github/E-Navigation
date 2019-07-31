package cn.ehanghai.routecheck.common.routelinecheck;

import cn.ehanghai.routecheck.nav.domain.BaseLine;
import cn.ehanghai.routecheck.nav.domain.LinePoint;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class RouteLineData {

    private BaseLine baseLine;

    private LinePoint startpoint;
    private   LinePoint endpoint;

    public RouteLineData() {
    }

    public RouteLineData(BaseLine baseLine, LinePoint startpoint, LinePoint endpoint) {
        this.baseLine = baseLine;
        this.startpoint = startpoint;
        this.endpoint = endpoint;
    }

    public BaseLine getBaseLine() {
        return baseLine;
    }

    public void setBaseLine(BaseLine baseLine) {
        this.baseLine = baseLine;
    }

    public LinePoint getStartpoint() {
        return startpoint;
    }

    public void setStartpoint(LinePoint startpoint) {
        this.startpoint = startpoint;
    }

    public LinePoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(LinePoint endpoint) {
        this.endpoint = endpoint;
    }

    public List<Epoint> GetLinePoints()
    {
        List<Epoint> epoints=new ArrayList<>();
        Epoint epoint=new Epoint(startpoint.getLon(),startpoint.getLat());
        epoints.add(epoint);

        epoint=new Epoint(endpoint.getLon(),endpoint.getLat());
        epoints.add(epoint);

        return epoints;
    }
}
