package cn.ehanghai.routecalc.common.utils;

import cn.ehanghai.routecalc.common.math.Epoint;
import net.ryian.commons.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GraphFastTest2 {
    private  double NoData=-9999;
    private HashMap<String, List<GraphVertix>> vertices=new HashMap<>();

    private  HashMap<String,Boolean> verticesCache=new HashMap<>();

    private  boolean enlarge;


    private List<String> shortest_path(String start, String finish,double shipHeight, double shipDepth, double shipTon,String laneNames)
    {


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
                    path.add(smallest);
                    verticesCache.put(smallest,true);

                    smallest = previous.get(smallest);
                }

                break;
            }

            if (distances.get(smallest) ==  Integer.MAX_VALUE)
            {
                break;
            }

            //

            List<GraphVertix> graphVertices= vertices.get(smallest);

            if(enlarge)
             graphVertices= findRelationNodes(smallest,  shipHeight,  shipDepth,  shipTon,lnames);

            for(GraphVertix vertix:graphVertices)
            {
                boolean check=  checkConditions( vertix,  shipHeight,  shipDepth,  shipTon,lnames);
                if(check==false) continue;

                double neighborvalue=vertix.getDistance();
                double alt = distances.get(smallest) + neighborvalue;
                String  neighborkey=vertix.getCode();
                if (alt < distances.get(neighborkey))
                {
                    distances.put(neighborkey,alt);
                    previous.put(neighborkey,smallest);
                }
            }

        }

        return path;
    }

    private boolean checkConditions(GraphVertix vertix,double shipHeight, double shipDepth, double shipTon,List<String> lnames)
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


        return  true;
    }

    private   List<GraphVertix> findRelationNodes(String smallest,double shipHeight, double shipDepth, double shipTon,List<String> lnames){
        List<GraphVertix> graphVertices= vertices.get(smallest);
        //首先判断是否有空闲点
        //如果有空闲点，， 则判断是否满足 angleCheck
        List<GraphVertix> tmpdatas=   graphVertices.stream()
                .filter(a->!verticesCache.containsKey(a.getCode())&&angleCheck(a)&&checkConditions( a,  shipHeight,  shipDepth,  shipTon,lnames))
                .collect(Collectors.toList());
        if(tmpdatas.size()>0)
        {
            graphVertices=tmpdatas;
        }


        return graphVertices;

    }

    private  boolean angleCheck(GraphVertix vertix)
    {
//        121.8500000  29.9500000
//        122.0900000  30.6000000
        Epoint start=new Epoint( 121.8500000 , 29.9500000);
        Epoint end=new Epoint(122.0900000 , 30.6000000);

        start=Epoint.LonLatToXY(start);
        end=Epoint.LonLatToXY(end);

        Epoint point=new Epoint( vertix.getLon(),vertix.getLat());

        point=Epoint.LonLatToXY(point);

        Epoint svec=Epoint.Vec(end,start);
        Epoint pvec=Epoint.Vec(end,point);

        double dot=Epoint.dot(svec,pvec);


        if(dot<0) return  false;

        return  true;
    }


    public void InitGraph(List<Node> allvertices)
    {
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
                    Double distance=Node.distance(node, adjnode.getNode());

                    GraphVertix vertix= new GraphVertix(code,distance,adjnode.getDepth(),adjnode.getTon(),adjnode.getHeight(),adjnode.getLane(),adjnode.getName());
                    vertix.setLon(adjnode.getNode().getPoint().getX());
                    vertix.setLat(adjnode.getNode().getPoint().getY());

                    graphVertices.add(vertix);
                }
            }
            vertices.put(node.getCode(), graphVertices);
        }
    }



    public List<String> Search(String startCode, String endCode,double shipHeight, double shipDepth, double shipTon,String laneNames)
    {
        List<String> paths = shortest_path(startCode, endCode, shipHeight,  shipDepth,  shipTon,laneNames);
        paths.add(startCode);
        Collections.reverse(paths);

        return paths;
    }

    public boolean isEnlarge() {
        return enlarge;
    }

    public void setEnlarge(boolean enlarge) {
        this.enlarge = enlarge;
    }
}
