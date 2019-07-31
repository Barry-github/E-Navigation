package cn.ehanghai.routecalc.nav.action;

import cn.ehanghai.routecalc.common.math.Epoint;
import cn.ehanghai.routecalc.common.math.Line;
import cn.ehanghai.routecalc.nav.domain.BaseLine;
import cn.ehanghai.routecalc.nav.domain.BaseLineAll;
import cn.ehanghai.routecalc.nav.domain.LinePoint;
import cn.ehanghai.routecalc.nav.domain.LinePointAll;
import cn.ehanghai.routecalc.nav.service.BaseLineAllService;
import cn.ehanghai.routecalc.nav.service.LinePointAllService;
import javafx.util.Pair;


import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class BaseLineIntersect {

    private LinePointAllService linePointAllService;

    private BaseLineAllService baseLineAllService;

    private  double NoData=-9999;

    public  void calcNewPoints(List<BaseLine> baseLines, List<LinePoint>  linePoints)
    {

        baseLines = baseLines.stream().filter(a -> a.getValid() != null && a.getValid() == 1).collect(Collectors.toList());
        linePoints = linePoints.stream().filter(a -> a.getValid() != null && a.getValid() == 1).collect(Collectors.toList());

        Long maxId=1L;
        String maxCode=  linePoints.stream().max(Comparator.comparing(a->a.getCode())).get().getCode();
        String []items=maxCode.split("-");
        if(items.length==2)
        {
            maxId=   Long.parseLong(items[1])+1;
        }


        List<IntersectPoint>  intersectpoints=new ArrayList<>();

        for (int i = 0; i <  baseLines.size(); ++i) {
            BaseLine start = baseLines.get(i);
            for (int j = i+1; j < baseLines.size(); ++j) {

                BaseLine end = baseLines.get(j);
                Epoint point= lineMeet( start, end,  linePoints);
                if(point!=null)
                {
                 boolean check=   pointCheck( point, linePoints);
                 if(check==false)  continue;

                    Optional<IntersectPoint> findp=  intersectpoints.stream().filter(a->Epoint.Distance(a.getEpoint(),point)<1e-5).findFirst();

                    if(findp.isPresent())
                    {
                        findp.get().getIndexs().add(new Pair<>(i,j));
                    }
                    else
                    {
                        IntersectPoint intersectPoint=new IntersectPoint();
                        intersectPoint.setEpoint(point);
                        intersectPoint.setIndexs(new ArrayList<>());
                        intersectPoint.getIndexs().add(new Pair<>(i,j));
                        intersectpoints.add(intersectPoint );

                    }

                }
            }
        }


        List<LinePointAll> newPoints=linePoints.stream().map(a->a.toLinePointAll()).collect(Collectors.toList());
        List<BaseLineAll> newLines=new ArrayList<>();

        HashMap<String,Boolean> linekeys=new HashMap<>();

        List<Integer> removeLines=new ArrayList<>();

        for(IntersectPoint point:intersectpoints)
        {


            LinePoint linePoint=   toLinePoint(point.getEpoint(),maxId);
            maxId++;
            newPoints.add(linePoint.toLinePointAll());

            for(Pair<Integer,Integer> pair:point.getIndexs())
            {
                int  index=pair.getKey();
                List<BaseLineAll>  lines=   insertLinePoint(linePoint,baseLines.get(index),linekeys);
                newLines.addAll(lines);
                removeLines.add(index);



                index=pair.getValue();
                 lines=   insertLinePoint(linePoint,baseLines.get(index),linekeys);
                newLines.addAll(lines);
                removeLines.add(index);
            }

        }

        removeLines=removeLines.stream().distinct().collect(Collectors.toList());

        for (int i = 0; i <  baseLines.size(); ++i)
        {
            boolean noexist=true;
            for (int j =0; j < removeLines.size(); ++j)
            {
                if(i==removeLines.get(j))
                {
                    noexist=false;
                            break;
                }
            }
            if(noexist)
            {
                BaseLineAll baseLineAll=baseLines.get(i).toBaseLineAll();
                String key=codeToKey(baseLineAll);
                if(!linekeys.containsKey(key))
                {
                    newLines.add(baseLineAll);
                    linekeys.put(key,true);

                }
            }
        }

        linePointAllService.insertList(newPoints);
        baseLineAllService.insertList(newLines);

    }

  private   String codeToKey(BaseLineAll line)
    {
        String code1=line.getStartCode();
        String code2=line.getEndCode();


        Long id1=  Long.parseLong(code1.replace("E-",""));
        Long id2=  Long.parseLong(code2.replace("E-",""));
        if(id1<id2)
        {
            return code1+code2;
        }

        return  code2+code1;
    }



  private   Boolean pointCheck(Epoint epoint,List<LinePoint> linePoints)
    {
        for(LinePoint linePoint:linePoints)
        {
            Epoint checkp=new Epoint(linePoint.getLon(),linePoint.getLat());
            checkp= Epoint.LonLatToXY(checkp);
            if(Epoint.Distance(checkp,epoint)<1e-5)
            {
                return  false;
            }
        }

      return  true;
    }

 private    LinePoint toLinePoint(Epoint epoint,long maxId)
    {
        epoint=Epoint.XYToLonLat(epoint);

        DecimalFormat df  = new DecimalFormat("0000000");
        String code= "E-" + df.format(maxId);

        LinePoint linePoint=new LinePoint();
        linePoint.setName("");
        linePoint.setIsolated(false);
        linePoint.setRemark("");
        linePoint.setLon((float)epoint.getX());
        linePoint.setLat((float)epoint.getY());
        linePoint.setNeedBroadcast(0);
        linePoint.setHarbour(false);
        linePoint.setImportState(0);
        linePoint.setCode(code);
        linePoint.setHarbour(false);
        linePoint.setNeedBroadcast(0);
        linePoint.calcGeoHash();
        linePoint.setValid(1);

        return linePoint;
    }

private     List<BaseLineAll> insertLinePoint(LinePoint linePoint,BaseLine line,  HashMap<String,Boolean> linekeys)
    {
        List <BaseLineAll> baseLines=new ArrayList<>();

        BaseLine baseLine=new BaseLine();
        baseLine.setStartCode(line.getStartCode());
        baseLine.setEndCode(linePoint.getCode());
        baseLine.setType(line.getType());
        baseLine.setDistance((long)NoData);
        baseLine.setDraught(NoData);
        baseLine.setHigh(line.getHigh());
        baseLine.setTonnage(line.getTonnage());
        baseLine.setLane(line.getLane());
        baseLine.setOneWayStreet(line.getOneWayStreet());
        baseLine.setImportState(line.getImportState());
        baseLine.setName(line.getName());
        baseLine.setWaterwayWidth(line.getWaterwayWidth());
        baseLine.setValid(1);

        BaseLineAll baseLineAll=baseLine.toBaseLineAll();
        String key=codeToKey(baseLineAll);
        if(!linekeys.containsKey(key))
        {
            linekeys.put(key,true);
            baseLines.add(baseLineAll);
        }






        baseLine=new BaseLine();
        baseLine.setStartCode(linePoint.getCode());
        baseLine.setEndCode(line.getEndCode());
        baseLine.setType(line.getType());
        baseLine.setDistance((long)NoData);
        baseLine.setDraught(NoData);
        baseLine.setHigh(line.getHigh());
        baseLine.setTonnage(line.getTonnage());
        baseLine.setLane(line.getLane());
        baseLine.setOneWayStreet(line.getOneWayStreet());
        baseLine.setImportState(line.getImportState());
        baseLine.setName(line.getName());
        baseLine.setWaterwayWidth(line.getWaterwayWidth());
        baseLine.setValid(1);

         baseLineAll=baseLine.toBaseLineAll();
         key=codeToKey(baseLineAll);
        if(!linekeys.containsKey(key))
        {
            linekeys.put(key,true);
            baseLines.add(baseLineAll);
        }

        return baseLines;
    }

//    public  void calcNewPoints(List<BaseLine> baseLines, List<LinePoint>  linePoints)
//    {
//
//        baseLines = baseLines.stream().filter(a -> a.getValid() != null && a.getValid() == 1).collect(Collectors.toList());
//        linePoints = linePoints.stream().filter(a -> a.getValid() != null && a.getValid() == 1).collect(Collectors.toList());
//
//        Long maxId=1L;
//        String maxCode=  linePoints.stream().max(Comparator.comparing(a->a.getCode())).get().getCode();
//        String []items=maxCode.split("-");
//        if(items.length==2)
//        {
//            maxId=   Long.parseLong(items[1])+1;
//        }
//
//        List<LinePointAll> newPoints=linePoints.stream().map(a->a.toLinePointAll()).collect(Collectors.toList());
//        List<BaseLineAll> newLines=new ArrayList<>();
//        List<Integer> lineIndexs=new ArrayList<>();
//
//        for (int i = 0; i <  baseLines.size(); ++i) {
//            BaseLine start = baseLines.get(i);
//            List<Pair<Epoint,List<Integer>>> meetpairs=new ArrayList<>();
//            for (int j = i+1; j < baseLines.size(); ++j) {
//
//                BaseLine end = baseLines.get(j);
//                Epoint point= lineMeet( start, end,  linePoints);
//                if(point!=null)
//                {
//
//                    Optional<Pair<Epoint,List<Integer>>>  pairop=   meetpairs.stream().filter(a->Epoint.Distance(point,a.getKey())<=1e-5).findFirst();
//
//                    if(pairop.isPresent())
//                    {
//                        pairop.get().getValue().add(j);
//                    }
//                    else
//                    {
//                        List<Integer> list=new ArrayList<>();
//                        list.add(j);
//                        meetpairs.add(new Pair<>(point,list));
//                    }
//
//                    lineIndexs.add(j);
//                    lineIndexs.add(i);
//                }
//            }
//
//            if(meetpairs.size()<=0)
//            {
//                continue;
//            }
//
//            maxId=calcNewPoints(baseLines,i, meetpairs, maxId,newPoints,newLines);
//        }
//
//        lineIndexs=lineIndexs.stream().distinct().collect(Collectors.toList());
//        for (int i = 0; i < baseLines.size(); ++i)
//        {
//            boolean nosplit=true;
//            for(int j=0;j<lineIndexs.size();++j)
//            {
//                if(i==lineIndexs.get(j))
//                {
//                    nosplit=false;
//                    break;
//                }
//            }
//
//            if(nosplit)
//            {
//                newLines.add(baseLines.get(i).toBaseLineAll());
//            }
//        }
//        mergeNewPoints(newPoints, newLines);
//
//    }
//
//    private  void mergeNewPoints(List<LinePointAll> points,List<BaseLineAll> lines)
//    {
//        List<LinePointAll> newPoints=new ArrayList<>();
//        HashMap<String,List<String >> codemap=new HashMap<>();
//        for(LinePointAll point:points)
//        {
//            Optional<LinePointAll> findp=     newPoints.stream().filter(a->distance(a,point)<1e-5).findFirst();
//            if(findp.isPresent())
//            {
//                String code=    findp.get().getCode();
//                if(codemap.containsKey(point.getCode()))
//                {
//                    codemap.get(point.getCode()).add(code);
//                }
//                else
//                {
//                    List<String > tmpdatas=new ArrayList<>();
//                    tmpdatas.add(code);
//                    codemap.put(point.getCode(),tmpdatas);
//                }
//            }
//            else
//            {
//                newPoints.add(point);
//            }
//        }
//
//
//        List<BaseLineAll> removelines=new ArrayList<>();
//        for(String key:codemap.keySet())
//        {
//            List<String> codes=codemap.get(key);
//            for (String code:codes)
//            {
//
//                for(BaseLineAll line:lines) {
//                    if(line.getStartCode().equals(code))
//                    {
//                        line.setStartCode(key);
//                    }
//
//                    if(line.getEndCode().equals(code))
//                    {
//                        line.setEndCode(key);
//                    }
//
//                    if(line.getStartCode().equals(line.getEndCode()))
//                    {
//
//                        removelines.add(line);
//                    }
//                }
//
//            }
//
//        }
//
//        removelines=   removelines.stream().distinct().collect(Collectors.toList());
//
//        for(BaseLineAll line:removelines)
//        {
//            lines.remove(line);
//        }
//
//
//        points=newPoints;
//
//        linePointAllService.insertList(points);
//        baseLineAllService.insertList(lines);
//
//    }

    private  long  calcNewPoints(List<BaseLine> baseLines,int index, List<Pair<Epoint,List<Integer>>> meetpairs,long maxId,List<LinePointAll>newPoints,List<BaseLineAll>newLines)
    {

        for(Pair<Epoint,List<Integer>> pair : meetpairs )
        {
            Epoint epoint=   pair.getKey();
            epoint=Epoint.XYToLonLat(epoint);

            DecimalFormat df  = new DecimalFormat("0000000");
            String code= "E-" + df.format(maxId);

            LinePointAll linePoint=new LinePointAll();
            linePoint.setName("");
            linePoint.setIsolated(false);
            linePoint.setRemark("");
            linePoint.setLon((float)epoint.getX());
            linePoint.setLat((float)epoint.getY());
            linePoint.setNeedBroadcast(0);
            linePoint.setHarbour(false);
            linePoint.setImportState(0);
            linePoint.setCode(code);
            linePoint.setHarbour(false);
            linePoint.setNeedBroadcast(0);
            linePoint.calcGeoHash();
            linePoint.setValid(1);


            maxId++;
            newPoints.add(linePoint);

            BaseLine start =baseLines.get(index);
            List <BaseLineAll> baseLineAlls=  makeNewLines(  linePoint, start);
            newLines.addAll(baseLineAlls);

            for(int j=0;j<pair.getValue().size();++j)
            {
                BaseLine end = baseLines.get(j);
                baseLineAlls=  makeNewLines(  linePoint, end);
                newLines.addAll(baseLineAlls);

            }

        }

        return  maxId;
    }


    private    List <BaseLineAll>  makeNewLines( LinePointAll linePoint,BaseLine line)
    {
        List <BaseLineAll> baseLineAlls=new ArrayList<>();

        BaseLineAll baseLine=  line.toBaseLineAll();
        baseLine.setStartCode(line.getStartCode());
        baseLine.setEndCode(linePoint.getCode());
        baseLine.setDraught(NoData);
        baseLine.setValid(1);
        baseLineAlls.add(baseLine);


        baseLine= line.toBaseLineAll();
        baseLine.setStartCode(linePoint.getCode());
        baseLine.setEndCode(line.getEndCode());
        baseLine.setDraught(NoData);
        baseLine.setValid(1);
        baseLineAlls.add(baseLine);

        return baseLineAlls;
    }



    private  Epoint lineMeet(BaseLine linea,BaseLine lineb, List<LinePoint>  linePoints)
    {
        Optional<LinePoint> astartop= linePoints.stream().filter(a->a.getCode().equals(linea.getStartCode())).findFirst();
        Optional<LinePoint> aendop=linePoints.stream().filter(a->a.getCode().equals(linea.getEndCode())).findFirst();
        Optional<LinePoint> bstartop=linePoints.stream().filter(a->a.getCode().equals(lineb.getStartCode())).findFirst();
        Optional<LinePoint> bendop=linePoints.stream().filter(a->a.getCode().equals(lineb.getEndCode())).findFirst();
        if(astartop.isPresent()&&aendop.isPresent()&&bstartop.isPresent()&&bendop.isPresent())
        {
            Epoint astart= getMepoint( astartop);
            Epoint aend=getMepoint(aendop);
            Epoint bstart=getMepoint(bstartop);
            Epoint bend=getMepoint(bendop);
            Epoint meetpoint=new Epoint();
            boolean linemeet= Line.DoubleSegmentMeet(astart,aend,bstart,bend,meetpoint);
            if(linemeet)
            {
                return meetpoint;
            }
        }

        return null;

    }

    private  Epoint getMepoint( Optional<LinePoint> opoint)
    {
        LinePoint linePoint=opoint.get();
        Epoint epoint=new Epoint(linePoint.getLon(),linePoint.getLat());
        return  Epoint.LonLatToXY(epoint);
    }

    private  double distance(LinePointAll pa,LinePointAll pb)
    {
        Epoint epa=new Epoint(pa.getLon(),pa.getLat());
        Epoint epb=new Epoint(pb.getLon(),pb.getLat());
        epa=  Epoint.LonLatToXY(epa);
        epb=  Epoint.LonLatToXY(epb);

        return Epoint.Distance(epa,epb);



    }

    public LinePointAllService getLinePointAllService() {
        return linePointAllService;
    }

    public void setLinePointAllService(LinePointAllService linePointAllService) {
        this.linePointAllService = linePointAllService;
    }

    public BaseLineAllService getBaseLineAllService() {
        return baseLineAllService;
    }

    public void setBaseLineAllService(BaseLineAllService baseLineAllService) {
        this.baseLineAllService = baseLineAllService;
    }
}
