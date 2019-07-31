package cn.ehanghai.routecalc.common.utils;

import net.ryian.commons.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GraphFast {

    private  double NoData=-9999;
    private   HashMap<String,List<GraphVertix>> vertices=new HashMap<>();

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
                    smallest = previous.get(smallest);
                }

                break;
            }

            if (distances.get(smallest) ==  Integer.MAX_VALUE)
            {
                break;
            }


            List<GraphVertix> graphVertices= vertices.get(smallest);
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
                    graphVertices.add(new GraphVertix(code,distance,adjnode.getDepth(),adjnode.getTon(),adjnode.getHeight(),adjnode.getLane(),adjnode.getName()));

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


}
