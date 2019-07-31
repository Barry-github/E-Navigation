package cn.ehanghai.routecalc.nav.action;

import cn.ehanghai.routecalc.common.utils.Tool;
import cn.ehanghai.routecalc.nav.domain.*;
import cn.ehanghai.routecalc.nav.service.*;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping({"/nav/routelinelistcalc"})
public class RouteLineListAction {

    @Autowired
    private LinePointService linePointService;

    @Autowired
    private NavBaseLineService baseLineService;

    @Autowired
    private HarbourService harbourService;

    @Autowired
    private RouteAreaService routeAreaService;

    private  double NoData=-9999;

    @Autowired
    private RouteLineCheckService routeLineCheckService;

    @Autowired
    private HarbourTypeService harbourTypeService;


    @ResponseBody
    @RequestMapping({"checkHarours/{areaCode}/{maxNum}"})
    public  String checkHarours(@PathVariable("areaCode") String areaCode,@PathVariable("maxNum")Integer maxNum)
    {
        return  calcLineList(  areaCode, maxNum);
    }

    @ResponseBody
    @RequestMapping({"checkHarours/{maxNum}"})
    public  String checkHarours(@PathVariable("maxNum")Integer maxNum)
    {
        return  calcLineList(  "", maxNum);
    }

    @ResponseBody
    @RequestMapping({"checkHarours/restart"})
    public  String checkHarours()
    {
        routeLineCheckService.clean();

        return  "SUCCESS";
    }


    public  String calcLineList( String areaCode,int maxNum)
    {

        List<Harbour>  harbours =harbourService.getAllData();
        List<BaseLine> alllines = this.baseLineService.getAllData();
        List<LinePoint> allpoints;
        if(!StringUtils.isEmpty(areaCode))
        {
            allpoints = linePointService.getAreaPoints( areaCode);
            List<String> pointCodes= allpoints.stream().map(a->a.getCode()).collect(Collectors.toList());
            alllines =  alllines.stream().filter(a->pointCodes.contains(a.getStartCode())&&pointCodes.contains(a.getEndCode())).collect(Collectors.toList());
            harbours=  harbours.stream().filter(a->pointCodes.contains(a.getLinePointCode())).collect(Collectors.toList());
        }
        else
        {
            allpoints = linePointService.getAllData();
        }

        List<String> lineIds=routeLineCheckService.getCheckedLineIds();
        HashMap<String,Boolean> idMaps=new HashMap<>();
        for(String lineId:lineIds)
        {
            String[]items=lineId.split("_");
            String key=items[0]+"_"+items[1];
            idMaps.put(key,true);
        }

        RouteLineCalcFast routeLineCalcFast =new RouteLineCalcFast(allpoints,alllines,0,false);

        List<RouteLine> routeLines=new ArrayList<>();
        List<HarbourType> harbourTypes=  harbourTypeService.getAll();
        for(int i=0;i<harbours.size();++i)
        {
            Integer startHarbourType=harbours.get(i).getHarbourType();
            if(startHarbourType==null)continue;

            for(int j=0;j<harbours.size();++j)
            {
                if(i!=j)
                {
                    if(routeLines.size()>=maxNum)
                    {
                        i=harbours.size()+1;
                        break;
                    }

                    Integer endHarbourType=harbours.get(j).getHarbourType();
                    if(endHarbourType==null) continue;

                    //yulj:起始码头与终点码头类型要相同，这个判断有问题，
                    //      组合码头会出问题，而且已经有第一个判断，要第二个干嘛
                    if(startHarbourType.intValue()!=endHarbourType.intValue()) continue;
                    if(!equalType( startHarbourType, endHarbourType, harbourTypes.size())) continue;

                    String startCode=harbours.get(i).getLinePointCode();
                    String endCode=harbours.get(j).getLinePointCode();
                    String idkey=startCode+"_"+endCode;

                    if(idMaps.containsKey(idkey)) continue;

                    List<RouteLine>  tmprouteLines= routeLineCalcFast.getRouteLines(startCode,endCode,NoData,NoData,NoData,"");

                    if(tmprouteLines.size()>0)
                    {
                        routeLines.addAll(tmprouteLines);
                    }
                }
            }
        }

        String content="NO DATA";
        if(routeLines.size()>0)
        {
            List<RouteLineCheck> result=  routeLines.stream().map(a->a.toCheckLine(true)).collect(Collectors.toList());

            routeLineCheckService.cleanExist();
            routeLineCheckService.insertList(result);
            content= JSON.toJSONString(result);
        }

        return content;
    }


