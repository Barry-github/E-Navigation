/**
 * 
 */
package cn.ehanghai.route.common.utils;

import java.util.*;

/**
* @author 胡恒博 E-mail:huhb@grassinfo.cn
* @version 创建时间：2017年7月3日 下午3:44:02
* 类说明
*/
public class Node {

    private String name = null;
    private String code;
    private boolean isHarbour ;
    private Epoint point ;

    private Node parent;

    private double f_n ;


    private List<Node> relationNodes = new ArrayList<>();

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

    public List<Node> getRelationNodes() {
        return relationNodes;
    }

    public void setRelationNodes(List<Node> relationNodes) {
        this.relationNodes = relationNodes;
    }
//    private Map<Node,Long> child=new HashMap<>();
//    public Node(String name){
//        this.name=name;
//    }
//    public String getName() {
//        return name;
//    }
//    public void setName(String name) {
//        this.name = name;
//    }
//    public Map<Node, Long> getChild() {
//        return child;
//    }
//    public void setChild(Map<Node, Long> child) {
//        this.child = child;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Node node = (Node) o;
//        return Objects.equals(name, node.name) &&
//                Objects.equals(child, node.child);
//    }
//
//    @Override
//    public int hashCode() {
//
//        return Objects.hash(name, child);
//    }
}
