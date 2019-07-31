package cn.ehanghai.routecalc.nav.action;

import ch.hsr.geohash.GeoHash;
import cn.ehanghai.routecalc.common.math.Epoint;
import cn.ehanghai.routecalc.common.utils.Graph;
import cn.ehanghai.routecalc.common.utils.HanyuPinyinHelper;
import cn.ehanghai.routecalc.common.utils.Node;
import cn.ehanghai.routecalc.nav.domain.*;
import cn.ehanghai.routecalc.nav.service.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import net.ryian.commons.StringUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/nav/routecalc"})
public class RouteCalcAction
{
//  private  static List<Node> allNodes;

//  private  static long time = 0L;

  private  double NoData=-9999;

  @Autowired
  private LinePointService linePointService;

  @Autowired
  private NavBaseLineService baseLineService;

  @Autowired
  private HarbourService harbourService;

  @Autowired
  private RouteLineCheckService routeLineCheckService;


  @Autowired
  private LinePointAllService linePointAllService;

  @Autowired
  private BaseLineAllService baseLineAllService;

  private  static  RouteLineCalcFast routeLineCalcFast;
  private  static  RouteLineCalcFast routeLineAllCalcFast;//含交点（虚拟转向点）


  //yqh:收到webservice中的calRoute中sentGet(url)指令匹配，针对三种参数的request的三种响应方法
  //方法一：只有起始点和终点
  @ResponseBody
  @RequestMapping({"calc/{startCode}/{endCode}"})
  public String calcRoute(@PathVariable("startCode") String startCode, @PathVariable("endCode") String endCode)
  {
    return calcRouteLines( startCode,  endCode, NoData,  NoData,  NoData,"");
  }

//方法二：五个参数：有起点、终点、船高、水深、吨位
  /**
   * 计算两个港口之间的航线
   * 使用两种数据源
   * 1、使用现有的转向点
   * 2、使用航线的交点做为转向点
   * */
  @ResponseBody
  @RequestMapping({"calc/{startCode}/{endCode}/{shipHeight}/{depth}/{shipton}"})
  public String calcRoute(@PathVariable("startCode") String startCode,
                          @PathVariable("endCode") String endCode,
                          @PathVariable("shipHeight") double shipHeight,
                          @PathVariable("depth") double depth,
                          @PathVariable("shipton") double shipton)
  {
    return calcRouteLines(   startCode,  endCode, shipHeight,   depth,   shipton,"");
  }

  //方法三：五个参数+添加定线制限制

  @ResponseBody
  @RequestMapping({"calc/{startCode}/{endCode}/{shipHeight}/{depth}/{shipton}/{laneNames}"})
  public String calcRoute(@PathVariable("startCode") String startCode,
                          @PathVariable("endCode") String endCode,
                          @PathVariable("shipHeight") double shipHeight,
                          @PathVariable("depth") double depth,
                          @PathVariable("shipton") double shipton,
                          @PathVariable("laneNames")  String laneNames)
  {
    return calcRouteLines(   startCode,  endCode, shipHeight,   depth,   shipton,laneNames);
  }


 /**
  * 构图重建
  * */
  @ResponseBody
  @RequestMapping({"calc/reset"})
  public String calcReset()
  {
     init();

    return "SUCCESS";
  }

