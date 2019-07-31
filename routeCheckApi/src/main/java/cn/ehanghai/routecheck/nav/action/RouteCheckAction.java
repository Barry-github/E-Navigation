package cn.ehanghai.routecheck.nav.action;


import cn.ehanghai.routecheck.common.routelinecheck.PoiData;
import cn.ehanghai.routecheck.common.routelinecheck.RouteLineCheck;
import cn.ehanghai.routecheck.common.routelinecheck.RouteLineData;
import cn.ehanghai.routecheck.nav.domain.BaseLine;
import cn.ehanghai.routecheck.nav.domain.LinePoint;
import cn.ehanghai.routecheck.nav.service.LinePointService;
import cn.ehanghai.routecheck.nav.service.NavBaseLineService;
import cn.ehanghai.routecheck.poi.domain.ErrorLine;
import cn.ehanghai.routecheck.poi.domain.PoiInfo;
import cn.ehanghai.routecheck.poi.service.ErrorLineService;
import cn.ehanghai.routecheck.poi.service.PoiService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/nav/routeCheck")
public class RouteCheckAction {

    static List<PoiData> poiDatas;

    static long time=0;


    @Autowired
    private PoiService poiService;


    @Autowired
    private LinePointService linePointService;


    @Autowired
    private NavBaseLineService baseLineService;


    @Autowired
    private ErrorLineService errorLineService;


    @ResponseBody
    @RequestMapping(value="check")
    public String routeCheck( String jsonargs)
    {
        try{
            queryPoi();
         List<BaseLine> baseLines =JSONObject.parseArray(jsonargs,BaseLine.class);


//            String json=JSONObject .toJSONString(poiDatas);
//            BufferedWriter out=new BufferedWriter(new FileWriter("D:\\json.txt"));
//            out.write(json);
//            out.close();

            for (BaseLine line :
                    baseLines) {
                String startCode=line.getStartCode();
                String endCode=line.getEndCode();
                LinePoint startpoint= linePointService.getPointByCode(startCode);
                LinePoint endpoint= linePointService.getPointByCode(endCode);

                BaseLine baseLine= baseLineService.getBaseLineByStartAndEnd(startCode,endCode);

                RouteLineData  routeLineData=new RouteLineData(baseLine,startpoint,endpoint);

                for (PoiData poiData:poiDatas  )
                {//澳门友谊大桥

                    ErrorLine errorLine= RouteLineCheck.Check(routeLineData,poiData);
                    if(errorLine!=null)
                    {
                        errorLineService.Insert(errorLine);
                    }
                }
            }

            return "Success";
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return "Failure";
    }

    @ResponseBody
    @RequestMapping(value="checkAll")
    public String routeCheckAll( )
    {

//        try{
            queryPoi();
            List<BaseLine> baseLines =   baseLineService.getAll();

            for (BaseLine line : baseLines) {

                if(line.getValid()==0) continue;

                String startCode=line.getStartCode();
                String endCode=line.getEndCode();
                LinePoint startpoint= linePointService.getPointByCode(startCode);
                LinePoint endpoint= linePointService.getPointByCode(endCode);
                System.out.println(startCode+" - "+endCode);



                BaseLine baseLine= baseLineService.getBaseLineByStartAndEnd(startCode,endCode);

                RouteLineData  routeLineData=new RouteLineData(baseLine,startpoint,endpoint);

                for (PoiData poiData:poiDatas  )
                {//澳门友谊大桥

                    ErrorLine errorLine= RouteLineCheck.Check(routeLineData,poiData);
                    if(errorLine!=null)
                    {
                        errorLineService.Insert(errorLine);
                    }
                }
            }

            return "Success";
//        }
//        catch(Exception ex)
//        {
//            ex.printStackTrace();
//        }
//        return "Failure";
    }





    private    void queryPoi()
    {
        long interval=24*60*60*1000;
        long nowtime=System.currentTimeMillis();
        if(time==0||time+interval<nowtime)
        {
            poiDatas=new ArrayList<>();
            String[] names=new String[]{"禁航区","军事禁区","导弹火炮射击区","锚地","疑存雷区","沉船","暗礁","适淹礁","大桥","架空电缆"};

            for (String name :  names) {
                List<PoiInfo> poiInfos = poiService.getPoiInfosByName(name);

                for (PoiInfo info :  poiInfos) {
                    PoiData poiData = PoiData.ToPoiData(info, name);
                    if(poiData!=null)
                    {
                        poiDatas.add(poiData);

                    }
                }

            }
            time=nowtime;
        }

    }
}
