package cn.ehanghai.routecalc.nav.action;

import cn.ehanghai.routecalc.common.math.Epoint;
import cn.ehanghai.routecalc.common.utils.Graph;
import cn.ehanghai.routecalc.common.utils.Node;
import cn.ehanghai.routecalc.common.utils.RelationNode;
import cn.ehanghai.routecalc.common.utils.WebService;
import cn.ehanghai.routecalc.nav.domain.BaseLine;
import cn.ehanghai.routecalc.nav.domain.LinePoint;
import cn.ehanghai.routecalc.nav.domain.RouteLine;

import javax.xml.ws.WebEndpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RouteLineCalc
{

    private  double NoData=-9999;

    private   List<LinePoint> allpoints;
    private   List<BaseLine> alllines;
    private   List<Node>  allNodes;

    private  int lineType;

    private boolean randomColor;

    public RouteLineCalc(List<LinePoint> allpoints, List<BaseLine> alllines,int lineType,boolean randomColor) {
        this.allpoints = allpoints;
        this.alllines = alllines;
        this.lineType=lineType;
        this.randomColor=randomColor;
        init();
    }

    private void  init()
    {

        allNodes = new ArrayList();
        for (LinePoint point : allpoints)
        {
//            if(point.getCode().equals("E-0001033"))
//            {
//                System.out.println("null");
//            }

            boolean find=  findPoint( point,alllines);

            if(find)
                allNodes.add(linePointToNode(point));
        }

        for (Node node : allNodes)
        {
//            if(node.getCode().equals("E-0004743"))
//            {
//                System.out.println("null");
//            }
            findRelationNodes(node, alllines,allNodes);
        }
    }

    private     void CheckUnknowDepth(List<RouteLine> routeLines)
    {
        for(int i=0;i<routeLines.size();++i)
        {
            if(routeLines.get(i).getDepth()<0)
            {
                double mindepth=Double.MAX_VALUE;

                List<Node> nodes=routeLines.get(i).getNodes();
                for(int j=0;j<nodes.size()-1;++j)
                {
                    String start=nodes.get(j).getCode();
                    String end=nodes.get(j+1).getCode();

                    Optional<BaseLine> baseLineOp=  alllines.stream().filter(a->(a.getStartCode().equals(start)&&a.getEndCode().equals(end))||(a.getEndCode().equals(start)&&a.getStartCode().equals(end))).findFirst();
                    if(baseLineOp.isPresent())
                    {
                        if(mindepth>baseLineOp.get().getDraught())
                        {
                            mindepth=baseLineOp.get().getDraught();
                        }
                    }
                }
                routeLines.get(i).setDepth(mindepth);

            }
        }
    }

    public List<RouteLine> getRouteLines(String startCode, String endCode, double shipheight, double depth, double shipton)
    {
        List<RouteLine> resultLines=new ArrayList<>();
        List<RouteLine> lines=getRouteLines(startCode,endCode,depth);

//        CheckUnknowDepth(lines);

        for(RouteLine line : lines)
        {
            boolean match=true;
            if(shipheight!=NoData&&line.getShipHeight()<shipheight)
            {
                match=false;
            }
            if(depth!=NoData&&
                    line.getDepth()<depth)
            {
                match=false;
            }

            if(shipton!=NoData&&line.getShipton()<shipton)
            {
                match=false;
            }

            if(match)
            {
                resultLines.add(line);
            }
        }




        return  resultLines;

    }



    private List<RouteLine> getRouteLines(String startCode,String endCode,Double shipDepth)
    {
        List<RouteLine> routeLines=new ArrayList<>();

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

        List<Double> depths = new ArrayList<>();
        findDepths(startlines, endlines, depths);

        if(shipDepth!=NoData)
        depths.add(shipDepth);

        depths.add(-9999.0);

        for (double depth : depths)
        {
            Graph graph = new Graph();
            graph.InitGraph(allNodes,depth);
            List<String> path = graph.Search(startCode, endCode);
            if (path.size() > 1)
            {
                double minheight=NoData;
                double minton=NoData;
                double mindepth=NoData;

                List<Node> nodes = new ArrayList();

                for(int i=0;i<path.size();++i)
                {
                    String code=path.get(i);
                    Node node = findNode(allNodes,code);
                    nodes.add(node);

                    if(i<path.size()-1)
                    {
                        String start=code;
                        String end=path.get(i+1);
                        Optional<BaseLine> line=  alllines.stream().filter(a->(a.getStartCode().equals(start)&&a.getEndCode().equals(end))||(a.getEndCode().equals(start)&&a.getStartCode().equals(end))).findFirst();
                        if(line.isPresent())
                        {
                            if(line.get().getHigh()<minheight)
                            {
                                minheight=line.get().getHigh();
                            }
                            if(line.get().getTonnage()<minton)
                            {
                                minton=line.get().getTonnage();
                            }

                            if(line.get().getDraught()<mindepth)
                            {
                                mindepth=line.get().getDraught();
                                System.out.println("start:"+line.get().getStartCode()+",end:"+line.get().getEndCode()+",depth:"+line.get().getDraught());
                            }
                        }
                    }

                    if(mindepth!=NoData)
                    {
                        depth=mindepth;
                    }
                }

                RouteLine  routeLine=new RouteLine(minheight,depth,minton,lineType,randomColor,nodes);
                routeLines.add(routeLine);
            }
        }

        return routeLines;
    }

    //yulj:对每个起始段，存在吃水更大的终止段，则把起始段吃水加入数组。这个不好理解
    private void findDepths(List<BaseLine> startlines, List<BaseLine> endlines, List<Double> depths) {
        for (BaseLine line : startlines) {
            if(line.getDraught()==NoData) continue;

            Optional<BaseLine> find = endlines.stream().filter(a -> a.getDraught() >= line.getDraught()).findFirst();
            if (find.isPresent()) {
                depths.add(line.getDraught());
            }
        }
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

                RelationNode relationNode=new RelationNode(tmpnode,line.getOneWayStreet(),line.getDraught(),line.getTonnage(),line.getHigh(),line.getLane(),line.getName());
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

                RelationNode relationNode=new RelationNode(tmpnode,line.getOneWayStreet(),line.getDraught(),line.getTonnage(),line.getHigh(),line.getLane(),line.getName());
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
//        System.out.println("ok");
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
}
