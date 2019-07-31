package cn.ehanghai.routecalc.nav.domain;

import cn.ehanghai.routecalc.common.math.Epoint;
import cn.ehanghai.routecalc.common.utils.Node;
import cn.ehanghai.routecalc.common.utils.RelationNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class RouteLine {
    private  double NoData=-9999;
    private  double shipHeight;
    private  double depth;
    private  double shipton;
    private List<Node> nodes;

    private  String color;

    private  int linetype;

    private  double factor;

    public RouteLine() {
        this.color=getColor(true);
    }

    public RouteLine(double shipHeight, double depth, double shipton, int linetype, boolean randomcolor, List<Node> nodes) {
        this.shipHeight = shipHeight;
        this.depth = depth;
        this.shipton = shipton;
        this.nodes = nodes;
        this.color=getColor(randomcolor);
        this.linetype=linetype;
        this.factor=NoData;
    }

    public RouteLineCheck toCheckLine(boolean usecode)
    {

        if(usecode==false)
            nodes=thinLine( nodes);

        Node startnode=nodes.get(0);
        Node endnode=nodes.get(nodes.size()-1);

        String start=startnode.getCode();
        String end=endnode.getCode();
        String lineId=start+"_"+end+"_"+depth;
        if(factor!=NoData)lineId=lineId+"_"+factor;

        List<String> points;
        if(usecode)
        {
            points=  nodes.stream().map(a->a.getCode()).collect(Collectors.toList());
        }
        else
        {
            points=  nodes.stream().map(a->String.format("%f,%f",a.getPoint().getX(),a.getPoint().getY())).collect(Collectors.toList());
        }
        String path=String.join(",",points);
        RouteLineCheck routeLineCheck=new RouteLineCheck();
        routeLineCheck.setLineCheck(false);
        routeLineCheck.setLineId(lineId);
        routeLineCheck.setPath(path);
        routeLineCheck.setLineType(linetype);
        routeLineCheck.setColor(color);
        routeLineCheck.setDirection(-2);
        if(nodes.size()>1)
        {
            int startd=-2;
            int endd=-2;
            Node snext=nodes.get(1);
            Optional<RelationNode> findnode= startnode.getRelationNodes().stream().filter(a->a.getNode().getCode().equals(snext.getCode())).findFirst();
            if(findnode.isPresent())
            {
                startd=findnode.get().getDirection();
            }

            Node enext=nodes.get(nodes.size()-2);
            findnode= endnode.getRelationNodes().stream().filter(a->a.getNode().getCode().equals(enext.getCode())).findFirst();
            if(findnode.isPresent())
            {
                endd=findnode.get().getDirection();
            }

            if(startd==0&&endd==0)
            {
                routeLineCheck.setDirection(0);
            }
            if(startd!=endd)
            {
                routeLineCheck.setDirection(startd);
            }
        }

        return routeLineCheck;


    }
    public double getLen(){
        double len=0;
        for(int i=0;i<nodes.size()-1;++i)
        {
            Node first=nodes.get(i);
            Node second=nodes.get(i+1);
            len+=Epoint.MDistance(first.getPoint(),second.getPoint());
        }
        return  len;
    }

    //yulj:精简航线上的节点，三点在一条直线上则去掉中间点
    private  List<Node> thinLine(List<Node> nodes)
    {
        if (nodes.size() < 3) return nodes;

        //double angle = 179.5;

        List<Node> points =new ArrayList<>();

        //yulj: 两点构成的航向是否相近来判断是否需要精简，称为方法3
        points.add(nodes.get(0));
        Epoint point1 = Epoint.LonLatToXY(nodes.get(0).getPoint());
        Epoint point2 = Epoint.LonLatToXY(nodes.get(1).getPoint());
        double angle1=GetAngle(point1,point2);
        point1=point2;

        for(int i=2;i<nodes.size();i++)
        {
            point2=Epoint.LonLatToXY(nodes.get(i).getPoint());
            double angle2=GetAngle(point1,point2);
            if(Math.abs(angle1-angle2)>1)points.add(nodes.get(i-1));
            angle1=angle2;
            point1=point2;
        }

        points.add(nodes.get(nodes.size()-1));

        //yulj: 这是采用方法1、方法2来精简
        /*for (int i = 0; i < nodes.size(); ++i)
        {
            if (i > 0 && i < nodes.size() - 1)
            {
                Node first = nodes.get(i - 1);
                Node second = nodes.get(i);
                Node thrid = nodes.get(i+1);
                boolean isremove = romvePoint(first, second, thrid, angle);
                if (!isremove)
                {
                    points.add(second);
                }
            }
            else
            {
                points.add(nodes.get(i));
            }
        }*/

        return points;
    }

    //yulj:获取直线角度，正北为0，顺时针增加，参数为平面二维坐标，非经纬度
    private double GetAngle(Epoint point1,Epoint point2)
    {
        double angle;
        angle = Math.atan2((point2.getY() - point1.getY()), (point2.getX() - point1.getX()));
        angle = angle / Math.PI * 180.0;
        if (angle < 0) angle += 360;
        angle = CheckAngle(angle);

        return angle;
    }

    // 返回正确范围内的角度
    private double CheckAngle(double angle) {
        if (angle <= 90.0) {
            angle = 90.0 - angle;
        } else {
            angle = 360.0 - angle + 90.0;
        }
        return angle;
    }

    //yulj:三点构成的夹角与给定的angle是否很接近
    /*private  boolean romvePoint(Node nfirst, Node nsecond, Node nthrid, double angle)
    {
        Epoint first = Epoint.LonLatToXY(nfirst.getPoint());
        Epoint second = Epoint.LonLatToXY(nsecond.getPoint());
        Epoint thrid = Epoint.LonLatToXY(nthrid.getPoint());

        //yulj:原判断不够准确，考虑采用新的判断方法
        // (y3-y1)*(x2-x1)-(y2-y1)*(x3-x1)=0，称为方法2
        double v1=(thrid.getY()-first.getY())*(second.getX()-first.getX());
        double v2=(second.getY()-first.getY())*(thrid.getX()-first.getX());
        double v=Math.abs(v1-v2);
        if(v<500000)return true;    //500000作为误差范围，有待验证
        else return false;

        //yulj:这是最初的通过计算三点构成夹角与给定的角度是否接近来判断是否需要删除点
        //      不妨称为方法1
        //Epoint vec1 = new Epoint(first.getX() - second.getX(), first.getY() - second.getY());
        //Epoint vec2 = new Epoint(thrid.getX() - second.getX(), thrid.getY() - second.getY());

        //double product = vec1.getX()* vec2.getX() + vec1.getY() * vec2.getY();
        //double vec1Len = Math.sqrt(vec1.getX() * vec1.getX() + vec1.getY() * vec1.getY());
        //double vec2Len = Math.sqrt(vec2.getX() * vec2.getX() + vec2.getY() * vec2.getY());
        //double testpro = vec1Len * vec2Len * Math.cos(angle / 180.0 * Math.PI);
        //if (product < testpro)
        //{
        //    return true;
        //}
        //return false;
    }*/

    private  String getColor(boolean randomcolor)
    {
        if(!randomcolor)   return String.format("%d,%d,%d",0,0,255);

        Random random = new Random((int)System.currentTimeMillis());
        int r=(int)(random.nextDouble()*200.0) ;
        int g=(int)(random.nextDouble()*200.0) ;
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

    public int getLinetype() {
        return linetype;
    }

    public void setLinetype(int linetype) {
        this.linetype = linetype;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }
}
