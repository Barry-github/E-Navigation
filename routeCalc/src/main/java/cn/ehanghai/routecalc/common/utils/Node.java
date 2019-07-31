package cn.ehanghai.routecalc.common.utils;


import cn.ehanghai.routecalc.common.math.Epoint;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;


public class Node {

    private String name = null;
    private String code;
    private boolean isHarbour;
    private Epoint point;

    private Node parent;

    private double f_n;

    private  Double depth;

    private  Double ton;
    private  Double height;




    @JSONField(serialize=false)
    private List<RelationNode> relationNodes = new ArrayList<>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isHarbour() {
        return isHarbour;
    }

    public void setHarbour(boolean harbour) {
        isHarbour = harbour;
    }

    public Epoint getPoint() {
        return point;
    }

    public void setPoint(Epoint point) {
        this.point = point;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public double getF_n() {
        return f_n;
    }

    public void setF_n(double f_n) {
        this.f_n = f_n;
    }

    public List<RelationNode> getRelationNodes() {
        return relationNodes;
    }

    public void setRelationNodes(List<RelationNode> relationNodes) {
        this.relationNodes = relationNodes;
    }


    public RelationNode getRelationNodes(int index, int maxnum)
    {
        if (index < this.relationNodes.size() && index < maxnum)
        {
            return this.relationNodes.get(index);
        }
        return null;

    }

    public RelationNode getRelationNodes(boolean issort, Node pNode, Stack<RelationNode> stack, int index, int maxnum)
    {
        if (issort)
        {
            Sort(pNode, stack);
        }
        if (index < this.relationNodes.size() && index < maxnum)
        {
            return this.relationNodes.get(index);
        }
        return null;

    }



    public void Sort(Node pNode, Stack<RelationNode> stack)
    {
        if (pNode != null)
        {
            this.relationNodes =  stack.stream().map(a->a).collect(Collectors.toList());

            Collections.sort(this.relationNodes ,(o1,o2)->{
             double dis1=distance(pNode,o1.getNode());
             double dis2=distance(pNode,o2.getNode());
             if(dis1>dis2) return  1;
             if(dis1<dis2) return  -1;
             return 0;

            });

        }

    }

    public static double distance(Node start, Node end)
    {

        Epoint startp= Epoint.LonLatToXY(start.getPoint());
        Epoint endp=Epoint.LonLatToXY(end.getPoint());
        return  Epoint.Distance(startp,endp);


//        return
//                Math.sqrt((start.point.getX() - end.point.getX()) * (start.point.getX() - end.point.getX()) +
//                        (start.point.getY() - end.point.getY()) * (start.point.getY() - end.point.getY()));


//        return GetDistBetwTwoPoint(start.point.getX(), start.point.getX(), end.point.getX(), end.point.getX());


    }
    public static double GetDistBetwTwoPoint(Node start,Node end)
    {
        return GetDistBetwTwoPoint(start.point.getX(), start.point.getY(), end.point.getX(), end.point.getY());
    }


    public static double GetDistBetwTwoPoint(double Lon1, double Lat1, double Lon2, double Lat2)
    {
        double earthRadiusX = 6378137;
        //#define PI 3.1415926535897931
        double aLon1 = Lon1 / 180 * Math.PI;
        double aLat1 = Lat1 / 180 * Math.PI;
        double aLon2 = Lon2 / 180 * Math.PI;
        double aLat2 = Lat2 / 180 * Math.PI;

        double dlon = aLon2 - aLon1;
        double dlat = aLat2 - aLat1;

        double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(aLat1) * Math.cos(aLat2) * Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double d = earthRadiusX / 1852 * c;
//        double d = earthRadiusX * c;
        return d;
    }

    ///================================
    public double update_h_n(Node target)
    {
        double    h_n = distance(this,target);
        return h_n;
    }

    public double update_g_n(Node source)
    {
        double  g_n = distance(this, source);
        return g_n;
    }

    public double update_f_n(Node source, Node target)
    {

        f_n = update_g_n(source) + update_h_n(target);
        return f_n;
    }

    public void update_parent(Node par)
    {
        parent = par;
    }

    public Double getDepth() {
        return depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }

    public Double getTon() {
        return ton;
    }

    public void setTon(Double ton) {
        this.ton = ton;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }
}
