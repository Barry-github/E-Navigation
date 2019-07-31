package cn.ehanghai.routecalc.nav.action;

import cn.ehanghai.routecalc.common.math.Epoint;
import cn.ehanghai.routecalc.common.utils.Tuple;
import cn.ehanghai.routecalc.nav.domain.BaseLine;
import cn.ehanghai.routecalc.nav.domain.LinePoint;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ZhouShanSouthCase {

    private  double lat=29.416666666666668;

    private  double NoData=-9999;

    //yulj: 向下最短航线包含的转向点和航线段
    private  List<LinePoint> downPoints;
    private  List<BaseLine> downLines;
    //yulj: 向上最短航线包含的转向点和航线段
    private  List<LinePoint> upPoints;
    private  List<BaseLine> upLines;
//    private  List<Epoint>   boundaryLines;

    private  int sindex;
    private  int eindex;


    private  boolean up;        //yulj:航线是否向上

    public  boolean init(){
        sindex=0;
        eindex=0;
//        if(downLines!=null&&upLines!=null&&boundaryLines!=null) return true;
        if(downLines!=null&&upLines!=null) return true;
        return false;
    }

    public void getPos(LinePoint start, LinePoint end)
    {

        if(start.getLat().doubleValue()<end.getLat().doubleValue()){
//    upLines
            up=true;
            sindex=0;
            eindex=upPoints.size();
            for(int i=0;i<upPoints.size();++i)
            {
                LinePoint point=upPoints.get(i);
                if(start.getLat().doubleValue()<point.getLat().doubleValue())
                {
                    sindex=i;
                    break;
                }
            }

            for(int i=upPoints.size()-1;i>=0;--i)
            {
                LinePoint point=upPoints.get(i);
                if(end.getLat().doubleValue()>point.getLat().doubleValue())
                {
                    eindex=i;
                    break;
                }
            }

        }
        else
        {
            up=false;
            sindex=0;
            eindex=downPoints.size();

            for(int i=0;i<downPoints.size();++i)
            {
                LinePoint point=downPoints.get(i);
                if(start.getLat().doubleValue()>point.getLat().doubleValue())
                {
                    sindex=i;
                    System.out.println("start.getLat() = " + start.getLat());
                    System.out.println("point.getLat() = " + point.getLat());
                    break;
                }
            }

            for(int i=downPoints.size()-1;i>=0;--i)
            {
                LinePoint point=downPoints.get(i);
                if(end.getLat().doubleValue()<point.getLat().doubleValue())
                {
                    eindex=i;
                    break;
                }
            }

        }
    }


    public  boolean checkLatRange(LinePoint start, LinePoint end) {

        if (start.getLat().doubleValue() > lat || end.getLat().doubleValue() > lat) return false;
        return true;
    }

    //yulj:判断航线方向，true：大体水平，false：大体竖直
    public  boolean checkLineDirect(LinePoint start, LinePoint end)
    {

        Epoint startp=new Epoint(start.getLon(),start.getLat());
        startp= Epoint.LonLatToXY(startp);

        Epoint endp=new Epoint(end.getLon(),end.getLat());
        endp= Epoint.LonLatToXY(endp);

        Epoint vec= Epoint.Vec(startp,endp);

        double  angle = Math.atan2(vec.getY(), vec.getX());
        angle = angle / Math.PI * 180.0;
        if (angle < 0) angle += 360;
        angle =    checkAngle( angle);

        if(Math.abs(angle-180)<45||Math.abs(angle-360)<45||Math.abs(angle)<45)
        {
            return false;
        }


        return  true;
    }

    private   double checkAngle(double angle)
    {
        if (angle <= 90.0)
        {
            angle = 90.0 - angle;
        }
        else
        {
            angle = 360.0 - angle + 90.0;
        }
        return angle;
    }

    public  List<String> getStartCodes(){
        List<String> codes=new ArrayList<>();
        if(up)
        {
            codes= getStartCodes( upPoints);
        }
        else {
            codes=getStartCodes(downPoints);
        }

        return codes;
    }


    private    List<String> getStartCodes(List<LinePoint> points){
        List<String> codes=new ArrayList<>();
        int i;
        for(i=sindex;i<points.size();++i)
        {
            if(codes.size()<11)
            {
                codes.add(points.get(i).getCode());
            }
        }

        sindex=i;

        return  codes;

    }

    public  List<String> getEndCodes(){
        List<String> codes=new ArrayList<>();
        if(up)
        {
            codes= getEndCodes( upPoints);
        }
        else {
            codes=getEndCodes(downPoints);
        }

        return codes;
    }


    private    List<String> getEndCodes(List<LinePoint> points){
        List<String> codes=new ArrayList<>();
        for(int i=eindex;i>=0;--i)
        {
            if(codes.size()<11)
            {
                codes.add(points.get(i).getCode());
            }
        }
//        Collections.reverse(codes);
        return  codes;

    }

    public Tuple< List<String>, List<BaseLine>> getLines(String startCode, String endCode){

        List<String> lineCodes=new ArrayList<>();
        List<BaseLine> lines=new ArrayList<>();

        int startIndex;
        int endIndex;
        if(up)
        {
            List<String> codes=  upPoints.stream().map(a->a.getCode()).collect(Collectors.toList());
            startIndex=codes.indexOf(startCode);
            endIndex=codes.indexOf(endCode);

            for(int i=startIndex;i<=endIndex;++i)
            {
                lineCodes.add(upPoints.get(i).getCode());

                if(i<upLines.size()) {
                    lines.add(upLines.get(i));
                }
            }
        }
        else {
            List<String> codes=  downPoints.stream().map(a->a.getCode()).collect(Collectors.toList());
            startIndex=codes.indexOf(startCode);
            endIndex=codes.indexOf(endCode);

            for(int i=startIndex;i<=endIndex;++i)
            {
                lineCodes.add(downPoints.get(i).getCode());

                if(i<downLines.size()) {
                    lines.add(downLines.get(i));
                }
            }
        }

        return  new Tuple< List<String>, List<BaseLine>>(lineCodes,lines);


    }


    public int getSindex() {
        return sindex;
    }

    public void setSindex(int sindex) {
        this.sindex = sindex;
    }

    public int getEindex() {
        return eindex;
    }

    public void setEindex(int eindex) {
        this.eindex = eindex;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public List<LinePoint> getDownPoints() {
        return downPoints;
    }

    public void setDownPoints(List<LinePoint> downPoints) {
        this.downPoints = downPoints;
    }

    public List<BaseLine> getDownLines() {
        return downLines;
    }

    public void setDownLines(List<BaseLine> downLines) {
        this.downLines = downLines;
    }

    public List<LinePoint> getUpPoints() {
        return upPoints;
    }

    public void setUpPoints(List<LinePoint> upPoints) {
        this.upPoints = upPoints;
    }

    public List<BaseLine> getUpLines() {
        return upLines;
    }

    public void setUpLines(List<BaseLine> upLines) {
        this.upLines = upLines;
    }

//
//    public List<Epoint> getBoundaryLines() {
//        return boundaryLines;
//    }
//
//    public void setBoundaryLines(List<Epoint> boundaryLines) {
//        this.boundaryLines = boundaryLines;
//    }
}
