package cn.ehanghai.routecalc.nav.action;

import cn.ehanghai.routecalc.common.math.Epoint;
import cn.ehanghai.routecalc.common.math.Line;
import cn.ehanghai.routecalc.common.utils.*;
import cn.ehanghai.routecalc.nav.domain.BaseLine;
import cn.ehanghai.routecalc.nav.domain.LinePoint;
import cn.ehanghai.routecalc.nav.domain.RouteLine;
import net.ryian.commons.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class RouteLineCalcFast {

    private  double NoData=-9999;

    private List<LinePoint> allpoints;
    private   List<BaseLine> alllines;
    private   List<Node>  allNodes;

    private  int lineType;

    private boolean randomColor;

    private GraphFastTest3 graphFast;

    private  ZhouShanSouthCase zssCase;

    private  boolean extend;

    public RouteLineCalcFast(List<LinePoint> allpoints, List<BaseLine> alllines,int lineType,boolean randomColor) {
        this.allpoints = allpoints;
        this.alllines = alllines;
        this.lineType=lineType;
        this.randomColor=randomColor;
        this.extend=false;
        zssCase=new ZhouShanSouthCase();
        init();
    }

    private void  init()
    {
        allNodes = new ArrayList();
        for (LinePoint point : allpoints)
        {
            boolean find=  findPoint( point,alllines);

            if(find)
                allNodes.add(linePointToNode(point));
        }

        for (Node node : allNodes)
        {
            findRelationNodes(node, alllines,allNodes);
        }

        graphFast=new GraphFastTest3();
        graphFast.InitGraph(allNodes);
    }

    public List<RouteLine> getRouteLines(String startCode,String endCode,double shipHeight, double shipDepth, double shipTon,String laneNames)
    {
        List<RouteLine> routeLines=new ArrayList<>();

        //yulj:如果在转向点中没有要搜索的航线的起始点和终止点，应该跳出返回空吧
        Optional<LinePoint> startop = allpoints.stream().filter(a -> a.getCode().equals(startCode)).findFirst();
        LinePoint startpoint = null;
        if (startop.isPresent()) {
            startpoint = startop.get();
        }

        Optional<LinePoint> endop = allpoints.stream().filter(a -> a.getCode().equals(endCode)).findFirst();
        LinePoint endpoint = null;
        if (endop.isPresent()) {
            endpoint = endop.get();
        }

        if (startpoint == null || endpoint == null)
            return routeLines;

        List<BaseLine> startlines = alllines.stream().filter(a -> a.getStartCode().equals(startCode)||a.getEndCode().equals(startCode)).collect(Collectors.toList());
        List<BaseLine> endlines = alllines.stream().filter(a -> a.getStartCode().equals(endCode)||a.getEndCode().equals(endCode)).collect(Collectors.toList());

        if (startlines.size() == 0) {
            return routeLines;
        }

        //起始终止点的最大吃水中小的一个值，从0到该值以2米间隔形成序列，去掉比要求的吃水小的值
        //码头和转向点的表不直接保存吃水，吃水在航线段表中存放，所以要取出与起始终止点相连的航线段
        List<Double> depths = new ArrayList<>();
        double maxPortDepth=findDepths(startlines, endlines, depths);

        if(shipDepth!=NoData)
        {
            depths=depths.stream().filter(a->a.doubleValue()>shipDepth).collect(Collectors.toList());
            depths.add(shipDepth);
        }

        depths=depths.stream().distinct().collect(Collectors.toList());

        Collections.sort(depths,(a,b)->a>=b?1:-1);

        double maxDepth=NoData;
        RouteLine maxDepthLine=null;
        List<String> maxDepthPath=new ArrayList<>();

        HashMap<String,Boolean> pathMap=new HashMap<>();

        for(int k=0;k<depths.size();++k)
        {
            double depth=depths.get(k);
            List<String> path = graphFast.Search(startCode, endCode, shipHeight,  depth,  shipTon,laneNames,NoData);

            if (path.size() > 1)
            {
                RouteLine routeLine= getRouteLine(  path);

                if(routeLine==null) continue;
                boolean check= checkLineSegment(routeLine);
                if(check==false) continue;

                double finalDepth = routeLine.getDepth();
                Optional<RouteLine> findRouteLine= routeLines.stream().filter(a->a.getDepth()== finalDepth).findFirst();
                if(!findRouteLine.isPresent())
                {
                    routeLines.add(routeLine);
                    if(finalDepth>maxDepth){
                        maxDepth=depth;
                        maxDepthPath=path;
                        maxDepthLine=routeLine;

                        String text=String.join(",",path);
                        pathMap.put(text,true);
                    }

                    if(maxPortDepth!=NoData)
                    {
                        if(finalDepth<=maxPortDepth&&(maxPortDepth-finalDepth)<0.5) break;
                    }
                }
            }
        }

        int lineCount=1;

        boolean portLenCheck= checkPortLen( startpoint, endpoint);

        double portMaxDepth=depths.stream().mapToDouble(a->a).max().getAsDouble();

        if(maxDepth!=NoData&&portMaxDepth>=10&&lineType==0&&extend&&portLenCheck)
        {
            double minton=50000;

            List<RouteLine> newLines=new ArrayList<>();

            List<LinePoint> linePoints=new ArrayList<>();
            for (int i = 0; i < maxDepthPath.size(); ++i) {
                String code = maxDepthPath.get(i);
                LinePoint point=  allpoints.stream().filter(a->a.getCode().equals(code)).findFirst().get();
                linePoints.add(point);
            }

            LinePoint start=linePoints.get(0);
            LinePoint end=linePoints.get(linePoints.size()-1);

            double maxLon=linePoints.stream().mapToDouble(a->a.getLon()).max().getAsDouble();

            List<List<String>>  paths=pathExtends( maxDepthPath, start, end,  maxLon, maxDepth, shipHeight,   shipTon, laneNames);
            for(int i=0;i<paths.size();++i)
            {
                List<String> path = paths.get(i);
                if (path.size() > 1)
                {
                    String text=String.join(",",path);

                    if(pathMap.containsKey(text)) continue;

                    pathMap.put(text,true);
                    RouteLine routeLine= getRouteLine(path);
                    if(routeLine==null) continue;

                    //检查是否有线段相交
                    boolean check= checkLineSegment(routeLine);
                    if(check==false) continue;

                    routeLine.setFactor(lineCount++);
                    newLines.add(routeLine);
                }
            }

            if(newLines.size()>0)
            {
                routeLines.addAll(newLines);
            }
        }

        return routeLines;
    }



    private RouteLine getRouteLine( List<String> path) {
        double minheight = Double.MAX_VALUE;
        double minton = Double.MAX_VALUE;
        double mindepth = Double.MAX_VALUE;
        double depth = Double.MAX_VALUE;
        List<Node> nodes = new ArrayList();

        for (int i = 0; i < path.size(); ++i) {
            String code = path.get(i);
            Node node = findNode(allNodes, code);
            nodes.add(node);

            if (i < path.size() - 1) {
                String start = code;
                String end = path.get(i + 1);
                Optional<BaseLine> line = alllines.stream().filter(a -> (a.getStartCode().equals(start) && a.getEndCode().equals(end)) || (a.getEndCode().equals(start) && a.getStartCode().equals(end))).findFirst();
                if (line.isPresent()) {
                    if (line.get().getHigh() < minheight) {
                        minheight = line.get().getHigh();
                    }
                    if (line.get().getTonnage() < minton) {
                        minton = line.get().getTonnage();
                    }

                    if (line.get().getDraught() < mindepth) {
                        mindepth = line.get().getDraught();
//                        System.out.println("start:" + line.get().getStartCode() + ",end:" + line.get().getEndCode() + ",depth:" + line.get().getDraught());
                    }
                }
                else
                {
                    return  null;
                }
            }


        }

        if (minheight == Double.MAX_VALUE) minheight = NoData;
        if (minton == Double.MAX_VALUE) minton = NoData;
        if (mindepth == Double.MAX_VALUE) mindepth = NoData;


        if (mindepth != NoData) depth = mindepth;

        RouteLine routeLine = new RouteLine(minheight, depth, minton, lineType, randomColor, nodes);

        return routeLine;
    }


    private  List<List<String>> maxDepthFactorExtends(double maxDepth,double minFactor,String startCode,String endCode,double shipHeight,  double shipTon,String laneNames)
    {
        List<List<String>> paths=new ArrayList<>();
//        for(int i=1;i<=50;i+=1)
        {
//            double factor=minFactor-i*1852.0;

//            double factor=minFactor-50*1852.0;

            double    factor=0;

//            System.out.println("maxDepth:"+maxDepth+",factor:"+factor);
            List<String> path = graphFast.Search(startCode, endCode, shipHeight,  maxDepth,  shipTon,laneNames,factor);
            paths.add(path);
        }

        return paths;
    }

    private  List<List<String>> maxDepthLineExtends(List<String> path,double minFactor,double shipHeight,  double shipTon,String laneNames){

        double minDepth=Double.MAX_VALUE;

        List<Tuple<Integer,Double>>nodeDepths=new ArrayList<>();
        for (int i = 0; i < path.size(); ++i) {
            String code = path.get(i);
            if (i < path.size() - 1) {
                String start = code;
                String end = path.get(i + 1);
                Optional<BaseLine> line = alllines.stream().filter(a -> (a.getStartCode().equals(start) && a.getEndCode().equals(end)) || (a.getEndCode().equals(start) && a.getStartCode().equals(end))).findFirst();
                if (line.isPresent()) {
                    nodeDepths.add(new Tuple<>(i,line.get().getDraught()));
                    if(line.get().getDraught()<minDepth) {
                        minDepth=line.get().getDraught();
                    }
                }
            }
        }

        List<Tuple<Integer,Integer>> codes=new ArrayList<>();
        List<Double> depths=new ArrayList<>();

        double depth=nodeDepths.get(0).second;
        String endCode="";
        String startCode="";
        for (int i = 1; i < nodeDepths.size()/2; ++i)
        {
//            if(codes.size()>=5) break;

            if(nodeDepths.get(i).second>=depth)
            {
                int indexi=nodeDepths.get(i).first;

                depth=nodeDepths.get(i).second;
                for (int j = nodeDepths.size()-1; j >= nodeDepths.size()/2; --j)
                {
                    if(nodeDepths.get(j).second>=depth)
                    {
                        int indexj=nodeDepths.get(j).first;
                        codes.add(new Tuple<>(indexi,indexj));
                        depths.add(depth);
                        break;
                    }
                }
            }
        }

        depth=nodeDepths.get(nodeDepths.size()-1).second;
        for (int j = nodeDepths.size()-2; j >= nodeDepths.size()/2; --j)
        {
//            if(codes.size()>=5) break;

            if(nodeDepths.get(j).second>=depth)
            {
                int indexj=nodeDepths.get(j).first;

                depth=nodeDepths.get(j).second;
                for (int i = 1; i < nodeDepths.size()/2; ++i)
                {
                    if(nodeDepths.get(i).second>=depth)
                    {
                        int indexi=nodeDepths.get(i).first;
                        codes.add(new Tuple<>(indexi,indexj));
                        depths.add(depth);
                        break;
                    }
                }
            }
        }


        List<List<String>> paths=new ArrayList<>();

        for (int i = 0; i < codes.size(); ++i)
        {
            int indexi=codes.get(i).first;
            int indexj=codes.get(i).second;
            startCode=path.get(indexi);
            endCode=path.get(indexj);

            depth=depths.get(i);

            List<String> tmppath = graphFast.Search(startCode, endCode, shipHeight,  depth,  shipTon,laneNames,NoData);
            if(tmppath.size()<=1) continue;

            List<String>startSub=path.subList(0,indexi-1);
            List<String>endSub=path.subList(indexj+1,path.size());
            List<String>newpath=new ArrayList<>();
            newpath.addAll(startSub);
            newpath.addAll(tmppath);
            newpath.addAll(endSub);

            paths.add(newpath);


        }




        return paths;



    }

    private List<List<String>> pathExtends(List<String> path,LinePoint start,LinePoint end, double maxLon,double maxDepth,double shipHeight,  double shipTon,String laneNames)
    {
        List<List<String>> result=new ArrayList<>();

        ChengShanAndHangZhouCase chengShanAndHangZhouCase=new ChengShanAndHangZhouCase();
        if(chengShanAndHangZhouCase.checkInner(start,end))
        {
            return result;
        }

        boolean iscase= chengShanAndHangZhouCase.check(start,end);
        if(iscase)
        {
            result= chengShanHangZhouCase( chengShanAndHangZhouCase, path, start, end, maxDepth, shipHeight,   shipTon, laneNames);

        }
        else
        {
            if(zssCase.init()==false) initZssCase();

            boolean rangeCheck=zssCase.checkLatRange(start,end);

            if(rangeCheck)
            {
                //
                result=    zhouShanSouthCase(  start, end, maxDepth, shipHeight,   shipTon, laneNames);

            }else
            {
                result= depthAndDistanceExtends( start, end,  maxLon, maxDepth, shipHeight,   shipTon, laneNames);
            }
        }

        return  result;
    }

    private List<List<String>> depthAndDistanceExtends(LinePoint start,LinePoint end, double maxLon,double maxDepth,double shipHeight,  double shipTon,String laneNames)
    {

//        maxDepth=NoData;


//        List<LinePoint> linePoints=new ArrayList<>();
//        for (int i = 0; i < path.size(); ++i) {
//            String code = path.get(i);
//            LinePoint point=  allpoints.stream().filter(a->a.getCode().equals(code)).findFirst().get();
//            linePoints.add(point);
//        }
//
//        LinePoint start=linePoints.get(0);
//        LinePoint end=linePoints.get(linePoints.size()-1);
//
//        double maxLon=linePoints.stream().mapToDouble(a->a.getLon()).max().getAsDouble();


//        List<BaseLine> outLines=  alllines.stream().filter(a->a.getDraught()!=NoData&&a.getDraught()>=40).collect(Collectors.toList());
        List<String> codes=new ArrayList<>();
//        for(BaseLine line:outLines)
//        {
//            codes.add(line.getStartCode());
//            codes.add(line.getEndCode());
//        }
//
//        codes=codes.stream().distinct().collect(Collectors.toList());

        codes.add("E-0006132");
//        codes.add("E-0002497");
        codes.add("E-0007796");

        codes.add("E-0004689");
        codes.add("E-0006134");
        codes.add("E-0006165");
        codes.add("E-0006036");
        codes.add("E-0007743");

        List<Tuple<String,Double>> points=new ArrayList<>();

        for (String code:codes)
        {
            if(code.equals("E-0004759")) continue;
            if(code.equals("E-0003296")) continue;

//          if(points.get(i).first.equals("E-0003296")) continue;

//          System.out.println("code = " + code);
            Optional<LinePoint> pointOp=  allpoints.stream().filter(a->a.getCode().equals(code)).findFirst();
//          if(!pointOp.isPresent()) continue;
            if(pointOp.isPresent())
            {
                LinePoint point=pointOp.get();

                if(point.getLon()<maxLon) continue;
//              Optional<String> findCode= path.stream().filter(a->a.equals(point.getCode())).findFirst();
//              if(findCode.isPresent()) continue;

                if((point.getLat()-start.getLat())*(point.getLat()-end.getLat())>=0) continue;

                Epoint epoint=new Epoint(point.getLon(),point.getLat());
                boolean check= chekRange(epoint);
                if(check==false) continue;


                double distance= pointDistance( start , point)/1852.0;
                points.add(new Tuple<>(code,distance));
            }
        }

        Collections.sort(points,(a,b)->{
            if(a.second>b.second) return 1;
            if(a.second<b.second) return -1;

            return 0;
        });

        List<List<String>> paths=new ArrayList<>();
        boolean check;

        for(int i=0;i<20;++i)
        {
            if(i<points.size())
            {
                //E-0003296

//              if(points.get(i).first.equals("E-0000637"))
//              {
//                  System.out.println("i = " + i);
//              }


                List<String> path1 = graphFast.Search(start.getCode(), points.get(i).first, shipHeight,  maxDepth,  shipTon,laneNames,NoData);

//                check=  checkLineSecond(  path1,start.getLat(),end.getLat(),true);
//                if(check==false) continue;

                check= checkLineSecondAngle(  path1, true);
                if(check==false) continue;

                //               check= checkLine( path1,start.getLat(),end.getLat());
//              if(check==false) continue;

                List<String> path2 = graphFast.Search( points.get(i).first, end.getCode(), shipHeight,  maxDepth,  shipTon,laneNames,NoData);
//              path1.addAll(path2);

//                check=  checkLineSecond(  path2,start.getLat(),end.getLat(),false);
//                if(check==false) continue;

                check= checkLineSecondAngle(  path2, false);
                if(check==false) continue;

                if(path1.size()<=1||path2.size()<=1) continue;

//              check= checkAngle( path1, path2,start.getLat(),end.getLat());
//              if(check==false) continue;


                pathAdd(path1, path2);

                check=   checkExtendPath( path1);
                if(check==false) continue;

                check=  checkLineAngle(  path1,start.getLat(),end.getLat());
                if(check==false) continue;

//              path1.addAll(path2);

                paths.add(path1);

//              System.out.println("code = " + points.get(i).first);


            }
        }


        return  paths;
    }



    private  List<List<String>> chengShanHangZhouCase(ChengShanAndHangZhouCase cshzCase,List<String> path,LinePoint start,LinePoint end,double maxDepth,double shipHeight,  double shipTon,String laneNames){

        List<Tuple<String,String>> codes= cshzCase.findCodes( path,  start, end);

        List<List<String>> paths=new ArrayList<>();
        boolean check;
        for(Tuple<String,String> tuple:codes)
        {
            List<String> path1 = graphFast.Search(start.getCode(), tuple.first, shipHeight,  maxDepth,  shipTon,laneNames,NoData);
            List<String> path2 = graphFast.Search(tuple.first, tuple.second, shipHeight,  maxDepth,  shipTon,laneNames,NoData);
            List<String> path3 = graphFast.Search(tuple.second, end.getCode(), shipHeight,  maxDepth,  shipTon,laneNames,NoData);

            if(path1.size()<=1||path2.size()<=1||path3.size()<=1) continue;

            pathAdd(path1, path2);
            pathAdd(path1, path3);

            check=   checkExtendPath( path1);
            if(check==false) continue;

            paths.add(path1);

        }

        return  paths;
    }


    private    List<List<String>> zhouShanSouthCase(LinePoint start,LinePoint end,double maxDepth,double shipHeight,  double shipTon,String laneNames){

        List<List<String>> paths=new ArrayList<>();

        //yulj:如果航线方向是大体竖直，则要考虑定线制
        if(zssCase.checkLineDirect(start,end)) {
            List<String> lnames=new ArrayList<>();
            if (!StringUtils.isEmpty(laneNames))
            {
                String[]items= laneNames.split(",|，");
                for(String item:items)
                {
                    lnames.add(item);
                }
            }

            boolean check=true;
            zssCase.getPos(start,end);
            List<String> startCodes=zssCase.getStartCodes();
            List<String> endCodes=zssCase.getEndCodes();

            Tuple<List<String>,String> sline=zhouShanSouthCaseGetLine( startCodes, start.getCode(), true, maxDepth, shipHeight,   shipTon, laneNames);
            Tuple<List<String>,String> eline=zhouShanSouthCaseGetLine( endCodes, end.getCode(), false, maxDepth, shipHeight,   shipTon, laneNames);
            if(sline==null||eline==null) return  paths;

//            System.out.println("startCode = " + sline.second);
//            System.out.println("endCode = " + eline.second);
            Tuple< List<String>, List<BaseLine>> newLines= zssCase.getLines( sline.second, eline.second);
            for(int i=0;i<newLines.second.size();++i)
            {
                BaseLine line =newLines.second.get(i);
                check = checkConditions(line, shipHeight, maxDepth, shipTon, lnames, NoData);
                if (check == false) {
                    break;
                }
            }
            List<String> casePath=newLines.first;

            if(check)
            {

                List<String> path1 = sline.first;
                List<String> path2 =eline.first;

                if(path1.size()<=1||path2.size()<=1) return paths;

                pathAdd(path1, casePath);
                pathAdd(path1, path2);

                check=   checkExtendPath( path1);
                if(check==false)  return paths;

                paths.add(path1);
            }

        }


        return paths;
    }

    private  Tuple<List<String>,String> zhouShanSouthCaseGetLine(List<String> codes,String code,boolean isstart,double maxDepth,double shipHeight,  double shipTon,String laneNames){

        List<Triple<List<String>,String,Double>> lines=new ArrayList<>();
        for(int i=0;i<codes.size()-1;++i)
        {
            List<String> path ;
            String first;

            if(isstart)
            {
                path = graphFast.Search(code, codes.get(i), shipHeight,  maxDepth,  shipTon,laneNames,NoData);

                if(path.size()<2) continue;

                first=path.get(path.size()-2);

            }
            else {
                path = graphFast.Search(codes.get(i),code,  shipHeight,  maxDepth,  shipTon,laneNames,NoData);
                if(path.size()<2) continue;
                first=path.get(1);
            }

            String center=codes.get(i);
            String second=codes.get(i+1);

            LinePoint firstp=allpoints.stream().filter(a->a.getCode().equals(first)).findFirst().get();
            LinePoint centerp=allpoints.stream().filter(a->a.getCode().equals(center)).findFirst().get();
            LinePoint secondp=allpoints.stream().filter(a->a.getCode().equals(second)).findFirst().get();

            Epoint efirst=new Epoint(firstp.getLon(),firstp.getLat());
            Epoint ecenter=new Epoint(centerp.getLon(),centerp.getLat());
            Epoint esecond=new Epoint(secondp.getLon(),secondp.getLat());

            Epoint vec1=Epoint.MVec(efirst,ecenter);
            Epoint vec2=Epoint.MVec(ecenter,esecond);

            double dot=Epoint.dot(vec1,vec2);

            if(dot>=0)
            {
                RouteLine routeLine=  getRouteLine(path);
                lines.add(new Triple<>(path,codes.get(i),routeLine.getLen()));
            }

        }

        if(lines.size()>0)
        {
            double minlen=  lines.stream().mapToDouble(a->a.Third).min().getAsDouble();
            Triple<List<String>,String,Double>line=  lines.stream().filter(a->a.Third==minlen).findFirst().get();
            return new Tuple<>(line.first,line.second);
        }


        return null;

    }

    private boolean checkConditions(BaseLine line,double shipHeight, double shipDepth, double shipTon,List<String> lnames,double factor)
    {
        if(shipDepth!=NoData&&line.getDraught().doubleValue()!=NoData&&line.getDraught()<shipDepth)  return  false;
        if(shipTon!=NoData&&line.getTonnage().doubleValue()!=NoData&&line.getTonnage()<shipTon)  return  false;
        if(shipHeight!=NoData&&line.getHigh().doubleValue()!=NoData&&line.getHigh()<shipHeight)  return  false;

        if(lnames.size()>0&&line.getLane())
        {
            for(String name:lnames)
            {
                if(name.equals(line.getName())) return  false;
            }
        }

        return  true;
    }

    private void initZssCase()
    {
        String  startCode="E-0001436";//
        String  endCode="E-0007779";
//        E-0000772
        List<String> path = graphFast.Search(startCode, endCode, NoData,  NoData,  NoData,"",NoData);

        startCode="E-0007779";
        endCode="E-0000772";
        List<String> path2 = graphFast.Search(startCode, endCode, NoData,  NoData,  NoData,"",NoData);
        path.addAll(path2.subList(1,path2.size()));

        startCode="E-0000772";
        endCode="E-0006098";
        path2 = graphFast.Search(startCode, endCode, NoData,  NoData,  NoData,"",NoData);
        path.addAll(path2.subList(1,path2.size()));

        Tuple<List<LinePoint>,List<BaseLine>>  tuple= getPathNodes(  path);
        zssCase.setUpPoints(tuple.first);
        zssCase.setUpLines(tuple.second);


        startCode="E-0006098";
        endCode="E-0000772";
//        E-0000772
        path = graphFast.Search(startCode, endCode, NoData,  NoData,  NoData,"",NoData);
        startCode="E-0000772";
        endCode="E-0007779";
        path2 = graphFast.Search(startCode, endCode, NoData,  NoData,  NoData,"",NoData);
        path.addAll(path2.subList(1,path2.size()));

        startCode="E-0007779";
        endCode="E-0001436";
        path2 = graphFast.Search(startCode, endCode, NoData,  NoData,  NoData,"",NoData);
        path.addAll(path2.subList(1,path2.size()));

        tuple= getPathNodes(  path);
        zssCase.setDownPoints(tuple.first);
        zssCase.setDownLines(tuple.second);

//        startCode="E-0000610";
//        endCode="E-0008032";
//
//        path = graphFast.Search(startCode, endCode, NoData,  NoData,  NoData,"",NoData);
//        tuple= getPathNodes(  path);
//
//        List<Epoint> epoints=  tuple.first.stream().map(a->Epoint.LonLatToXY(new Epoint(a.getLon(),a.getLat()))).collect(Collectors.toList());
//
//        zssCase.setBoundaryLines(epoints);

    }

    private  Tuple<List<LinePoint>,List<BaseLine>> getPathNodes( List<String> path) {
        List<LinePoint> points = new ArrayList<>();
        List<BaseLine> lines = new ArrayList<>();
        for (int i = 0; i < path.size()-1; ++i) {
            String code = path.get(i);
            LinePoint point = allpoints.stream().filter(a -> a.getCode().equals(code)).findFirst().get();
            points.add(point);

            String start = path.get(i);
            String end = path.get(i + 1);
            BaseLine line = alllines.stream().filter(a -> (a.getStartCode().equals(start) && a.getEndCode().equals(end)) || (a.getEndCode().equals(start) && a.getStartCode().equals(end))).findFirst().get();
            lines.add(line);

        }

        return new Tuple<>(points,lines);
    }


    private  boolean checkExtendPath(List<String> path)
    {
//        boolean check= checkLine( path,2);
//        if(check==false) return false;
//        check= checkLine( path,3);
//        if(check==false) return false;
//        check= checkLine( path,4);
//        if(check==false) return false;
//        check= checkLine( path,5);
//        if(check==false)return false;

        boolean   check=checkLine( path);
        if(check==false)return false;

        return true;
    }


    private  void pathAdd(List<String> path1,List<String> path2){

        int index=0;
        for(int j=0;j<path2.size();++j)
        {
            int indexi=path1.size()-1-j;
            if(indexi<0) break;
            if(!path2.get(j).equals(path1.get(indexi)))
            {
                index=j;
                break;
            }
        }

        path1.addAll(path2.subList(index,path2.size()));
    }


    // 利用统计方式 检测 航程较大的航线
    private  List<RouteLine> checkLen(List<RouteLine> routeLines,double maxDepthPathLen){

        if(routeLines.size()==1)
        {
            double routeLen=routeLines.get(0).getLen();
            double avg=maxDepthPathLen;
            double delta=Math.sqrt((routeLen-avg)*(routeLen-avg)*0.5);

            boolean check= checkLineSegment(routeLines.get(0));
//            System.out.println("routeLen-avg="+(routeLen-avg)+"delta = " + (2*delta));
            if(routeLen-avg>2*delta||check==false)
            {
                return  new ArrayList<>();
            }

            return  routeLines;

        }
        else
        {
            List<Double> distances=   routeLines.stream().map(a->a.getLen()).collect(Collectors.toList());
            double sum= distances.stream().mapToDouble(a->a).sum();
            double avg=sum/distances.size();
            double delta= distances.stream().mapToDouble(a->(a-avg)*(a-avg)).sum()/distances.size();
            delta=Math.sqrt(delta);


            HashMap<Integer,Boolean>removeIndexs=new HashMap<>();
            for (int i =0; i < distances.size(); ++i)
            {
                double outlier =distances.get(i)-avg;
                if(outlier>delta)
                {
                    removeIndexs.put(i,true);
                }
            }

            for (int i =0; i < routeLines.size(); ++i)
            {
                boolean check= checkLineSegment(routeLines.get(i));
                if(check==false)
                {
                    removeIndexs.put(i,true);
                }
            }

            List<RouteLine> result=new ArrayList<>();
            for (int i =0; i < routeLines.size(); ++i)
            {
                if(!removeIndexs.containsKey(i))
                {
                    result.add(routeLines.get(i));
                }
            }
            return result;
        }

    }
    //检查两个港口之间的距离 至少30海里
    private  boolean checkPortLen(LinePoint startpoint,LinePoint endpoint){
        Epoint spoint=new Epoint(startpoint.getLon(),startpoint.getLat());
        Epoint epoint=new Epoint(endpoint.getLon(),endpoint.getLat());

        double distane=Epoint.MDistance(spoint,epoint);
        distane=distane/1852.0;

        if(distane<30.0) return  false;

        return  true;

    }
    //检查是否有交叉情况
    private  boolean checkLineSegment(RouteLine routeLine)
    {
        List<Epoint> epoints=  routeLine.getNodes().stream().map(a->Epoint.LonLatToXY(a.getPoint())).collect(Collectors.toList());

        Epoint meetPoint=new Epoint();

        for(int i=0;i<epoints.size()-1;++i)
        {
            Epoint pa=epoints.get(i);
            Epoint pb=epoints.get(i+1);
            for(int k=i+2;k<epoints.size()-1;++k)
            {
                Epoint pc=epoints.get(k);
                Epoint pd=epoints.get(k+1);

                boolean meet=  Line.DoubleSegmentMeet(pa,pb,pc,pd,meetPoint);
                if(meet)
                {
                    return false;
                }
            }
        }

        return true;
    }


    //检查一定间隔内的两点之间是否存在 共线
    private  boolean checkLine( List<String> path,int step)
    {
        for(int i=0;i<path.size()-step;i++)
        {
            String start=path.get(i);
            String end=path.get(i+step);
            Optional<BaseLine> line = alllines.stream().filter(a -> (a.getStartCode().equals(start) && a.getEndCode().equals(end)) ).findFirst();
            if (line.isPresent()) {
                if(line.get().getOneWayStreet()!=-1)
                {
                    return  false;
                }
            }

            line = alllines.stream().filter(a -> (a.getStartCode().equals(end) && a.getEndCode().equals(start)) ).findFirst();
            if (line.isPresent()) {
                if(line.get().getOneWayStreet()!=1)
                {
                    return  false;
                }
            }
        }

        return true;
    }


    //检查是否存在重复的点
    private  boolean checkLine( List<String> path)
    {
//        String text=String.join(",",path);
//        System.out.println("text = " + text);

        for(int i=0;i<path.size();i++)
        {
            String code=path.get(i);
            List<String>codes= path.stream().filter(a->a.equals(code)).collect(Collectors.toList());
            if(codes.size()>1)
            {
                return  false;
            }
        }

        return true;
    }


    //检查 第二个点是否在lat范围外
    private  boolean checkLineSecond( List<String> path,double startlat,double endlat,boolean last)
    {
        if(path.size()>=2)
        {
            String code=path.get(1);
            if(last)
            {
                code=path.get(path.size()-2);
            }
            String finalCode = code;
            Optional<LinePoint> pointOp= allpoints.stream().filter(a->a.getCode().equals(finalCode)).findFirst();
            if(pointOp.isPresent())
            {
                LinePoint point=pointOp.get();
                if((point.getLat()-startlat)*(point.getLat()-endlat)>0 )
                {

                    return false;
                }
            }
        }

        return true;
    }

    //检查 第二个点形成的角度
    private  boolean checkLineSecondAngle( List<String> path,boolean last)
    {
        if(path.size()>=3)
        {
            String centerCode=path.get(1);
            String firstCode=path.get(0);
            String secondCode=path.get(2);

            if(last)
            {
                centerCode=path.get(path.size()-2);
                firstCode=path.get(path.size()-1);
                secondCode=path.get(path.size()-3);
            }

            String finalCenterCode = centerCode;
            Optional<LinePoint> centerOp= allpoints.stream().filter(a->a.getCode().equals(finalCenterCode)).findFirst();

            String finalFirstCode = firstCode;
            Optional<LinePoint> firstOp= allpoints.stream().filter(a->a.getCode().equals(finalFirstCode)).findFirst();

            String finalSecondCode = secondCode;
            Optional<LinePoint> secondOp= allpoints.stream().filter(a->a.getCode().equals(finalSecondCode)).findFirst();

            if(firstOp.isPresent()&&centerOp.isPresent()&&secondOp.isPresent())
            {
                Epoint center=new Epoint(centerOp.get().getLon(),centerOp.get().getLat());
                Epoint first=new Epoint(firstOp.get().getLon(),firstOp.get().getLat());
                Epoint second=new Epoint(secondOp.get().getLon(),secondOp.get().getLat());
                center=Epoint.LonLatToXY(center);
                first=Epoint.LonLatToXY(first);
                second=Epoint.LonLatToXY(second);

                Epoint fvec=Epoint.Vec(center,first);
                Epoint svec=Epoint.Vec(center,second);

                double dot=Epoint.dot(fvec,svec);
                if(dot>0)
                {
                    double ldot=  Epoint.VecLen(fvec)*Epoint.VecLen(svec)*Math.cos(30.0/180.0*Math.PI);
                    if(dot<ldot)
                    {
                        return  false;
                    }
                }

            }
        }

        return true;
    }
    //检测 在lat范围外的 航程
    private  boolean checkLine( List<String> path,double startlat,double endlat)
    {
//        String text=String.join(",",path);
//        System.out.println("text = " + text);

        List<LinePoint> linePoints=new ArrayList<>();
        for(int i=0;i<path.size();i++)
        {
            String code=path.get(i);
            Optional<LinePoint> pointOp= allpoints.stream().filter(a->a.getCode().equals(code)).findFirst();
            if(pointOp.isPresent())
            {
                linePoints.add(pointOp.get());
            }
        }



        double length=0;
        double pathLen=0;
        for(int i=0;i<linePoints.size()-1;i++)
        {
            LinePoint start=linePoints.get(i);
            LinePoint end=linePoints.get(i+1);
            double tmp=pointDistance( start , end);
            pathLen+=tmp;


            if((start.getLat()-startlat)*(start.getLat()-endlat)>0
                    &&(end.getLat()-startlat)*(end.getLat()-endlat)>0)
            {

                length+=tmp;
            }
        }


        if(length/pathLen>0.8)
        {
            return  false;
        }

        return true;
    }

    private  boolean checkLineAngle( List<String> path,double startlat,double endlat)
    {
        double centerLat=30.1175;
        if((centerLat-startlat)*(centerLat-endlat)>0)return  true;
        double maxLat=Math.max(startlat,endlat);
        for(int i=0;i<path.size();i++)
        {
            String code=path.get(i);

            Optional<LinePoint> pointOp= allpoints.stream().filter(a->a.getCode().equals(code)).findFirst();
            if(pointOp.isPresent())
            {
                if(pointOp.get().getLat()>maxLat)
                {
                    return  false;
                }
            }
        }

        return  true;


    }

    //检测舟山以南区域 航线方向
    private  boolean checkAngle(List<String> path1,List<String> path2,double startlat,double endlat){
        double lat=29.416666666666668;
        if(startlat<=lat&&endlat<=endlat)
        {
            String center=path2.get(0);
            String first=path1.get(path1.size()-2);
            String second=path2.get(1);
            LinePoint centerp=  allpoints.stream().filter(a->a.getCode().equals(center)).findFirst().get();
            LinePoint firstp=  allpoints.stream().filter(a->a.getCode().equals(first)).findFirst().get();
            LinePoint secondp=  allpoints.stream().filter(a->a.getCode().equals(second)).findFirst().get();

            Epoint cp=new Epoint(centerp.getLon(),centerp.getLat());
            cp=Epoint.LonLatToXY(cp);

            Epoint fp=new Epoint(firstp.getLon(),firstp.getLat());
            fp=Epoint.LonLatToXY(fp);

            Epoint sp=new Epoint(secondp.getLon(),secondp.getLat());
            sp=Epoint.LonLatToXY(sp);


            Epoint vf=Epoint.Vec(fp,cp);
            Epoint vs=Epoint.Vec(cp,sp);

            double dot= Epoint.dot(vf,vs);
            if(dot<0)
            {
                return  false;
            }

            return  true;
        }
        else
        {

            return  true;
        }

    }

    private  boolean chekRange(Epoint point)
    {

        Epoint mpoint=Epoint.LonLatToXY(point);

//        String text="122.9109061606,30.6603470360,122.8669608481,30.4131379033,122.8080880658,30.1873145654,122.6287642117,29.8087945940,122.5062046135,29.6478161007,122.7218113030,29.6000646613,123.0912265862,29.5140550868,123.2925925845,30.1761555431,123.4363517267,30.6193019273";
        String text="122.8976766970,37.5604789599,122.9100117234,36.8196040467,122.8901609330,35.8797207836,122.8874143510,34.9443947718,122.8092554866,33.7351338504,122.8576112097,31.6431479801,122.8631043738,30.9465548195,122.9152894324,30.7324806147,122.9015565222,30.6412980535,122.8751431851,30.4902709937,122.8077825035,30.3103635795,122.7612962131,30.1210731788,122.6967233459,29.9681617854,122.6057630804,29.7655698680,122.5313646391,29.6706800625,122.4756726951,29.3175212969,122.4674329490,28.7907589169,122.0583928060,28.0742365149,121.8534350878,27.8895361502,121.4747350419,27.3341206877,120.6498228764,26.4018491921,120.1514580103,25.6212667901,119.8698133941,25.3148097767,119.4793686766,24.9784002735,118.8485657269,24.5183532551,117.8624578170,23.7695732636,117.0913740125,23.2893141890,116.6150811368,22.8996267386,116.4675267455,22.8166597306,115.5516766531,22.4523616585,114.9072432472,22.2448354405,114.2664443217,22.1051175115,114.1177981026,21.7681737648,113.9530031807,21.6558978691,112.1666865907,21.3871236888,111.3073311774,21.1899079709,110.9530220954,21.1604549327,110.7202407791,21.1090505907,110.7957717850,20.8063951106,110.9990817011,20.6467778044,111.2984591425,20.6159325840,111.5401583613,20.6724773763,113.5600732608,21.1485941809,114.7041621665,21.8077077270,116.0018998569,22.1841308019,117.8064309417,23.1632166258,119.5956915267,24.1701625201,120.5404815718,25.0388928283,120.8918474369,25.9285866814,121.9006709122,27.4001376950,122.8815174266,28.5857223512,123.2822582235,29.3720387583,123.5081774256,30.3461989674,123.6110773436,31.3675678916,123.4755052894,32.4604617718,123.5221971839,33.2169509911,123.4850575429,34.1506534436,123.5238754480,35.7630495377,123.4663297528,36.7785621823,123.4357408297,37.5139762795";

        String []items=  text.split(",");
//        System.out.println("items = " + items.length);
        List<Epoint> epoints=new ArrayList<>();
        for(int i=0;i<items.length;i+=2)
        {
            double x=Double.parseDouble(items[i]);
            double y=Double.parseDouble(items[i+1]);
            Epoint epoint=new Epoint(x,y);
            epoint=Epoint.LonLatToXY(epoint);
            epoints.add(epoint);
        }
        epoints.add(epoints.get(0));


//        System.out.println("epoints = " + epoints.size());

        RouteLineFence fence=new RouteLineFence();
        fence.setAllLinePoints(epoints);
        return  fence.isInPolygonNormal(mpoint);
    }

    private  double pointDistance(LinePoint start ,LinePoint end){

        Epoint pa=new Epoint(start.getLon(),start.getLat());
        Epoint pb=new Epoint(end.getLon(),end.getLat());

        return Epoint.MDistance(pa,pb);

    }


    private double findDepths(List<BaseLine> startlines, List<BaseLine> endlines, List<Double> depths) {
        double maxDepth=NoData;

        OptionalDouble maxStartOp=  startlines.stream().filter(a->a.getDraught().doubleValue()!=NoData).mapToDouble(a->a.getDraught()).max();
        OptionalDouble maxEndOp= endlines.stream().filter(a->a.getDraught().doubleValue()!=NoData).mapToDouble(a->a.getDraught()).max();
        if(maxStartOp.isPresent()&&maxEndOp.isPresent())
        {
            double minDepth=Math.min(maxStartOp.getAsDouble(),maxEndOp.getAsDouble());

            for(double val=0;val<minDepth;val+=2.0)
            {
                depths.add(val);
            }

//            double  testDepth=(int)(minDepth)+0.5;
//            if(minDepth>=testDepth)

                depths.add(minDepth);
            maxDepth=minDepth;

        }
        else
        {
            depths.add(0.0);
        }

        return maxDepth;


    }

    private Node linePointToNode(LinePoint point)
    {
        Node node = new Node();
        node.setName(point.getName());
        node.setCode(point.getCode());
        Epoint epoint = new Epoint(point.getLon().floatValue(), point.getLat().floatValue());
        node.setPoint(epoint);
        node.setHarbour(false);
        return node;
    }

    private void findRelationNodes(Node node, List<BaseLine> alllines,List<Node> allnodes)
    {
        List<RelationNode> nodes = new ArrayList();
        for (BaseLine line : alllines)
        {
            if (line.getOneWayStreet()!=-1&&(line.getStartCode().equals(node.getCode())))
            {// 1 0
                String nodecode = line.getEndCode();
                Node tmpnode = findNode(allnodes,nodecode);
                if(tmpnode==null) continue;

                RelationNode relationNode=new RelationNode(tmpnode, line.getOneWayStreet(),line.getDraught(), line.getTonnage(), line.getHigh(),line.getLane(),line.getName());
//                relationNode.setNode(tmpnode);
//                relationNode.setDepth(line.getDraught());
//                relationNode.setDirection(line.getOneWayStreet());
                nodes.add(relationNode);
            }

            if (line.getOneWayStreet()!=1 && (line.getEndCode().equals(node.getCode())))
            {// -1 0
                String nodecode = line.getStartCode();
                Node tmpnode = findNode(allnodes,nodecode);
                if(tmpnode==null) continue;

                RelationNode relationNode=new RelationNode(tmpnode, line.getOneWayStreet(),line.getDraught(), line.getTonnage(), line.getHigh(),line.getLane(),line.getName());
//                relationNode.setNode(tmpnode);
//                relationNode.setDepth(line.getDraught());
//                relationNode.setDirection(line.getOneWayStreet());
                nodes.add(relationNode);
            }
        }
        node.setRelationNodes(nodes);
    }

    private Node findNode(List<Node> nodes,String code)
    {
        for (Node node : nodes)
        {
            if (node.getCode().equals(code))
            {
                return node;
            }
        }
        return null;
    }

    private boolean findPoint(LinePoint point,List<BaseLine> lines)
    {
        for (BaseLine line : lines)
        {
            if(point.getCode().equals(line.getStartCode()))
            {
                return true;
            }

            if(point.getCode().equals(line.getEndCode()))
            {
                return  true;
            }
        }

        return  false;
    }

    public boolean isExtend() {
        return extend;
    }

    public void setExtend(boolean extend) {
        this.extend = extend;
    }
}
