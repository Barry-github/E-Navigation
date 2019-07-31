package cn.ehanghai.routecalc.nav.action;

import cn.ehanghai.routecalc.common.math.Epoint;
import cn.ehanghai.routecalc.nav.domain.BaseLine;
import javafx.util.Pair;

import java.util.List;

public class IntersectPoint {

    private Epoint  epoint;

    private List<Pair<Integer,Integer>> indexs;

    public IntersectPoint(Epoint epoint, List<Pair<Integer, Integer>> indexs) {
        this.epoint = epoint;
        this.indexs = indexs;
    }

    public IntersectPoint() {
    }

    public Epoint getEpoint() {
        return epoint;
    }

    public void setEpoint(Epoint epoint) {
        this.epoint = epoint;
    }

    public List<Pair<Integer, Integer>> getIndexs() {
        return indexs;
    }

    public void setIndexs(List<Pair<Integer, Integer>> indexs) {
        this.indexs = indexs;
    }
}
