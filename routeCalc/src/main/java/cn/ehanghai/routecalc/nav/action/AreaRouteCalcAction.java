package cn.ehanghai.routecalc.nav.action;

import cn.ehanghai.routecalc.common.math.Epoint;
import cn.ehanghai.routecalc.common.utils.Node;
import cn.ehanghai.routecalc.nav.domain.*;
import cn.ehanghai.routecalc.nav.service.*;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping({"/nav/area/route"})
public class AreaRouteCalcAction {

    @Autowired
    private LinePointService linePointService;

    @Autowired
    private NavBaseLineService baseLineService;

    @Autowired
    private HarbourService harbourService;

    @Autowired
    private RouteAreaService routeAreaService;

    private  static  RouteLineCalcFast routeLineCalcFast;

    private  static  List<Harbour> harbours;
    @Autowired
    private RouteLineCheckService routeLineCheckService;

    private  double NoData=-9999;


    /**
     * 计算两个港口之间的航线
     * 只使用现有的转向点
     * 只返回最短航线
     * */
    @ResponseBody
    @RequestMapping({"calc/{areaCode}/{startCode}/{endCode}/{shipHeight}/{depth}/{shipton}"})
    public String calcRoute(@PathVariable("areaCode") String areaCode,
                            @PathVariable("startCode") String startCode,
                            @PathVariable("endCode") String endCode,
                            @PathVariable("shipHeight") double shipHeight,
                            @PathVariable("depth") double depth,
                            @PathVariable("shipton") double shipton)
    {
       return  calcRouteByArea( areaCode,  startCode, endCode, shipHeight, depth, shipton, "");
    }


    //添加定线制限制
    @ResponseBody
    @RequestMapping({"calc/{areaCode}/{startCode}/{endCode}/{shipHeight}/{depth}/{shipton}/{laneName}"})
    public String calcRoute(@PathVariable("areaCode") String areaCode,
                            @PathVariable("startCode") String startCode,
                            @PathVariable("endCode") String endCode,
                            @PathVariable("shipHeight") double shipHeight,
                            @PathVariable("depth") double depth,
                            @PathVariable("shipton") double shipton,
                            @PathVariable("laneName") String laneName)
    {
        return  calcRouteByArea( areaCode,  startCode, endCode, shipHeight, depth, shipton, laneName);
    }



    private  String calcRouteByArea(String areaCode, String startCode,String endCode,double shipHeight,double depth,double shipton,String laneName)
    {
        if(shipHeight==99999.0) shipHeight=-9999;
        if(depth==99999.0) depth=-9999;
        if(shipton==99999.0) shipton=-9999;

        Init( areaCode);

        List<RouteLine> routeLines= routeLineCalcFast.getRouteLines(startCode,endCode,shipHeight,depth,shipton,laneName);

        if (routeLines.size() > 0) {


            RouteLine minLenLine=routeLines.get(0);
            List<Double> lens=new ArrayList<>();

            for(int i=0;i<routeLines.size();++i)
            {
                lens.add(getPathLen(routeLines.get(i).getNodes()));
            }
            if(lens.size()>1)
            {
                Double minlen=  lens.stream().mapToDouble(a->a).min().getAsDouble();
                int index=lens.indexOf(minlen);
                minLenLine=routeLines.get(index);
            }

            RouteLineCheck routeLineCheck=   minLenLine.toCheckLine(false);

            String json= JSON.toJSONString(routeLineCheck);

            return  json;
        }

        return "";
    }

    /**
     * 根据区域code获取港口
     * */

