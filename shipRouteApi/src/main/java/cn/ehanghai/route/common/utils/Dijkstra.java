/**
 * 
 */
package cn.ehanghai.route.common.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
* @author 胡恒博 E-mail:huhb@grassinfo.cn
* @version 创建时间：2017年7月3日 下午3:46:32
* 类说明 Dijkstra(迪杰斯特拉)算法求最短路径
*/
public class Dijkstra {  
	
    Set<Node> open=new HashSet<Node>();
    Set<Node> close=new HashSet<Node>();
    Node end = null;
    Map<String,Long> path=new HashMap<>();//封装路径距离  
    Map<String,String> pathInfo=new HashMap<>();//封装路径信息  
    
    
    /**
     * 初始化迪杰斯特拉算法
	 * @param open 所有节点
	 * @param start 开始节点
	 * @param open 顶点列表
	 */
	public Dijkstra(Node start, Node end, Set<Node> open) {
//		this.end = end;
//		open.remove(start);
//		this.open = open;
//    	close.add(start);
//    	for (Node node : this.open) {
//    		/**
//    		 * 初始化点距离，与初始点不连接的，默认最大值
//    		 */
//    		path.put(node.getName(), Long.MAX_VALUE);
//    		Map<Node,Long> childs=start.getChild();
//            for(Node child:childs.keySet()){
//            	if (child.getName().equals(node.getName())) {
//					path.put(node.getName(), childs.get(child));
//					break;
//				}
//            }
//		}
//    	for (Node node : this.open) {
//    		pathInfo.put(node.getName(), start.getName()+"->"+node.getName());
//		}
	}
    
    /**
     * 
     * @param start 开始节点
     * @return 返回路径字符串：34->38->39->40->46->50->77->78->76->75->74
     */
    public String computePath(Node start){
//    	if (start==null) {
//			return null;
//		}
//        Node nearest=getShortestPath(start);//取距离start节点最近的子节点,放入close
//        if(nearest==null){
//            return pathInfo.toString();
//        }
//        close.add(nearest);
//        open.remove(nearest);
//        Map<Node,Long> childs=nearest.getChild();
//        for(Node child:childs.keySet()){
//            if(open.contains(child)){//如果子节点在open中
//            	Long newCompute=path.get(nearest.getName())+childs.get(child);
//                if(path.get(child.getName())>newCompute){//之前设置的距离大于新计算出来的距离
//                	 path.put(child.getName(), newCompute);
//                     pathInfo.put(child.getName(), pathInfo.get(nearest.getName())+"->"+child.getName());
//                }
//            }
//        }
//        computePath(start);//重复执行自己,确保所有子节点被遍历
//        computePath(nearest);//向外一层层递归,直至所有顶点被遍历
//        return pathInfo.get(end.getName()).toString();


        return null;
    }  
    
    public void printPathInfo(){  
    	Set<Map.Entry<String, String>> pathInfos=pathInfo.entrySet(); 
        for(Map.Entry<String, String> pathInfo:pathInfos){ 
            System.out.println(pathInfo.getKey()+":"+pathInfo.getValue()); 
        }   
    }  
    /** 
     * 获取与node最近的子节点 
     */  
    private Node getShortestPath(Node node){
//        Node res=null;
//        Long minDis=Long.MAX_VALUE;
//        Map<Node,Long> childs=node.getChild();
//        for(Node child:childs.keySet()){
//            if(open.contains(child)){
//                Long distance=childs.get(child);
//                if(distance<minDis){
//                    minDis=distance;
//                    res=child;
//                }
//            }
//        }
//        return res;

        return null;
    }  
}  