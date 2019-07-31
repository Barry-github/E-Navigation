package cn.ehanghai.route.nav.action;

import cn.ehanghai.route.common.constants.ResponseCode;
import cn.ehanghai.route.common.utils.ResponseBean;
import cn.ehanghai.route.common.utils.WebService;
import cn.ehanghai.route.nav.domain.AisPath;
import cn.ehanghai.route.nav.domain.AisTrace;
import cn.ehanghai.route.nav.domain.RouteLineCheck;
import cn.ehanghai.route.nav.domain.ShipTrace;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/nav/ais")
public class QueryAisAction {

    private  static List<String> mmsilist;

    private  static  int nextIndex;


    @ResponseBody
    @RequestMapping({"query/batch/{starttime}/{endtime}/{mmsicount}/{aistype}"})
    public  ResponseBean  aisQuery(HttpServletRequest request,@PathVariable("starttime")String starttime, @PathVariable("endtime")String endtime, @PathVariable("mmsicount")String mmsicount,@PathVariable("aistype")String aistype) {
        switch (aistype)
        {
            case "batch":
                return aisQueryBatch(request,starttime,endtime,mmsicount);
            case "mmsi":
                return aisQueryMmsi(request,starttime,endtime,mmsicount);
        }
        String   message="traces";
        JSONObject result=new JSONObject();
        result.put(message,null);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),"类型参数错误", result);

    }


    private  ResponseBean  aisQueryBatch(HttpServletRequest request,@PathVariable("starttime")String starttime, @PathVariable("endtime")String endtime, @PathVariable("mmsicount")String mmsicount)
    {

        if(mmsilist==null)
        {
            init();
        }

        if(mmsilist==null||mmsilist.size()==0)
        {
            String   message="traces";
            JSONObject result=new JSONObject();
            result.put(message,null);
            return new ResponseBean(ResponseCode.SUCCESS, result);
        }

        int len=Integer.parseInt(mmsicount);
        List<AisTrace> alltraces=new ArrayList<>();
        for(int i=0;i<len;++i)
        {
            if(nextIndex>=mmsilist.size())
            {
                nextIndex=0;
            }
            String mmsi=mmsilist.get(nextIndex);
            mmsi=mmsi.replace("\"","").trim();
            List<ShipTrace> shipTraces=    WebService.queryAis(starttime,endtime,mmsi);
            AisTrace aisTrace=new AisTrace();
            aisTrace.setMmsi(mmsi);
            List<AisPath> paths=  toAisPath(   shipTraces);
            aisTrace.setPaths(paths);

            alltraces.add(aisTrace);

            nextIndex++;
        }


        String   message="traces";
        JSONObject result=new JSONObject();
        result.put(message,alltraces);
        return new ResponseBean(ResponseCode.SUCCESS, result);
    }


    private  ResponseBean  aisQueryMmsi(HttpServletRequest request,@PathVariable("starttime")String starttime,@PathVariable("endtime")String endtime,@PathVariable("mmsi")String mmsi)
    {

        List<ShipTrace> shipTraces=    WebService.queryAis(starttime,endtime,mmsi);
        AisTrace aisTrace=new AisTrace();
        aisTrace.setMmsi(mmsi);
        List<AisPath> paths=  toAisPath(   shipTraces);
        aisTrace.setPaths(paths);

        List<AisTrace> alltraces=new ArrayList<>();
        alltraces.add(aisTrace);
        String   message="traces";
        JSONObject result=new JSONObject();
        result.put(message,alltraces);
        return new ResponseBean(ResponseCode.SUCCESS, result);

    }

    private    List<AisPath> toAisPath(List<ShipTrace> shipTraces)
    {

        List<AisPath> paths=new ArrayList<>();
        if(shipTraces==null||shipTraces.size()==0) return  paths;

        Map<Integer,List<ShipTrace>>traceGroups= shipTraces.stream().collect( Collectors.groupingBy(a->a.getTraceIndex()));

        for(Integer key :traceGroups.keySet())
        {
            AisPath aisPath=new AisPath();
            aisPath.setColor(randomColor());

            List<String> items= traceGroups.get(key).stream().map(a->String.format("%f,%f",a.getLon(),a.getLat())).collect(Collectors.toList());
            aisPath.setPath(String.join(",",items));
            paths.add(aisPath);
        }

        return paths;

    }


    private  void init()
    {
        String backupdir = System.getProperty("user.dir") + "/mmsi/mmsilist.txt";
        File file=new File(backupdir);
        if(file.exists())
        {
            try {
                List<String> lines=  FileUtils.readLines(file);
                if(lines!=null&&lines.size()>0)
                {
                    mmsilist=lines.stream().map(a->a.trim()).collect(Collectors.toList());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private  String randomColor()
    {
        Random random = new Random((int)System.currentTimeMillis());
        int r=(int)(random.nextDouble()*255.0) ;
        int g=(int)(random.nextDouble()*255.0) ;
        int b=(int)(random.nextDouble()*255.0) ;

        return String.format("%d,%d,%d",255,0,0);


    }

}