    @ResponseBody
    @RequestMapping({"harbour/{areaCode}"})
    public  String getHarboursByAreaCode(@PathVariable("areaCode") String areaCode)
    {
        Init(areaCode);

        if(harbours==null)  return "";

          String json=JSON.toJSONString(harbours);
          return  json;

//        List<RouteArea> routeAreas=  routeAreaService.getAllData();
//        RouteArea routeArea=routeAreas.get(0);
//
//        List<LinePoint> allpoints = linePointService.getAllData( );
//        allpoints =allpoints.stream().filter(a->a.getLon()<routeArea.getLonMin()||a.getLon()>routeArea.getLonMax()
//        ||a.getLat()<routeArea.getLatMin()||a.getLat()>routeArea.getLatMax()).collect(Collectors.toList());
//
//
//        List<String> pointCodes= allpoints.stream().map(a->a.getCode()).collect(Collectors.toList());
//        List<BaseLine> alllines = this.baseLineService.getAllData();
//        alllines =  alllines.stream().filter(a->pointCodes.contains(a.getStartCode())&&pointCodes.contains(a.getEndCode())).collect(Collectors.toList());
//        harbours =harbourService.getAllData();
//        harbours=  harbours.stream().filter(a->pointCodes.contains(a.getLinePointCode())).collect(Collectors.toList());
//        String json="";//JSON.toJSONString(harbours);
//
//       List<String> items= harbours.stream().map(a->a.getId()+","+a.getLinePointCode()+","+a.getNameLat()+","+a.getNameLon()+","+a.getName()+","+a.getValid()).collect(Collectors.toList());
//
//       json=String.join("\r\n",items);
//
//        return  json;

    }

    /**
     * 获取所有的区域code 信息
     * */
    @ResponseBody
    @RequestMapping({"areaCode"})
    public String getAreaCodes()
    {
      List<RouteArea> routeAreas=  routeAreaService.getAllData();

      if(routeAreas==null)  return "";
      String json=JSON.toJSONString(routeAreas);
      return  json;


    }


    @ResponseBody
    @RequestMapping({"checkHarours/{areaCode}"})
    public  String checkHarours(@PathVariable("areaCode") String areaCode) {
        reset(areaCode);

        List<RouteLine> routeLines=new ArrayList<>();

        StringBuffer buffer=new StringBuffer();

        for(int i=0;i<harbours.size();++i)
        {
            for(int j=0;j<harbours.size();++j)
            {
                if(i!=j)
                {
                    String startCode=harbours.get(i).getLinePointCode();
                    String endCode=harbours.get(j).getLinePointCode();
                    List<RouteLine>  tmprouteLines= routeLineCalcFast.getRouteLines(startCode,endCode,NoData,NoData,NoData,"");

                    if(tmprouteLines.size()<=0)
                    {
                        String msg=harbours.get(i).getName()+" 至 "+harbours.get(j).getName()+" 编号："+startCode+" - "+endCode+" 不存在通路。";
                        //buffer.append(msg+"\r\n");
                        buffer.append(msg+"</br>");
                    }
                    else
                    {
                        routeLines.addAll(tmprouteLines);

                    }
                }

            }
        }


        List<RouteLineCheck> result=  routeLines.stream().map(a->a.toCheckLine(true)).collect(Collectors.toList());

        routeLineCheckService.clean();
        routeLineCheckService.insertList(result);



        String content= buffer.toString();
        if(content.equals(""))
        {
            content="所有港口都有通路。";
        }

        return content;
    }

    public  String reset( String areaCode)
    {
        List<LinePoint> allpoints = linePointService.getAreaPoints( areaCode);
        List<String> pointCodes= allpoints.stream().map(a->a.getCode()).collect(Collectors.toList());
        List<BaseLine> alllines = this.baseLineService.getAllData();
        alllines =  alllines.stream().filter(a->pointCodes.contains(a.getStartCode())&&pointCodes.contains(a.getEndCode())).collect(Collectors.toList());

        harbours =harbourService.getAllData();
        harbours=  harbours.stream().filter(a->pointCodes.contains(a.getLinePointCode())).collect(Collectors.toList());

        routeLineCalcFast =new RouteLineCalcFast(allpoints,alllines,0,true);

        return "SUCCESS";
    }

    private  void Init(String areaCode)
    {
        if(routeLineCalcFast==null||harbours==null)
        {
            reset( areaCode);
        }
    }



    private double getPathLen(List<Node> nodes)
    {

        List<Epoint> epoints=   nodes.stream().map(a->new Epoint(a.getPoint().getX(),a.getPoint().getY())).collect(Collectors.toList());
        epoints=    epoints.stream().map(a->Epoint.LonLatToXY(a)).collect(Collectors.toList());
        double distance=0;
        for(int i=0;i<epoints.size()-1;++i)
        {
            distance+=   Epoint.Distance(epoints.get(i),epoints.get(i+1));
        }
        return  distance;
    }


}
