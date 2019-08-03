package cn.ehanghai.route.nav.action;

import ch.hsr.geohash.GeoHash;
import cn.ehanghai.route.common.constants.ResponseCode;
import cn.ehanghai.route.common.utils.Epoint;
import cn.ehanghai.route.common.utils.Node;
import cn.ehanghai.route.common.utils.ResponseBean;
import cn.ehanghai.route.common.utils.WebService;
import cn.ehanghai.route.nav.domain.*;
import cn.ehanghai.route.nav.service.LinePointService;
import cn.ehanghai.route.nav.service.NavBaseLineService;
import cn.ehanghai.route.nav.service.RouteAreaService;
import cn.ehanghai.route.nav.service.RouteLineCheckService;
//import cn.ehanghai.routecalc.common.utils.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/nav/routeline")
public class NavRouteLineAction {

    static long lineId=1;

    @Autowired
    private LinePointService linePointService;

    @Autowired
    private NavBaseLineService baseLineService;


    @Autowired
    private RouteLineCheckService routeLineCheckService;

    @Autowired
    private RouteAreaService routeAreaService;

    private  Calendar checkRouteLastTime;

    private  double NoData=-9999;


    /**
     * 计算两个港口之间的航线--添加交点
     * */
    @ResponseBody
    @RequestMapping(value="calcrouteline/{startCode}/{endCode}/{shipHeight}/{depth}/{shipton}")
    public ResponseBean calcRouteLine(HttpServletRequest request,
                                      @PathVariable("startCode") String startCode,
                                      @PathVariable("endCode") String endCode,
                                      @PathVariable("shipHeight") double shipHeight,
                                      @PathVariable("depth") double depth,
                                      @PathVariable("shipton") double shipton)
    {

        return calcRouteLine( request,startCode,endCode,shipHeight,depth,shipton,"");
    }


    @ResponseBody
    @RequestMapping(value="calcrouteline/{startCode}/{endCode}/{shipHeight}/{depth}/{shipton}/{laneName}")
    public ResponseBean calcRouteLine(HttpServletRequest request,
                                      @PathVariable("startCode") String startCode,
                                      @PathVariable("endCode") String endCode,
                                      @PathVariable("shipHeight") double shipHeight,
                                      @PathVariable("depth") double depth,
                                      @PathVariable("shipton") double shipton,
                                      @PathVariable("laneName") String laneName)
    {

        List<RouteLineCheck>lines=    WebService.calcRoute(startCode,endCode,shipHeight,depth,shipton,laneName);

        if(lines==null||lines.size()<=0)
        {
            JSONObject result = new JSONObject();
            result.put("lineinfos", new ArrayList<>());
            String message=startCode+"至"+endCode+"没有通路";
            return new ResponseBean(ResponseCode.SUCCESS.getCode(),message, result);
        }

        JSONObject result = new JSONObject();
        result.put("lineinfos", lines);
        return new ResponseBean(ResponseCode.SUCCESS, result);
    }


    /**
     * 构图重建
     * yulj:重新初始化算法数据，即重新读取转向点、航线等数据
     * */
    @ResponseBody
    @RequestMapping({"calcrouteline/reset"})
    public ResponseBean calcReset()
    {
        String message="操作成功";
        String content= WebService.calcReset();
        if(content.equals("SUCCESS"))
        {
            message="操作失败";
        }
        JSONObject result=new JSONObject();
        result.put("Success",true);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),message, result);
    }

