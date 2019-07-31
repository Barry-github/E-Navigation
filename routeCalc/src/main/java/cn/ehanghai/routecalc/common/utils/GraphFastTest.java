package cn.ehanghai.routecalc.common.utils;

import cn.ehanghai.routecalc.common.math.Epoint;
import cn.ehanghai.routecalc.nav.domain.RouteLine;
import net.ryian.commons.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GraphFastTest {

    private  double NoData=-9999;
    private HashMap<String, List<GraphVertix>> vertices=new HashMap<>();

    private  HashMap<String,Double> innerFactors=new HashMap<>();

    private List<RouteLineFence> routeLineFences=new ArrayList<>();

    private  double minFactor;


    private List<String> shortest_path(String start, String finish,double shipHeight, double shipDepth, double shipTon,String laneNames,double factor)
    {
//        start="E-0000050";
//        finish="E-0000129";
//        double endlon=122.09;
//        double endlat=30.6;

        minFactor=Double.MAX_VALUE;
        HashMap<String, String> previous = new HashMap<>();
        HashMap<String, Double> distances = new HashMap<>();
        List<String> nodes = new ArrayList<>();


        List<String> path = new ArrayList<>();

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

        while (nodes.size() != 0)
        {
            Collections.sort(nodes,(o1, o2)->{
                double dis1=distances.get(o1);
                double dis2=distances.get(o2);
                if(dis1>dis2) return  1;
                if(dis1<dis2) return  -1;
                return 0;

            });


            String smallest = nodes.get(0);
            nodes.remove(smallest);

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

            if (distances.get(smallest) ==  Integer.MAX_VALUE)
            {
                break;
            }


//            if(smallest.equals("E-0007777"))
//            {
//                System.out.println("smallest = " + smallest);
//            }


            List<GraphVertix> graphVertices= vertices.get(smallest);
//            graphVertices= graphVertices.stream().filter(a->checkConditions( a,  shipHeight,  shipDepth,  shipTon,lnames)).collect(Collectors.toList());


//            List<GraphVertix> tmpdatas=   graphVertices.stream().filter(a->a.getInnerFactor()<30).collect(Collectors.toList());
//if(tmpdatas.size()>0)
//{
//    graphVertices=tmpdatas;
//}



            //            if(tmpdatas.size()<=0) {
//                tmpdatas=   graphVertices.stream().filter(a->a.getInnerFactor()<100).collect(Collectors.toList());
//                Collections.sort(tmpdatas,(a,b)->{return a.getInnerFactor()-b.getInnerFactor();});
//            }
//            else
//            {
//                Collections.sort(tmpdatas,(a,b)->{return b.getInnerFactor()-a.getInnerFactor();});
//            }
//
//            if(tmpdatas.size()>0) {
//                tmpdatas= tmpdatas.stream().filter(a->checkConditions( a,  shipHeight,  shipDepth,  shipTon,lnames)).collect(Collectors.toList());
//
//
//                GraphVertix vertix=tmpdatas.get(0);
//                double neighborvalue=vertix.getDistance();
//                double alt = distances.get(smallest) +0;// neighborvalue;
//                String  neighborkey=vertix.getCode();
//                if (alt < distances.get(neighborkey))
//                {
//                    distances.put(neighborkey,alt);
//                    previous.put(neighborkey,smallest);
//                }
//
//            }else
//            {
                for(GraphVertix vertix:graphVertices)
                {
                    boolean check=  checkConditions( vertix,  shipHeight,  shipDepth,  shipTon,lnames, factor);
                    if(check==false) continue;

//                    Double distance=Node.GetDistBetwTwoPoint(vertix.getLon(),vertix.getLat(), endlon,endlat);

//                    double neighborvalue=(distance+vertix.getDistance())*0.006+vertix.getInnerFactor()*0.01*0.4;


                    double neighborvalue=vertix.getDistance();//vertix.getDistance()+
                    if(factor!=NoData){
                        neighborvalue=vertix.getDistance()+vertix.getInnerFactor();
                    }

                    double alt = distances.get(smallest) + neighborvalue;

                    String  neighborkey=vertix.getCode();
                    if (alt < distances.get(neighborkey))
                    {
                        distances.put(neighborkey,alt);
                        previous.put(neighborkey,smallest);
                    }
                }
//            }



        }

        return path;
    }

//    private  List<GraphVertix> findVertix( List<GraphVertix> graphVertices)
//    {
//        List<GraphVertix> tmpdatas=   graphVertices.stream().filter(a->a.getInnerFactor()<30).collect(Collectors.toList());
//        if(tmpdatas.size()<=0) {
//            tmpdatas=   graphVertices.stream().filter(a->a.getInnerFactor()<100).collect(Collectors.toList());
//        }
//
//        if(tmpdatas.size()>0) {
//            tmpdatas.stream().filter()
//        }else
//        {
//
//        }
//
//
//        return  graphVertices;
//
//    }

    private boolean checkConditions(GraphVertix vertix,double shipHeight, double shipDepth, double shipTon,List<String> lnames,double factor)
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

//        if(vertix.getInnerFactor()<2715) return  false;

//if(vertix.getLon()>123) return  false;

        return  true;
    }


    public void InitGraph(List<Node> allvertices)
    {
//        initFactor();
        initRoueLineFence();

        for (Node node : allvertices)
        {
//            if(node.getCode().equals("E-0007777"))
//            {
//                System.out.println("smallest = " + node.getCode());
//            }

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
                    Double distance=Node.distance(node, adjnode.getNode());


//                    if(code.equals("E-0006132"))
//                    {
//                        System.out.println("code = " + code);
//                    }


                    double factor=clacFactor(adjnode.getNode());

//                    double factor=findInnerFactor(node.getCode(),code);
//                    int factor= (int)((150-adjnode.getNode().getPoint().getX())*100);

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
        double factor=Node.GetDistBetwTwoPoint(node.getPoint().getX(),node.getPoint().getY(),150,node.getPoint().getY());
//        Epoint epoint=new Epoint(node.getPoint().getX(),node.getPoint().getY());
//        double factor= calcFenceDis( epoint);
        innerFactors.put(node.getCode(),factor);

        return  factor;
    }
//    private  double clacFactor(GraphVertix vertix)
//    {
//        Epoint epoint=new Epoint(vertix.getLon(),vertix.getLat());
//        double factor= calcFenceDis( epoint);
//        return  factor;
//    }

//    private  double findInnerFactor(String start,String end)
//    {
//        int factor=100;
//        String key=start+"_"+end;
//        if(innerFactors.containsKey(key))
//        {
//            factor=innerFactors.get(key);
//        }
//
//        return  factor;
//
//    }
//
//    private  void initFactor()
//    {
//        innerFactors.put("E-0007777_E-0007690",  50);
//        innerFactors.put("E-0007690_E-0000610",  50);
//        innerFactors.put("E-0000610_E-0000611",  50);
//        innerFactors.put("E-0000610_E-0004689",  45);
//        innerFactors.put("E-0000610_E-0002497",  40);
//        innerFactors.put("E-0000610_E-0006132",  35);
//        innerFactors.put("E-0006132_E-0002497",  30);
//
//
//        innerFactors.put("E-0007690_E-0007777",    50);
//        innerFactors.put("E-0000610_E-0007690",    50);
//        innerFactors.put("E-0000611_E-0000610",    50);
//        innerFactors.put("E-0004689_E-0000610",    45);
//        innerFactors.put("E-0002497_E-0000610",    40);
//        innerFactors.put("E-0006132_E-0000610",    35);
//        innerFactors.put("E-0002497_E-0006132",    30);
//
//    }

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
            epoint=Epoint.LonLatToXY(epoint);
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


//    private  double calcFenceDis(Node node){
//        Epoint epoint=new Epoint(node.getPoint().getX(),node.getPoint().getY());
//        return calcFenceDis( epoint);
//    }
    private  double calcFenceDis(Epoint epoint){

        epoint=Epoint.LonLatToXY(epoint);

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
        GraphFastTest test=new GraphFastTest();
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
