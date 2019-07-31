package cn.ehanghai.route.common.excelimport;

import java.util.ArrayList;
import java.util.List;

public class ImportRouteLine {

    private List<ImportRouteNode> nodes;

    public ImportRouteLine() {
        this.nodes = new ArrayList<>();
    }

    public boolean IsSameLine(ImportRouteNode node)
    {
        if(nodes.size()==0) return true;
        if(node.getName().equals(nodes.get(0).getName()))
        {
            return  true;
        }
        return  false;

    }

    public List<ImportRouteNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<ImportRouteNode> nodes) {
        this.nodes = nodes;
    }

    public  void Print()
    {
        for(int i=0;i<nodes.size();++i)
        {

            System.out.println(nodes.get(i).getName()+","+nodes.get(i).getNodeIndex()+","+nodes.get(i).getAngle()+","+nodes.get(i).getLen()+","+nodes.get(i).getRemark()+","+nodes.get(i).getLon()+","+nodes.get(i).getLat());
        }
    }
}