//    /**
//     * 重新计算所有交点
//     * */
//    @ResponseBody
//    @RequestMapping({"calcrouteline/insersectPoints"})
//    public ResponseBean calcInsersectPoints()
//    {
//        String message="正在计算交点，大概需要1个小时。";
//        WebService.calcInsersectPoints();
//        JSONObject result=new JSONObject();
//        result.put("Success",true);
//        return new ResponseBean(ResponseCode.SUCCESS.getCode(),message, result);
//    }


    /**
     * 获取未经人工检查的航线
     * */
    @ResponseBody
    @RequestMapping({"getUnCheckLines"})
    public ResponseBean getUnCheckLines(HttpServletRequest request)
    {
        List<RouteLineCheck> routeLineChecks=   routeLineCheckService.getUnCheckLines();
        Collections.sort(routeLineChecks,(o1,o2)-> (int)(o2.getId()-o1.getId()));
        JSONObject result = new JSONObject();
        result.put("unchecklines", JSONArray.parseArray(JSONObject.toJSONString(routeLineChecks)));

        return new ResponseBean(ResponseCode.SUCCESS, result);
    }

    /**
     * 获取未经人工检查的航线
     * yulj:感觉效率较低，应该可以一次性用sql直接获得结果
     * */
    @ResponseBody
    @RequestMapping({"getUnCheckLines/{areaCode}"})
    public ResponseBean getUnCheckLines(HttpServletRequest request,@PathVariable("areaCode") String areaCode)
    {
        List<RouteLineCheck> routeLineChecks=   routeLineCheckService.getUnCheckLines();

        List<LinePoint> linePoints=  linePointService.getAreaPoints(areaCode);
        List<String> codes=  linePoints.stream().map(a->a.getCode()).collect(Collectors.toList());
        routeLineChecks= routeLineChecks.stream().filter(a->{
            String []items=a.getLineId().split("_");
            if(codes.contains(items[0])) return  true;
            if(codes.contains(items[1])) return  true;

            return  false;
        }).collect(Collectors.toList());


        JSONObject result = new JSONObject();
        result.put("unchecklines", JSONArray.parseArray(JSONObject.toJSONString(routeLineChecks)));

        return new ResponseBean(ResponseCode.SUCCESS, result);
    }

    /*
    *批量删除  linesNum 删除个数
    * yulj: 所谓删除，其实是将未检查航线设为已检查，为什么不把所有未检查航线设为已检查呢？
    * **/
    @ResponseBody
    @RequestMapping({"batchDelCheckLines/{linesNum}"})
    public ResponseBean batchDelCheckLines(HttpServletRequest request,@PathVariable("linesNum")Integer linesNum)
    {
        List<RouteLineCheck> routeLineChecks=   routeLineCheckService.getUnCheckLines();
        int num=200;
        if(linesNum!=null)
        {
            num=linesNum;
        }
        for(int i=0;i<num&&i<routeLineChecks.size();++i)
        {
            routeLineCheckService.Delete(routeLineChecks.get(i).getLineId());
        }

        JSONObject result=new JSONObject();
        result.put("Success",true);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),"删除成功", result);
    }

    /**
     * 删除已经人工检查过的航线
     * */
    @ResponseBody
    @RequestMapping(value="deletecheckline/{lineId:.+}")
    public ResponseBean DeleteCheckLine(HttpServletRequest request,@PathVariable("lineId")String lineId )
    {
        routeLineCheckService.Delete(lineId);
        JSONObject result=new JSONObject();
        result.put("Success",true);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),"删除成功", result);
    }

    /**
     * 检查所有港口航线是否存在绕行
     * yulj: 好像是生成所有港口之间新的航线吧
     * */
    @ResponseBody
    @RequestMapping({"checkRouteLines"})
    public  ResponseBean checkRouteLines(HttpServletRequest request)
    {
        boolean timeFlag=false;
        if(checkRouteLastTime==null)
        {
            timeFlag=true;
        }
        else
        {
            Calendar nowTime=Calendar.getInstance();
            nowTime.add(Calendar.MINUTE,-5);
            if(checkRouteLastTime.getTimeInMillis()<nowTime.getTimeInMillis())
            {//距上次计算 大于5分钟以内 才能计算
                timeFlag=true;
            }
        }

        String msg="";
        if( WebService.checkRouteOver==true)
        {
            if(timeFlag)
            {
                msg="正在计算港口航线";
                WebService.checkRouteAsync();
                checkRouteLastTime=Calendar.getInstance();
            }
            else
            {
                msg="计算完毕";
            }
        }

        JSONObject result=new JSONObject();
        result.put("Success",true);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),msg, result);
    }

    @ResponseBody
    @RequestMapping({"checkRouteLines/{areaCode}/{maxNum}"})
    public  ResponseBean checkRouteLines(HttpServletRequest request,@PathVariable("areaCode") String areaCode,@PathVariable("maxNum")Integer maxNum)
    {
        String message=  WebService.checkRouteLines(areaCode,maxNum);
        JSONObject result=new JSONObject();
        result.put("Success",true);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),message, result);
    }

    @ResponseBody
    @RequestMapping({"checkRouteLines/{maxNum}"})
    public  ResponseBean checkRouteLines(HttpServletRequest request,@PathVariable("maxNum")Integer maxNum)
    {
        String message=  WebService.checkRouteLines("",maxNum);
        JSONObject result=new JSONObject();
        result.put("Success",true);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),message, result);
    }

    /**
     * yulj:找出已有checked且吃水>10的航线，快速搜索（舟山区域？）与此起始相同的可行航线
     * */
    @ResponseBody
    @RequestMapping({"depthNodes/{maxNum}/{shipTon}"})
    public  ResponseBean calcDepthLineList(HttpServletRequest request,@PathVariable("maxNum")Integer maxNum,@PathVariable("shipTon")Double shipTon)
    {
        String message=  WebService.calcDepthLineList(maxNum,shipTon);
        JSONObject result=new JSONObject();
        result.put("Success",true);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),message, result);
    }

    /**
     * yulj:清空航线（不是航线段）
     * */
    @ResponseBody
    @RequestMapping({"checkRouteLines/restart"})
    public  ResponseBean checkRouteLinesRestart(HttpServletRequest request)
    {
        String message=  WebService.checkRouteLinesRestart();
        JSONObject result=new JSONObject();
        result.put("Success",true);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),message, result);
    }

    /**
     * yulj:似乎是检查所有港口航线是否存在绕行，与前面checkRouteLines一样？
     * */
    @ResponseBody
    @RequestMapping({"checkHarourRouteLines"})
    public  ResponseBean checkHarourRouteLines(HttpServletRequest request)
    {
        String msg=WebService.checkRouteSync();

        JSONObject result=new JSONObject();
        result.put("Success",true);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),msg, result);
    }


    /**
     * 检查所有港口之间是否存在通路
     * */
    @ResponseBody
    @RequestMapping({"checkHarours"})
    public  String checkHarours() {

        return WebService.checkHarours(NoData,NoData,NoData);
    }

    /**
     * 检查所有港口之间是否存在通路
     * */
    @ResponseBody
    @RequestMapping({"checkHarours/{areaCode}"})
    public  String checkHarours(@PathVariable("areaCode") String areaCode) {

        return WebService.checkHarours(areaCode);
    }


    /**
     * 检查港口之间 是否存在通路
     * */
    @ResponseBody
    @RequestMapping({"checkHarours/{shipHeight}/{depth}/{shipton}"})
    public  String checkHarours(@PathVariable("shipHeight") double shipHeight, @PathVariable("depth") double depth, @PathVariable("shipton") double shipton) {

        return WebService.checkHarours(shipHeight,depth,shipton);
    }


