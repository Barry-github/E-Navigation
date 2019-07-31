package cn.ehanghai.route.nav.domain;


import cn.ehanghai.route.common.utils.Node;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RouteLine {
    private  double shipHeight;
    private  double depth;
    private  double shipton;
    private List<Node> nodes;

    private  String color;

    public RouteLine() {

        this.color=randomColor();
    }

    public RouteLine(double shipHeight, double depth, double shipton, List<Node> nodes) {
        this.shipHeight = shipHeight;
        this.depth = depth;
        this.shipton = shipton;
        this.nodes = nodes;
        this.color=randomColor();
    }

    public  RouteLineCheck toCheckLine()
    {
        String start= nodes.get(0).getCode();
        String end=nodes.get(nodes.size()-1).getCode();
        String lineId=start+"_"+end+"_"+depth;

        List<String> points=  nodes.stream().map(a->String.format("(%f,%f)",a.getPoint().getX(),a.getPoint().getY())).collect(Collectors.toList());

        String path=String.join(";",points);
        RouteLineCheck routeLineCheck=new RouteLineCheck();
        routeLineCheck.setLineCheck(false);
        routeLineCheck.setLineId(lineId);
        routeLineCheck.setPath(path);

        return routeLineCheck;


    }

    private  String randomColor()
    {
        Random random = new Random((int)System.currentTimeMillis());
        int r=(int)(random.nextDouble()*255.0) ;
        int g=(int)(random.nextDouble()*255.0) ;
        int b=(int)(random.nextDouble()*255.0) ;

        return String.format("%d,%d,%d",r,g,b);
    }

    public double getShipHeight() {
        return shipHeight;
    }

    public void setShipHeight(double shipHeight) {
        this.shipHeight = shipHeight;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public double getShipton() {
        return shipton;
    }

    public void setShipton(double shipton) {
        this.shipton = shipton;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
