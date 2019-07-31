package cn.ehanghai.routecalc.common.utils;

import cn.ehanghai.routecalc.common.math.Epoint;
import net.ryian.commons.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GraphFastTest3 {

    private  double NoData=-9999;
    private HashMap<String, List<GraphVertix>> vertices=new HashMap<>();

    //yulj:计算航线段距离的调节因子？
    private  HashMap<String,Double> innerFactors=new HashMap<>();

    private List<RouteLineFence> routeLineFences=new ArrayList<>();


    private  double minFactor;


    private List<String> shortest_path(String start, String finish,double shipHeight, double shipDepth, double shipTon,String laneNames,double factor)
    {
        minFactor=Double.MAX_VALUE;
        HashMap<String, String> previous = new HashMap<>();
        HashMap<String, Double> distances = new HashMap<>();
        List<String> nodes = new ArrayList<>();
        List<String> path = new ArrayList<>();

        //yulj:这是指不能包含的港口吗？
        List<String> lnames=new ArrayList<>();
        if (!StringUtils.isEmpty(laneNames))
        {
            String[]items= laneNames.split(",|，");
            for(String item:items)
            {
                lnames.add(item);
            }
        }

        for (String vertexKey : vertices.keySet())
        {
            if (vertexKey.equals(start))
            {
                distances.put(vertexKey,0.0);
            }
            else
            {
                distances.put(vertexKey,Double.MAX_VALUE);

            }
            nodes.add(vertexKey);
        }

        HashMap<String,Boolean> colseNodes=new HashMap<>();

        while (nodes.size() != 0)
        {
            //升序排列
            Collections.sort(nodes,(o1, o2)->{
                double dis1=distances.get(o1);
                double dis2=distances.get(o2);
                if(dis1>dis2) return  1;
                if(dis1<dis2) return  -1;
                return 0;
            });

            //取出nodes中到start距离最短的点
            String smallest = nodes.get(0);
            nodes.remove(smallest);
            colseNodes.put(smallest,true);

            //已经找出到finish点的最短路径
            if (smallest.equals(finish) )
            {
                path = new ArrayList<>();
                while (previous.containsKey(smallest))
                {
                    double innerfactor=innerFactors.get(smallest);
                    if(innerfactor<minFactor) {
                        minFactor=innerfactor;
                    }

                    path.add(smallest);
                    smallest = previous.get(smallest);
                }
                break;
            }

            //全断开了，无法到达finish
            if (distances.get(smallest) ==  Integer.MAX_VALUE)
            {
                break;
            }

            List<GraphVertix> graphVertices= vertices.get(smallest);
            for(GraphVertix vertix:graphVertices)
            {
                String  neighborkey=vertix.getCode();

                if(colseNodes.containsKey(neighborkey)) continue;

                boolean check=  checkConditions( vertix,  shipHeight,  shipDepth,  shipTon,lnames, factor);

                if(check==false) {
                    distances.put(neighborkey,Double.MAX_VALUE);
                    continue;
                }

                //调整航线段距离
                double neighborvalue=vertix.getDistance();
                if(factor!=NoData){
                    neighborvalue=vertix.getDistance()+vertix.getInnerFactor();
                }

                double alt = distances.get(smallest) + neighborvalue;


                if (alt < distances.get(neighborkey))
                {
                    distances.put(neighborkey,alt);
                    previous.put(neighborkey,smallest);
                }
            }
        }

        return path;
    }


    private boolean checkConditions(GraphVertix vertix, double shipHeight, double shipDepth, double shipTon, List<String> lnames, double factor)
    {
        if(shipDepth!=NoData&&vertix.getDepth().doubleValue()!=NoData&&vertix.getDepth()<shipDepth)  return  false;
        if(shipTon!=NoData&&vertix.getTon().doubleValue()!=NoData&&vertix.getTon()<shipTon)  return  false;
        if(shipHeight!=NoData&&vertix.getHeight().doubleValue()!=NoData&&vertix.getHeight()<shipHeight)  return  false;

        if(lnames.size()>0&&vertix.getLane())
        {
            for(String name:lnames)
            {
                if(name.equals(vertix.getName())) return  false;
            }
        }

        if(factor!=NoData&&vertix.getInnerFactor().doubleValue()!=NoData&&vertix.getInnerFactor()<factor)  return  false;

        return  true;
    }


    public void InitGraph(List<Node> allvertices)
    {

//        initFactor();
//        initRoueLineFence();

        for (Node node : allvertices)
        {

            List<GraphVertix> graphVertices = new ArrayList<>();

            for (RelationNode adjnode : node.getRelationNodes())
            {
                if(adjnode!=null)
                {
                    if(adjnode.getNode()==null)
                    {
                        System.out.println("null");
                    }
                    String code=adjnode.getNode().getCode();
                    Double distance= Node.distance(node, adjnode.getNode());

                    double factor=clacFactor(adjnode.getNode());

                    GraphVertix vertix= new GraphVertix(code,distance,adjnode.getDepth(),adjnode.getTon(),adjnode.getHeight(),adjnode.getLane(),adjnode.getName());
                    vertix.setInnerFactor(factor);
                    vertix.setLon(adjnode.getNode().getPoint().getX());
                    vertix.setLat(adjnode.getNode().getPoint().getY());

                    graphVertices.add(vertix);


                }
            }
            vertices.put(node.getCode(), graphVertices);
        }


    }

    private  double clacFactor(Node node)
    {
        double factor= Node.GetDistBetwTwoPoint(node.getPoint().getX(),node.getPoint().getY(),150,node.getPoint().getY());
//        Epoint epoint=new Epoint(node.getPoint().getX(),node.getPoint().getY());
//        double factor= calcFenceDis( epoint);
        innerFactors.put(node.getCode(),factor);

        return  factor;
    }

    private  void initRoueLineFence(){
//        String text="37.9002780611,117.2698602795,31.3085340732,120.5257149185,28.9523385355,120.1521797622,25.7919164060,118.3064766372,22.0977922578,106.7049141372,17.0693225463,107.5838203872,19.5928498094,122.0418281997,23.7772394583,126.9417305435,30.9459266466,131.0349394531,38.3471449434,130.7932402344";
        String text="130.7723138951,38.3499339503,130.9001036497,28.3686474435,129.3841348573,25.2884174167,119.0207442772,17.4297212581,107.5949630272,17.1359934536,106.6940841210,22.1420582171,116.2961349022,25.0810524653,120.1852950585,29.3050838652,120.2457198631,29.5299737027,117.2164223296,37.9239229712";
        String []items=  text.split(",");
        System.out.println("items = " + items.length);
        List<Epoint> epoints=new ArrayList<>();
        for(int i=0;i<items.length;i+=2)
        {
            double x=Double.parseDouble(items[i]);
            double y=Double.parseDouble(items[i+1]);
            Epoint epoint=new Epoint(x,y);
            epoint= Epoint.LonLatToXY(epoint);
            epoints.add(epoint);
        }

        System.out.println("epoints = " + epoints.size());

        int num=epoints.size()/2;

        for(int i=0;i<num-1;++i){
            List<Epoint> points=new ArrayList<>();
            List<Epoint> line=new ArrayList<>();

            line.add(epoints.get(i));
            line.add(epoints.get(i+1));

            points.add(epoints.get(i));
            points.add(epoints.get(i+1));
            points.add(epoints.get(epoints.size()-i-2));
            points.add(epoints.get(epoints.size()-i-1));
            points.add(epoints.get(i));

            RouteLineFence fence=new RouteLineFence();
            fence.setAllLinePoints(points);

            fence.setOutLine(line);
            routeLineFences.add(fence);
        }

        System.out.println("routeLineFences = " + routeLineFences.size());
    }


    private  double calcFenceDis(Epoint epoint){

        epoint= Epoint.LonLatToXY(epoint);

        double distance=NoData;
        for(RouteLineFence fence:routeLineFences)
        {
            if(fence.isInPolygonNormal(epoint))
            {
                distance=fence.clacDistance(epoint);
                fence.add(distance);
                break;
            }
        }
        return distance;
    }

    public  void test(List<Node> allvertices){

        initRoueLineFence();
        for (Node node : allvertices)
        {
            clacFactor( node);
        }

        for(RouteLineFence fence:routeLineFences)
        {
            String text= fence.disToString();
            System.out.println("fence = " +text);
        }

    }

    public List<String> Search(String startCode, String endCode,double shipHeight, double shipDepth, double shipTon,String laneNames,double factor)
    {

        List<String> paths = shortest_path(startCode, endCode, shipHeight,  shipDepth,  shipTon,laneNames, factor);
        paths.add(startCode);
        Collections.reverse(paths);

        return paths;
    }

    public double getMinFactor() {
        return minFactor;
    }


    public static void main(String[] args) {
        double lon=123.2491700;
        double lat=30.1175000;
        //122.5825000 29.6736100
        Epoint epoint=new Epoint(lon,lat);
        GraphFastTest3 test=new GraphFastTest3();
        test.initRoueLineFence();
        double factor= test.calcFenceDis( epoint)/1852.0;
        System.out.println("factor = " + factor);


        lon=122.5825000;
        lat=29.6736100;
        //
        epoint=new Epoint(lon,lat);
        factor= test.calcFenceDis( epoint)/1852.0;
        System.out.println("factor = " + factor);


        lon=121.7211100;
        lat=30.0166660;
        epoint=new Epoint(lon,lat);
        factor= test.calcFenceDis( epoint)/1852.0;
        System.out.println("factor = " + factor);



    }
}