  //yqh:计算航线的方法
  public String calcRouteLines( String startCode, String endCode,double shipHeight,  double depth,  double shipton,String laneNames)
  {

//yulj:这里为什么去掉这个判断？这样每次计算航线都要初始化一遍
//    if(routeLineCalcFast==null||routeLineAllCalcFast==null)
    {
      init();
    }

    List<RouteLine> routeLines= routeLineCalcFast.getRouteLines(startCode,endCode,shipHeight,depth,shipton,laneNames);

    List<RouteLine> tmpdatas= routeLineAllCalcFast.getRouteLines(startCode,endCode,shipHeight,depth,shipton,laneNames);

    //yulj:usecode为true，表示不精简三点构成近似直线的中间节点
    List<RouteLineCheck> routeLineChecks=  routeLines.stream().map(a->a.toCheckLine(true)).collect(Collectors.toList());

    List<RouteLineCheck> temproutes=  tmpdatas.stream().map(a->a.toCheckLine(true)).collect(Collectors.toList());

    for(int i=0;i<temproutes.size();++i)
    {
      RouteLineCheck routeLineCheck=temproutes.get(i);
      Optional<RouteLineCheck> find= routeLineChecks.stream().filter(a->a.getPath().equals(routeLineCheck.getPath())).findFirst();
      if(!find.isPresent())
      {
        routeLines.add(tmpdatas.get(i));
      }
    }


    List<String> colors=new ArrayList<>();
    colors.add("0,0,255");
    colors.add("255,185,15");
    colors.add("255,0,255");
    colors.add("178,34,34");
    colors.add("178,58,238");
    colors.add("56,56,56");
    colors.add("33,136,140");

    if (routeLines.size() > 0)
    {
      //yulj:usecode为false，精简三点接近直线的中间点
      List<RouteLineCheck> result=  routeLines.stream().map(a->a.toCheckLine(false)).collect(Collectors.toList());

      //yulj:这里七种颜色，result难道刚刚好为7条吗？
      //yqh：取结果数量和颜色数量中的最小值，所以显示的路线<7
      for(int i=0;i<colors.size()&&i<result.size();++i)
      {
//        System.out.println(result.get(i).getPath());

        result.get(i).setColor(colors.get(i));
      }

      String json = JSONObject.toJSONString(result);
      return json;
    }

    return "";
  }

  private  void init()
  {

    List<LinePoint> allpoints;//转向点
    List<BaseLine> alllines= this.baseLineService.getAllData(); //航线

    List<LinePointAll> linePointAlls;   //包含虚拟转向点的所有转向点？
    List<BaseLineAll> baseLineAlls=this.baseLineAllService.getAllData();//包含虚拟航线的所有航线

    allpoints = this.linePointService.getAllData();
    linePointAlls=this.linePointAllService.getAllData();


    routeLineCalcFast =new RouteLineCalcFast(allpoints,alllines,0,true);

    //yulj:转换成RouteLineCalcFast需要的参数类型，其实都是一样的，可以考虑将RouteLineCalcFast写成泛型
    allpoints = linePointAlls.stream().map(a->a.toLinePoint()).collect(Collectors.toList());
    alllines =   baseLineAlls.stream().map(a->a.toBaseLine()).collect(Collectors.toList());
    routeLineAllCalcFast =new RouteLineCalcFast(allpoints,alllines,1,true);

    routeLineCalcFast.setExtend(true);
    routeLineAllCalcFast.setExtend(true);
  }

  /**
   * 计算所有港口的航线--
   * 用于航线检测是否存在绕行
   * 计算结果 写入数据库
   *
   * 只使用现有的转向点做为数据源
   * */
  @ResponseBody
  @RequestMapping({"checkRoute"})
  public  String checkRoute()
  {

    List<LinePoint> allpoints = this.linePointService.getAllData();
    List<BaseLine> alllines = this.baseLineService.getAllData();
    List<RouteLineCheck> routeLineChecks= this.routeLineCheckService.getAllData();
    RouteLineCalc routeLineCalc =new RouteLineCalc(allpoints,alllines,0,false);

    List<RouteLine> routeLines=new ArrayList<>();

    List<Harbour>  harbours= harbourService.getAllData();

    for(int i=0;i<harbours.size();++i)
    {
      for(int j=0;j<harbours.size();++j)
      {
        if(i!=j)
        {
          String startCode=harbours.get(i).getLinePointCode();
          String endCode=harbours.get(j).getLinePointCode();

          List<RouteLine> tmpdatas= routeLineCalc.getRouteLines(startCode,endCode,NoData,NoData,NoData);

          if(tmpdatas.size()>0)
          {
            routeLines.addAll(tmpdatas);
          }
        }
      }
    }

//    CheckUnknowDepth(routeLines, alllines);

    List<RouteLineCheck> result=  routeLines.stream().map(a->a.toCheckLine(true)).collect(Collectors.toList());

    List<RouteLineCheck> newRouteLines=new ArrayList<>();

    for(RouteLineCheck line: result)
    {
      Optional<RouteLineCheck> lineop= routeLineChecks.stream().filter(a->a.getLineId()==line.getLineId()).findFirst();
      if(lineop.isPresent())
      {
        if( !lineop.get().getPath().equals(line.getPath()))
        {
          this.routeLineCheckService.deleteData(line.getLineId());
          line.setLineCheck(false);
          newRouteLines.add(line);
        }
      }
      else
      {
        newRouteLines.add(line);
      }
    }

    this.routeLineCheckService.insertList(newRouteLines);

    return "SUCCESS";
  }

