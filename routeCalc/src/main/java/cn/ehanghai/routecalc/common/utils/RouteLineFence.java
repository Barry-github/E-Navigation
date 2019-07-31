package cn.ehanghai.routecalc.common.utils;

import cn.ehanghai.routecalc.common.math.Epoint;
import cn.ehanghai.routecalc.common.math.Line;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RouteLineFence {

    private List<Epoint> allLinePoints;

    private  List<Epoint> outLine;

    private  List<Double> distances=new ArrayList<>();

    public double clacDistance(Epoint point){
     double distance=   Epoint.Distance(point,outLine.get(0));
     double tmpDis=Epoint.Distance(point,outLine.get(1));
     distance=Math.min(distance,tmpDis);

     Line line=new Line(outLine.get(0),outLine.get(1));

     Epoint vpoint=   line.Vertical(point);
      boolean isIn=  Epoint.PointInSegment(outLine.get(0),outLine.get(1),vpoint);
      if(isIn)
      {
          tmpDis=Epoint.Distance(point,vpoint);
          distance=Math.min(distance,tmpDis);
      }
     return  distance;
    }

    public  boolean isInPolygonNormal(Epoint point)
    {
        int nCross = 0;

        for (int i = 0; i < allLinePoints.size(); i++)
        {
            Epoint p1 = allLinePoints.get(i);
            Epoint p2 = allLinePoints.get((i + 1) % allLinePoints.size());

            if (Math.abs(p1.getY() - p2.getY()) < 1e-6) // p1p2 水平线
            {
                continue;
            }

            if (point.getY() < Math.min(p1.getY(), p2.getY())) // 交点在p1 p2延长线上
            {
                continue;
            }

            if (point.getY() >= Math.max(p1.getY(), p2.getY())) // 交点在p1p2延长线上
            {
                continue;
            }

            double x = (point.getY() - p1.getY()) * (p2.getX() - p1.getX()) / (p2.getY() - p1.getY()) + p1.getX();

            if (x > point.getX())
            {
                nCross++;
            }
        }

        return nCross % 2 == 1;
    }


    public  void add(double distance){
        distances.add(distance);

    }

    public String disToString(){
        Collections.sort(distances, (a,b)-> a>=b?1:-1);
//        return String.join(",",distances.stream().map(a->a+"").collect(Collectors.toList()));
        StringBuffer buffer=new StringBuffer();
        for(int i=1;i<distances.size();++i){
            double value=Math.round(distances.get(i)-distances.get(i-1));
            buffer.append(value+",");
        }

        return  buffer.toString();
    }

    public List<Epoint> getAllLinePoints() {
        return allLinePoints;
    }

    public void setAllLinePoints(List<Epoint> allLinePoints) {
        this.allLinePoints = allLinePoints;
    }

    public List<Epoint> getOutLine() {
        return outLine;
    }

    public void setOutLine(List<Epoint> outLine) {
        this.outLine = outLine;
    }
}