    private  HashMap<String,Boolean> noRouteCodes=new HashMap<>();
    @ResponseBody
    @RequestMapping({"depthNodes/{maxNum}/{shipTon}"})
    public  String calcDepthLineList(@PathVariable("maxNum")Integer maxNum,@PathVariable("shipTon")Double shipTon)
    {
        List<Harbour>  harbours =harbourService.getAllData();
        List<BaseLine> alllines = this.baseLineService.getAllData();
        List<LinePoint>  allpoints = linePointService.getAllData();

        //yulj:没有用到，定义这个干嘛
        //HashMap<String,Double> harbourLats=new HashMap<>();

        List<Harbour> depthHarbours=new ArrayList<>();

        for(int i=0;i<harbours.size();++i) {
            String code = harbours.get(i).getLinePointCode();

            //yulj:不明白为什么不用harbours.get(i).getNameLat()获取港口纬度，而且也没有用到harbourLats
           /* Optional<LinePoint> pointOp= allpoints.stream().filter(a->a.getCode().equals(code)).findFirst();
            if(pointOp.isPresent())
            {
                double lat=   pointOp.get().getLat();
                harbourLats.put(code,lat);
            }*/

            //yulj:获取所有包含该港口的航线
            List<BaseLine> startLines = alllines.stream().filter(a -> a.getStartCode().equals(code) || a.getEndCode().equals(code)).filter(a->a.getDraught()!=NoData||a.getDraught()!=null).collect(Collectors.toList());
            if(startLines==null||startLines.size()==0) continue;

            //yulj:获取最大吃水
            double maxDepth=startLines.stream().mapToDouble(a->a.getDraught()).max().getAsDouble();
            if(maxDepth>=10) {
                depthHarbours.add( harbours.get(i));
            }
        }

        if(shipTon==null) shipTon=NoData;

        List<String> lineIds=routeLineCheckService.getCheckedLineIds();
        HashMap<String,Boolean> idMaps=new HashMap<>();
        for(String lineId:lineIds)
        {
            //yulj:拆分之后又合并，因为可拆分三段或四段，取前两段，
            String[]items=lineId.split("_");
            String key=items[0]+"_"+items[1];
            idMaps.put(key,true);// 而且既然值都是true，何必用哈希表
        }

        RouteLineCalcFast routeLineCalcFast =new RouteLineCalcFast(allpoints,alllines,0,false);
        routeLineCalcFast.setExtend(true);

        List<RouteLine> routeLines=new ArrayList<>();
        List<HarbourType> harbourTypes=  harbourTypeService.getAll();
        for(int i=0;i<depthHarbours.size();++i)
        {
            Integer startHarbourType=depthHarbours.get(i).getHarbourType();
            if(startHarbourType==null)continue;

            for(int j=0;j<depthHarbours.size();++j)
            {
                if(i!=j)
                {
                    if(routeLines.size()>=maxNum)
                    {
                        i=depthHarbours.size()+1;
                        break;
                    }

                    Integer endHarbourType=depthHarbours.get(j).getHarbourType();
                    if(endHarbourType==null) continue;

                    //yulj:起始码头与终点码头类型要相同，这个判断有问题，
                    //      组合码头会出问题，而且已经有第一个判断，要第二个干嘛
                    if(startHarbourType.intValue()!=endHarbourType.intValue()) continue;
                    if(!equalType( startHarbourType, endHarbourType, harbourTypes.size())) continue;

                    String startCode=depthHarbours.get(i).getLinePointCode();
                    String endCode=depthHarbours.get(j).getLinePointCode();
                    String idkey=startCode+"_"+endCode;

                    //yulj:两个港口之间已有航线就不计算了？
                    if(idMaps.containsKey(idkey)) continue;

                    if(noRouteCodes.containsKey(idkey)) continue;

                    List<RouteLine>  tmprouteLines= routeLineCalcFast.getRouteLines(startCode,endCode,NoData,NoData,shipTon,"");

                    if(tmprouteLines.size()>0)
                    {
                        routeLines.addAll(tmprouteLines);
                    }
                    else
                    {
                        System.out.println("no route: startCode = " + startCode+",endCode = "+endCode);
                        noRouteCodes.put(idkey,true);
                    }
                }
            }
        }

        String content="NO DATA";
        if(routeLines.size()>0)
        {
            List<RouteLineCheck> result=  routeLines.stream().map(a->a.toCheckLine(true)).collect(Collectors.toList());

            routeLineCheckService.cleanExist();
            routeLineCheckService.insertList(result);
            content= JSON.toJSONString(result);
        }

        return content;
    }


    private  boolean equalType(Integer start,Integer end,Integer count)
    {
        List<Integer> svalues= Tool.toBinValues(start,count);
        List<Integer> evalues= Tool.toBinValues(end,count);
        if(checkType( svalues)==false) return false;
        if(checkType( evalues)==false) return false;

        for(Integer value:svalues){
            Optional<Integer> find= evalues.stream().filter(a->a.intValue()==value.intValue()).findFirst();
            if(find.isPresent())
            {
                return  true;
            }
        }

        return  false;
    }

    private  boolean checkType(List<Integer> values){

        if(values.contains(1)) return  true;
        if(values.contains(2)) return  true;
        if(values.contains(3)) return  true;

        return  false;
    }
}