  @ResponseBody
  @RequestMapping({"clean"})
  public  String  clean()
  {
    List<RouteLineCheck>  routeLineChecks=  this.routeLineCheckService.getAllData();

    HashMap<String,RouteLineCheck> routeLineCheckHashMap=new HashMap<>();

    for(RouteLineCheck data:routeLineChecks)
    {
      if(!routeLineCheckHashMap.containsKey(data.getLineId()))
      {
        routeLineCheckHashMap.put(data.getLineId(),data);
      }
    }


    List<RouteLineCheck> datas=new ArrayList<>();
    for(String key:routeLineCheckHashMap.keySet())
    {
      datas.add(routeLineCheckHashMap.get(key));
    }

    this.routeLineCheckService.clean();

    this.routeLineCheckService.insertList(datas);

    return "SUCCESS";
  }

  @ResponseBody
  @RequestMapping({"toNameList"})
  public  String toNameList()
  {
    List<LinePoint> allpoints = this.linePointService.getAllData();
    List<RouteLineCheck>  routeLineChecks=  this.routeLineCheckService.getAllData();

    List<RouteCatalog> routeCatalogs=new ArrayList<>();

    HanyuPinyinHelper hanyuPinyinHelper = new HanyuPinyinHelper() ;


    for(RouteLineCheck data:routeLineChecks)
    {
      String[]items= data.getLineId().split("_");

      Optional<LinePoint> firstop= allpoints.stream().filter(a->a.getCode().equals(items[0])).findFirst();
      Optional<LinePoint> secondop= allpoints.stream().filter(a->a.getCode().equals(items[1])).findFirst();

      if(firstop.isPresent()&&secondop.isPresent())
      {
        LinePoint first=firstop.get();
        LinePoint second=secondop.get();


        String startCode=first.getCode();
        String startName=first.getName();
        String startLon=toDuFenMiao(first.getLon());
        String startLat=toDuFenMiao(first.getLat());
        String  endCode= second.getCode();
        String  endName=second.getName();
        String  endLon=toDuFenMiao(second.getLon());
        String endLat=toDuFenMiao(second.getLat());
        String initials="";

        String pinyin= hanyuPinyinHelper.toHanyuPinyin(first.getName());
        if(!StringUtils.isEmpty(pinyin))
        {
          char[] chars= pinyin.toUpperCase().toCharArray();
          if(chars.length>0)
          {
            initials= chars[0]+"";
          }
        }

        int pathLen=data.getPath().length();

        RouteCatalog routeCatalog=new  RouteCatalog( startCode,  startName,  startLon,  startLat,
                endCode,  endName,  endLon,  endLat,  initials,  pathLen);

        routeCatalogs.add(routeCatalog);
      }
    }

    Map<String, List<RouteCatalog>> groups = routeCatalogs.stream().collect(Collectors.groupingBy(RouteCatalog::getInitials));

    StringBuffer stringBuffer=new StringBuffer();
    for(String key:groups.keySet())
    {
      List<RouteCatalog> group=  groups.get(key);
//      group.sort(Comparator.comparingInt(RouteCatalog::getPathLen));
      group.sort(Comparator.comparing(RouteCatalog::getStartName));
      for(RouteCatalog data:group)
      {
        stringBuffer.append(data.toString()+"\r\n");
      }
    }


    String content=stringBuffer.toString();

    try {
      FileUtils.writeStringToFile(new File("C:\\Users\\peter\\Desktop\\dataprocess\\list.csv"),content);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return "SUCCESS";
  }


  private String  toDuFenMiao(float angle)
  {
    int du=(int)(angle);
    double left=(angle-du)*60.0;
    int fen=(int)(left);
    int miao=(int)((left-fen)*60);

    return String.format("%d°%d′%d″",du,fen,miao);

  }





  @ResponseBody
  @RequestMapping({"checkRouteIP"})
  public  String checkRouteIP()
  {

    List<LinePointAll> linePointAlls = this.linePointAllService.getAllData();
    List<BaseLineAll> baseLineAlls = this.baseLineAllService.getAllData();

    List<LinePoint> allpoints = linePointAlls.stream().map(a->a.toLinePoint()).collect(Collectors.toList());
    List<BaseLine>  alllines =   baseLineAlls.stream().map(a->a.toBaseLine()).collect(Collectors.toList());

    RouteLineCalc routeLineCalc =new RouteLineCalc(allpoints,alllines,1,false);

    List<RouteLine> routeLines=new ArrayList<>();

    List<Harbour>  harbours= harbourService.getAllData();

    for(int i=0;i<harbours.size();++i)
    {
      for(int j=0;j<harbours.size();++j)
      {
        if(i!=j)
        {
          String startCode=harbours.get(i).getLinePointCode();
          String endCode=harbours.get(j).getLinePointCode();

          List<RouteLine> tmpdatas= routeLineCalc.getRouteLines(startCode,endCode,NoData,NoData,NoData);

          if(tmpdatas.size()>0)
          {
            routeLines.addAll(tmpdatas);
          }
        }

      }

    }


    List<RouteLineCheck> result=  routeLines.stream().map(a->a.toCheckLine(true)).collect(Collectors.toList());

    String json= JSON.toJSONString(result);
    try {
      FileUtils.writeStringToFile(new File("allroutes.json"),json);
    } catch (IOException e) {
      e.printStackTrace();
    }


    return "SUCCESS";
  }

  /**
   * 计算航线的全部交点
   * */
  @ResponseBody
  @RequestMapping({"insersectPoints"})
  public String InsersectPoints()
  {


    linePointAllService.clean();
    baseLineAllService.clean();



    List<LinePoint> allpoints = this.linePointService.getAllData();
    List<BaseLine> alllines = this.baseLineService.getAllData();

    BaseLineIntersect baseLineIntersect=new BaseLineIntersect();
    baseLineIntersect.setLinePointAllService(linePointAllService);
    baseLineIntersect.setBaseLineAllService(baseLineAllService);

    baseLineIntersect.calcNewPoints(alllines,allpoints);



    return "SUCCESS";
  }

  @ResponseBody
  @RequestMapping({"checkHarours"})
  public  String checkHarours() {
    return  checkHarours(NoData,   NoData,  NoData);
  }

  @ResponseBody
  @RequestMapping({"checkHarours/{shipHeight}/{depth}/{shipton}"})
  public  String checkHarours(@PathVariable("shipHeight") double shipHeight, @PathVariable("depth") double depth, @PathVariable("shipton") double shipton)
  {

    List<LinePoint> allpoints = this.linePointService.getAllData();
    List<BaseLine> alllines = this.baseLineService.getAllData();
    RouteLineCalc routeLineCalc =new RouteLineCalc(allpoints,alllines,0,false);


    StringBuffer buffer=new StringBuffer();
    List<Harbour>  harbours= harbourService.getAllData();

    for(int i=0;i<harbours.size();++i)
    {
      for(int j=0;j<harbours.size();++j)
      {
        if(i!=j)
        {
          String startCode=harbours.get(i).getLinePointCode();
          String endCode=harbours.get(j).getLinePointCode();
          List<RouteLine> routeLines= routeLineCalc.getRouteLines(startCode,endCode,shipHeight,depth,shipton);

          if(routeLines.size()<=0)
          {
            String msg=harbours.get(i).getName()+" 至 "+harbours.get(j).getName()+" 编号："+startCode+" - "+endCode+" 不存在通路。";
            //buffer.append(msg+"\r\n");
            buffer.append(msg+"</br>");
          }
        }

      }
    }


    String content= buffer.toString();
    if(content.equals(""))
    {
      content="所有港口都有通路。";
    }

    return content;


  }







}

