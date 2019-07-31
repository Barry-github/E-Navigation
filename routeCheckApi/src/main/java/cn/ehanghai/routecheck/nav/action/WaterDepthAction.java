package cn.ehanghai.routecheck.nav.action;

import ch.hsr.geohash.GeoHash;

import cn.ehanghai.routecheck.common.constants.ResponseCode;
import cn.ehanghai.routecheck.common.routelinecheck.Epoint;
import cn.ehanghai.routecheck.common.routelinecheck.IdwInterpolation;
import cn.ehanghai.routecheck.common.routelinecheck.WaterDepthToDb;
import cn.ehanghai.routecheck.common.utils.ResponseBean;
import cn.ehanghai.routecheck.nav.domain.*;
import cn.ehanghai.routecheck.nav.service.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/nav/waterdepth")
public class WaterDepthAction  {


    @Autowired
    private WaterDepthService waterDepthService;

    @Autowired
    private WaterDepthRangeService waterDepthRangeService;


    @Autowired
    private LinePointService linePointService;


    @Autowired
    private NavBaseLineService baseLineService;


    @Autowired
    private LinePointAllService linePointAllService;


    @Autowired
    private BaseLineAllService baseLineAllService;

    private  double NoData=-9999;

    @ResponseBody
    @RequestMapping(value="checklinedepth")
    public String checkLineDepth( String jsonargs)
    {
        try{
            List<BaseLine> baseLines =JSONObject.parseArray(jsonargs,BaseLine.class);
            WaterDepthCalc waterDepthCalc=new WaterDepthCalc();
            waterDepthCalc.setWaterDepthService(waterDepthService);

            for (BaseLine line :
                    baseLines) {
                String startCode=line.getStartCode();
                String endCode=line.getEndCode();
                LinePoint start= linePointService.getPointByCode(startCode);
                LinePoint end= linePointService.getPointByCode(endCode);


                double depth=   waterDepthCalc.NoBatchCalc(start.getLon(), start.getLat(), end.getLon(), end.getLat());

                if(depth!=NoData)
                {
                    line.setDraught(depth);
                    baseLineService.saveOrUpdate(line);

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
    @RequestMapping(value="checklinedepth/{startCode}/{endCode}")
    public String checkLineDepth(@PathVariable("startCode") String startCode,@PathVariable("endCode")  String endCode)
    {
        try{
            LinePoint start= linePointService.getPointByCode(startCode);
            LinePoint end= linePointService.getPointByCode(endCode);

            BaseLine line=baseLineService.getBaseLineByStartAndEnd(startCode,endCode);


            WaterDepthCalc waterDepthCalc=new WaterDepthCalc();
            waterDepthCalc.setWaterDepthService(waterDepthService);
            double depth=   waterDepthCalc.NoBatchCalc(start.getLon(), start.getLat(), end.getLon(), end.getLat());

            if(depth!=NoData)
            {
                line.setDraught(depth);
                baseLineService.saveOrUpdate(line);

            }


            return  depth+"";
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return "";

    }


    @ResponseBody
    @RequestMapping(value="checklinedepth/23244/{startCode}/{endCode}")
    public String checkLineDepth2(@PathVariable("startCode") String startCode,@PathVariable("endCode")  String endCode)
    {
        try{
            LinePoint start= linePointAllService.getPointByCode(startCode).toLinePoint();
            LinePoint end= linePointAllService.getPointByCode(endCode).toLinePoint();

            BaseLineAll line=baseLineAllService.getBaseLineByStartAndEnd(startCode,endCode);


            WaterDepthCalc waterDepthCalc=new WaterDepthCalc();
            waterDepthCalc.setWaterDepthService(waterDepthService);
            double depth=   waterDepthCalc.NoBatchCalc(start.getLon(), start.getLat(), end.getLon(), end.getLat());

            if(depth!=NoData)
            {
                line.setDraught(depth);
                baseLineAllService.saveOrUpdate(line);

            }


            return  depth+"";
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return "";

    }

    @ResponseBody
    @RequestMapping(value="checklinedepthbypos")
    public ResponseBean checkLineDepthByPos(double startlon, double startlat, double endlon, double endlat)
    {
        WaterDepthCalc waterDepthCalc=new WaterDepthCalc();
        waterDepthCalc.setWaterDepthService(waterDepthService);
        double depth=   waterDepthCalc.NoBatchCalc(startlon, startlat, endlon, endlat);

        JSONObject result=new JSONObject();
        result.put("points", depth);
        return new ResponseBean(ResponseCode.SUCCESS, result);
    }

    //yulj: Kettle上每天凌晨4点执行该方法
    @ResponseBody
    @RequestMapping(value="checkBaseLineAllDepth")
    public  String  CheckBaseLineAllDepth()
    {
        List<BaseLineAll> baseLineAlls= baseLineAllService.getNoDepths();
        List<LinePointAll> pointAlls=linePointAllService.getAllData();

        WaterDepthCalc waterDepthCalc=new WaterDepthCalc();
        waterDepthCalc.setWaterDepthService(waterDepthService);

        for(BaseLineAll line:baseLineAlls)
        {
            Optional<LinePointAll> startop=   pointAlls.stream().filter(a->a.getCode().equals(line.getStartCode())).findFirst();
            Optional<LinePointAll> endop= pointAlls.stream().filter(a->a.getCode().equals(line.getEndCode())).findFirst();

            if(startop.isPresent()&&endop.isPresent())
            {

                LinePointAll start=startop.get();
                LinePointAll end=endop.get();

                double mindepth=  waterDepthCalc.BatchCalc(start.getLon(),start.getLat(),end.getLon(),end.getLat());
                if(Double.isInfinite(mindepth))
                {
                    System.out.println(line.getStartCode()+ "_"+line.getEndCode()+" Infinity");
                    continue;
                }

                if(mindepth!=NoData)
                {
                    line.setDraught(mindepth);
                    baseLineAllService.saveOrUpdate(line);

                }
                else
                {
                    System.out.println(line.getStartCode()+ "_"+line.getEndCode()+NoData);
                }

            }
            else
            {
                System.out.println(line.getStartCode()+ "_"+line.getEndCode()+" not exist");
            }
        }


        return "SUCCESS";
    }


    @ResponseBody
    @RequestMapping(value="CheckBaseLinesDepth")
    public  String  CheckBaseLinesDepth()
    {
        List<BaseLine> baseLineAlls= baseLineService.getNoDepths();
        List<LinePoint> pointAlls=linePointService.getAll();

        WaterDepthCalc waterDepthCalc=new WaterDepthCalc();
        waterDepthCalc.setWaterDepthService(waterDepthService);

        for(BaseLine line:baseLineAlls)
        {
            Optional<LinePoint> startop=   pointAlls.stream().filter(a->a.getCode().equals(line.getStartCode())).findFirst();
            Optional<LinePoint> endop= pointAlls.stream().filter(a->a.getCode().equals(line.getEndCode())).findFirst();

            if(startop.isPresent()&&endop.isPresent())
            {

                LinePoint start=startop.get();
                LinePoint end=endop.get();

                double mindepth=  waterDepthCalc.BatchCalc(start.getLon(),start.getLat(),end.getLon(),end.getLat());
                if(Double.isInfinite(mindepth))
                {
                    System.out.println(line.getStartCode()+ "_"+line.getEndCode()+" Infinity");
                    continue;
                }

                if(mindepth!=NoData)
                {
                    line.setDraught(mindepth);
                    baseLineService.saveOrUpdate(line);

                }
                else
                {
                    System.out.println(line.getStartCode()+ "_"+line.getEndCode()+NoData);
                }

            }
            else
            {
                System.out.println(line.getStartCode()+ "_"+line.getEndCode()+" not exist");
            }
        }


        return "SUCCESS";
    }


    public ResponseBean toDb1() throws IOException {

        String filepath="E:\\e航海\\Gis引擎\\海图解析\\Gdal研究\\S57Api\\TestApi\\ENC\\Data";
        File file=new File(filepath);
        File[] files=file.listFiles();

        List<WaterDepthRange> ranges=new ArrayList<>();
        for (File f :  files)
        {
            if (f.isFile())
            {
                String resolution="50W";
                if(f.getName().contains("C1313300"))
                {
                    resolution="30W";
                }
                WaterDepthToDb waterDepth=new WaterDepthToDb();
                List<WaterDepth> datas=   waterDepth.readData(f.getAbsolutePath(),resolution);
//                waterDepthService.saveList(datas);

                WaterDepthRange range= getRange(   datas, resolution);
               ranges.add(range);
            }
        }

        waterDepthRangeService.saveList(ranges);
        String   message="数据导入成功";
        JSONObject result=new JSONObject();
        result.put(message,true);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),"数据导入成功1", result);
    }


    public   ResponseBean toDb2() throws Exception {

        List<WaterDepthRange> ranges=new ArrayList<>();

        String path="E:\\e航海\\航线规划\\航线编辑\\航线规划Beta版\\第二阶段-港口航线规划\\水深数据全国";
        File dir=new File(path);
        File[] files= dir.listFiles();
        WaterDepthToDb waterDepth=new WaterDepthToDb();
        List<WaterDepth> alldatas=new ArrayList<>();
        for(File file:files) {
            List<WaterDepth> datas = waterDepth.readExcel(file, "50W");
            alldatas.addAll(datas);
            if(alldatas.size()>5000)
            {
                waterDepthService.saveList(alldatas);
                alldatas=new ArrayList<>();
            }

            WaterDepthRange range= getRange(   datas, "50W");
            ranges.add(range);
        }

        waterDepthRangeService.saveList(ranges);

        if(alldatas.size()>0)
        waterDepthService.saveList(alldatas);

        String   message="数据导入成功";
        JSONObject result=new JSONObject();
        result.put(message,true);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),"数据导入成功2", result);
    }


    WaterDepthRange getRange(  List<WaterDepth> datas,String resolution)
    {
        OptionalDouble maxlon=  datas.stream().mapToDouble(a->a.getLon()).max();
        OptionalDouble minlon=  datas.stream().mapToDouble(a->a.getLon()).min();

        OptionalDouble maxlat=  datas.stream().mapToDouble(a->a.getLat()).max();
        OptionalDouble minlat=  datas.stream().mapToDouble(a->a.getLat()).min();
        WaterDepthRange waterDepthRange=new WaterDepthRange();
        waterDepthRange.setResolution(resolution);
        waterDepthRange.setMaxLon(maxlon.getAsDouble());
        waterDepthRange.setMinLon(minlon.getAsDouble());
        waterDepthRange.setMaxLat(maxlat.getAsDouble());
        waterDepthRange.setMinLat(minlat.getAsDouble());

        return  waterDepthRange;
    }





}

