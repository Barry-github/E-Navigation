package cn.ehanghai.routecalc.nav.action;

import cn.ehanghai.routecalc.common.utils.Tool;
import cn.ehanghai.routecalc.nav.domain.*;
import cn.ehanghai.routecalc.nav.service.HarbourService;
import cn.ehanghai.routecalc.nav.service.HarbourTypeService;
import cn.ehanghai.routecalc.nav.service.LinePointService;
import cn.ehanghai.routecalc.nav.service.NavBaseLineService;
import com.alibaba.fastjson.JSON;
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
@RequestMapping({"/nav/route/service"})

public class RouteService {

    @Autowired
    private LinePointService linePointService;

    @Autowired
    private NavBaseLineService baseLineService;

    @Autowired
    private HarbourService harbourService;

    private  static RouteLineCalcFast routeLineCalcFast;

    private  static  String harboursJson;

    @Autowired
    private HarbourTypeService harbourTypeService;

    private  double NoData=-9999;




    /**
     * 计算两个港口之间的航线
     * 只使用现有的转向点
     * 只返回多条最短航线
     * */
    @ResponseBody
    @RequestMapping({"calc/{startCode}/{endCode}/{shipHeight}/{depth}/{shipton}"})
    public String calcRoute(
                            @PathVariable("startCode") String startCode,
                            @PathVariable("endCode") String endCode,
                            @PathVariable("shipHeight") double shipHeight,
                            @PathVariable("depth") double depth,
                            @PathVariable("shipton") double shipton)
    {
        return  calcRoute(startCode,endCode,shipHeight,depth,shipton,"");
    }


    //添加定线制限制
    @ResponseBody
    @RequestMapping({"calc/{startCode}/{endCode}/{shipHeight}/{depth}/{shipton}/{laneName}"})
    public String calcRoute(
                            @PathVariable("startCode") String startCode,
                            @PathVariable("endCode") String endCode,
                            @PathVariable("shipHeight") double shipHeight,
                            @PathVariable("depth") double depth,
                            @PathVariable("shipton") double shipton,
                            @PathVariable("laneName") String laneName)
    {
        init();
        List<RouteLine> routeLines= routeLineCalcFast.getRouteLines(startCode,endCode,shipHeight,depth,shipton,laneName);
        List<RouteLineCheck> result=  routeLines.stream().map(a->a.toCheckLine(false)).collect(Collectors.toList());

        //yulj: 应罗剑要求，舟全云上生成的航线暂时限制不超过3条
        int n=result.size();
        if(n>3)
            for(int i=n-1;i>2;i--)
                result.remove(i);

        String json= JSON.toJSONString(result);
        return  json;
    }

    @ResponseBody
    @RequestMapping({"harbours"})
    public  String getHarbours() {
        init();
        return harboursJson;
    }

    @ResponseBody
    @RequestMapping({"getLaneNames"})
    public List<String> getLaneNames(){
        List<String> names=  baseLineService.getLaneNames();
        names.add("全部不经过");
        return names;
    }


    private  void init()
    {
        if(routeLineCalcFast==null)
        {
            reset();
        }
    }


    private  void reset()
    {
        List<BaseLine> alllines= this.baseLineService.getAllData();
        List<LinePoint> allpoints = this.linePointService.getAllData();
        routeLineCalcFast =new RouteLineCalcFast(allpoints,alllines,0,true);

        //yulj:没有这个设置，舟全云上搜出来的航线比航线规划后台搜出来的少
        routeLineCalcFast.setExtend(true);

        /*HashMap<String,List<Integer>>  typeValueMap=new HashMap<>();
        List<HarbourType> harbourTypes=  harbourTypeService.getAll();
        List<Integer> typevalues=  harbourTypes.stream().map(a->a.getType()).collect(Collectors.toList());
        List<List<Integer>> combinations= Tool.combinations(typevalues);
        for(List<Integer> combination:combinations)
        {
            Integer data=  Tool.toBinNum(combination);
            for(Integer type : combination)
            {
                Optional<HarbourType> optional= harbourTypes.stream().filter(a->a.getType().intValue()==type.intValue()).findFirst();
                if(optional.isPresent())
                {
                    String name=optional.get().getName();
                    if(typeValueMap.containsKey(name))
                    {
                        typeValueMap.get(name).add(data);
                    }
                    else
                    {
                        List<Integer> datas=new ArrayList<>();
                        datas.add(data);
                        typeValueMap.put(name,datas);
                    }
                }
            }
        }

        List<Harbour> harbours=harbourService.getHarbours();

        List<Harbour>allHarbours=getHarbourTypes(harbours,typeValueMap,"集装箱码头");
        List<Harbour>tmpHarbours=getHarbourTypes(harbours,typeValueMap,"散杂货码头");
        allHarbours.addAll(tmpHarbours);
        tmpHarbours=getHarbourTypes(harbours,typeValueMap,"油船码头");
        allHarbours.addAll(tmpHarbours);*/

        List<Harbour> harbours=harbourService.getHarbours();
        List<Harbour>allHarbours=new ArrayList<>();
        for(Harbour hb:harbours)
        {
            if(hb.getHarbourType()==null)continue;
            int htype=hb.getHarbourType();
            Harbour newHb=null;
            if((htype&1)>0)
            {
                newHb=hb;
                newHb.setHarbourType(1);
                allHarbours.add(newHb);
            }
            if((htype&2)>0)
            {
                if(newHb==null)newHb=hb;
                else newHb=CloneHarbour(hb);

                newHb.setHarbourType(2);
                allHarbours.add(newHb);
            }
            if((htype&4)>0)
            {
                if(newHb==null)newHb=hb;
                else newHb=CloneHarbour(hb);

                newHb.setHarbourType(3);
                allHarbours.add(newHb);
            }
        }

        harboursJson=JSON.toJSONString(allHarbours);
    }

    private Harbour CloneHarbour(Harbour hb)
    {
        Harbour newHb=new Harbour();
        newHb.setName(hb.getName());
        newHb.setArea(hb.getArea());
        newHb.setCode(hb.getCode());
        newHb.setNameLat(hb.getNameLat());
        newHb.setNameLon(hb.getNameLon());
        newHb.setOverlay(hb.getOverlay());
        newHb.setPid(hb.getPid());
        newHb.setValid(hb.getValid());
        newHb.setLinePointCode(hb.getLinePointCode());
        newHb.setCreateDate(hb.getCreateDate());
        newHb.setCreator(hb.getCreator());
        newHb.setUpdateDate(hb.getUpdateDate());
        newHb.setUpdator(hb.getUpdator());
        newHb.setHarbourType(hb.getHarbourType());
        return newHb;
    }


    private List<Harbour> getHarbourTypes(List<Harbour> harbours, HashMap<String,List<Integer>>  typeValueMap, String name){

        HarbourType harbourType=   harbourTypeService.getType(name);
        List<Integer> datas= typeValueMap.get(harbourType.getName());

        List<Harbour>allHarbours=new ArrayList<>();
        for(Integer data: datas)
        {
            List<Harbour> tmpHabours=   harbours.stream().filter(a->a.getHarbourType()==data).collect(Collectors.toList());

            for (Harbour harbour:tmpHabours)
            {
                harbour.setHarbourType(harbourType.getType());
            }
            allHarbours.addAll(tmpHabours);
        }

        return  allHarbours;

    }
}
