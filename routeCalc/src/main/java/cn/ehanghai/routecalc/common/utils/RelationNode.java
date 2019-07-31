package cn.ehanghai.routecalc.common.utils;

public class RelationNode {
    private  Node node;
    private  double depth;

    private  int direction;
    private  double ton;
    private  double height;

    private Boolean lane;

    private  String name;

    public RelationNode(Node node, int direction,double depth,  double ton, double height,boolean lane,String name) {
        this.node = node;
        this.depth = depth;
        this.direction = direction;
        this.ton = ton;
        this.height = height;
        this.lane=lane;
        this.name=name;
    }



    public RelationNode() {
    }

    public double getTon() {
        return ton;
    }

    public void setTon(double ton) {
        this.ton = ton;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public Boolean getLane() {
        return lane;
    }

    public void setLane(Boolean lane) {
        this.lane = lane;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