/*
////////////////////////////////////////////任意点航线规划//////////////////////////////////////////////////////////////////
    //yqh：先获取当前点的经度纬度和搜索半径
    @ResponseBody
    @RequestMapping({"calcRouteLineByPoint/{lon}/{lat}/{raduis}/{endCode}"})
    public ResponseBean calcRouteLineByPoint(HttpServletRequest request,@PathVariable("lon")Double lon,@PathVariable("lat")Double lat,@PathVariable("raduis")Double raduis,@PathVariable("endCode")String endCode)
    {
        return getRouteLineByPoint(  lon, lat, raduis, endCode);
    }

    public ResponseBean getRouteLineByPoint(double lon,double lat,double raduis,String endCode)
    {
        raduis=raduis*1852; //yulj:海里转化为米
        LinePoint endPoint=    linePointService.getPointByCode(endCode);
        GeoHash geoHash = GeoHash.withCharacterPrecision(lat, lon, 12);
        String text= geoHash.toBinaryString();
        String geohash = text.substring(0,10);
        List<LinePoint> linePoints=  linePointService.getNearPoints(geohash);//yqh:当前任意点周围所有的转向点
        List<LinePoint> filterPoints=new ArrayList<>(); //yqh:当前任意点周围规定半径内的转向点
        Epoint start=new Epoint(lon,lat);
        start=  Epoint.LonLatToXY(start);
        for(LinePoint point:linePoints)
        {
            Epoint epoint=new Epoint(point.getLon(),point.getLat());
            epoint=  Epoint.LonLatToXY(epoint);
            double distance=Epoint.Distance(start,epoint);
            if(distance<=raduis)
            {
                filterPoints.add(point);
            }
        }


        List<RouteLineCheck>alllines=new ArrayList<>();
        for(LinePoint point:filterPoints)
        {
            List<RouteLineCheck>lines=    WebService.calcRoute(point.getCode(),endCode,NoData,NoData,NoData,"");
            if(lines!=null)
                alllines.addAll(lines);

        }

        if(alllines==null||alllines.size()<=0)
        {
            String message="没有航线到达"+endPoint.getName();
            return new ResponseBean(ResponseCode.SUCCESS.getCode(),message, null);
        }

        JSONObject result = new JSONObject();
        result.put("lineinfos", JSONArray.parseArray(JSONObject.toJSONString(alllines)));

        return new ResponseBean(ResponseCode.SUCCESS, result);



    }*/

    //Try
    //yqh：先获取当前点的经度纬度和搜索半径
    @ResponseBody
    @RequestMapping({"calcRouteLineByPoint/{lon}/{lat}/{raduis}/{endCode}"})
    public ResponseBean calcRouteLineByPoint(HttpServletRequest request,@PathVariable("lon")Double lon,@PathVariable("lat")Double lat,@PathVariable("raduis")Double raduis,@PathVariable("endCode")String endCode)
    {
        return getRouteLineByPoint(  lon, lat, raduis, endCode);
    }

    public ResponseBean getRouteLineByPoint(double lon,double lat,double raduis,String endCode)
    {
        //raduis = 1.0;
        //raduis=raduis*1852; //yulj:海里转化为米
        LinePoint endPoint=    linePointService.getPointByCode(endCode);
        GeoHash geoHash = GeoHash.withCharacterPrecision(lat, lon, 12);
        String text= geoHash.toBinaryString();
        String geohash = text.substring(0,10);
        List<LinePoint> linePoints=  linePointService.getNearPoints(geohash);//yqh:当前任意点周围所有的转向点
        List<LinePoint> filterPoints=new ArrayList<>(); //yqh:当前任意点周围规定半径内的转向点
        Epoint start=new Epoint(lon,lat);
        start=  Epoint.LonLatToXY(start);
        //yqh：按0.2海里步长递增搜索
        for (int meterStep = 1; meterStep * 0.2 * 1852 < 1000 * 0.2 * 1852; meterStep++){
            for(LinePoint point:linePoints)
            {
                Epoint epoint=new Epoint(point.getLon(),point.getLat());
                epoint=  Epoint.LonLatToXY(epoint);
                double distance=Epoint.Distance(start,epoint);
                if(distance<=meterStep*0.2*1852)
                {
                    filterPoints.add(point);
                }
            }
            //yqh:取至少两个邻近转向点
            if(filterPoints.size()<2){
                filterPoints.clear();
            }
            if (filterPoints.size() > 1){
                    break;
            }
        }
        for(int i = 0; i < filterPoints.size(); i++) {
            System.out.println("point: " + (i+1));
            System.out.println("pointName: " + filterPoints.get(i).getName());
            System.out.println("pointCode: " + filterPoints.get(i).getCode());
            System.out.println("pointLongitude: " + filterPoints.get(i).getLon());
            System.out.println("pointLatitude: " + filterPoints.get(i).getLat());
        }
        /*
        if(filterPoints == null|| filterPoints.size()<=0){
            String message = "请扩大搜索半径！";
            return new ResponseBean(ResponseCode.SUCCESS.getCode(),message,null);
        }
         */

        List<RouteLineCheck>alllines=new ArrayList<>();
        for(LinePoint point:filterPoints)
        {
            List<RouteLineCheck>lines=    WebService.calcRoute(point.getCode(),endCode,NoData,NoData,NoData,"");
            if(lines!=null)
                alllines.addAll(lines);

        }

        if(alllines==null||alllines.size()<=0)
        {
            String message="没有航线到达"+endPoint.getName();
            return new ResponseBean(ResponseCode.SUCCESS.getCode(),message, null);
        }
        //yqh:计算航线的距离
        HashMap<RouteLineCheck,Double> map = new HashMap<>();

        for (RouteLineCheck line : alllines) {

            String path = line.getPath();
            Double distance = WebService.calcDistance(path);
            map.put(line, distance);

        }
        //根据距离排序航线再输出
        for (Map.Entry<RouteLineCheck, Double> entry: map.entrySet()){
            System.out.println("LineID: " + entry.getKey().getLineId() + "  Distance: " + entry.getValue());
        }
        List<Map.Entry<RouteLineCheck, Double>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<RouteLineCheck, Double>>() {
            @Override
            public int compare(Map.Entry<RouteLineCheck, Double> o1, Map.Entry<RouteLineCheck, Double> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        System.out.println("-------------After Sorting------------");
        List<RouteLineCheck> alllinesSorted = new ArrayList<>();
        for (Map.Entry<RouteLineCheck, Double> entry: list){
            System.out.println("LineID: " + entry.getKey().getLineId() + "  Distance: " + entry.getValue());
            alllinesSorted.add(entry.getKey());
        }

        JSONObject result = new JSONObject();
        //result.put("lineinfos", JSONArray.parseArray(JSONObject.toJSONString(alllines)));
        result.put("lineinfos", JSONArray.parseArray(JSONObject.toJSONString(alllinesSorted)));

        return new ResponseBean(ResponseCode.SUCCESS, result);
        //yqh：应该加入一个功能：在web页面上点选想要的航线后，将该航线突出表示，其余航线颜色恢复默认
        //避免航线颜色着色的覆盖问题



    }





}
