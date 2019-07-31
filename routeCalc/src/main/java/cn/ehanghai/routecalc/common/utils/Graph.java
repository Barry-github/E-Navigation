package cn.ehanghai.routecalc.common.utils;


import java.util.*;

public class Graph
{

  private   HashMap<String,HashMap<String,Double>> vertices=new HashMap<>();


    public void add_vertex(String name, HashMap<String, Double> edges)
    {

        vertices.put(name,edges);
    }

    public List<String> shortest_path(String start, String finish)
    {
        HashMap<String, String> previous = new HashMap<>();
        HashMap<String, Double> distances = new HashMap<>();
        List<String> nodes = new ArrayList<>();

        List<String> path = new ArrayList<>();

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
            Collections.sort(nodes,(o1,o2)->{
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


            HashMap<String,Double> neighbormap= vertices.get(smallest);

            for(String neighborkey :neighbormap.keySet())
            {
                double neighborvalue=neighbormap.get(neighborkey);
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

    public void InitGraph(List<Node> allvertices,double depth)
    {
        for (Node node : allvertices)
        {
            HashMap<String, Double> adjdic = new HashMap<>();
//            System.out.println(node.getCode());
//            if(node.getCode().equals("E-0000972"))
//            {
//                System.out.println("ok");
//            }
            for (RelationNode adjnode : node.getRelationNodes())
            {
                if(adjnode!=null&&adjnode.getDepth()>=depth)
                {
                    if(adjnode.getNode()==null)
                    {
                        System.out.println("null");
                    }
                    adjdic.put(adjnode.getNode().getCode(),Node.distance(node, adjnode.getNode()));
                }
            }
            add_vertex(node.getCode(), adjdic);
        }
    }



    public List<String> Search(String startCode, String endCode)
    {
        List<String> paths = shortest_path(startCode, endCode);
        paths.add(startCode);
        Collections.reverse(paths);

        return paths;
    }


}