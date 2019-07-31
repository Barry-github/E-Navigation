package cn.ehanghai.route.nav.action;


import cn.ehanghai.route.common.action.BaseApiAction;
import cn.ehanghai.route.common.constants.ResponseCode;
import cn.ehanghai.route.common.utils.Epoint;
import cn.ehanghai.route.common.utils.ResponseBean;
import cn.ehanghai.route.common.utils.WebService;
import cn.ehanghai.route.nav.domain.*;
import cn.ehanghai.route.nav.service.HarbourService;
import cn.ehanghai.route.nav.service.LinePointService;
import cn.ehanghai.route.nav.service.NavBaseLineService;
import cn.ehanghai.route.nav.service.OperationRecordService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.ryian.commons.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 胡恒博
 **/
@RestController
@RequestMapping("/nav/navPoint")
public class NavPointAction extends BaseApiAction<LinePoint, LinePointService> {

    @Autowired
    private HarbourService harbourTestService;

    @Autowired
    private NavBaseLineService navBaseLineTestService;



    @Autowired
    private OperationRecordService operationRecordService;

    private  double NoData=-9999;


    /**
     * 获取转向点编号
     */
    @GetMapping("getPointCode")
    public ResponseBean getPointCode() {
        String pointCode = entityService.getPointCode();
        if (!StringUtils.isEmpty(pointCode)) {
            return new ResponseBean(ResponseCode.SUCCESS, pointCode);
        }
        return new ResponseBean(ResponseCode.SUCCESS, "E-000001");
    }

    @Override
    public ResponseBean save(HttpServletRequest request) throws Exception {
        LinePoint linePoint = bindEntity(request, entityClass);
        if(!linePoint.checkRange())
        {
            return new ResponseBean(ResponseCode.FAIL.getCode(), "经纬度数据错误，请检查!",null);
        }
        if (linePoint.getId() != null) {
            LinePoint oldPoint = entityService.get(linePoint.getId());
            if (oldPoint == null) {
                return new ResponseBean(ResponseCode.FAIL.getCode(), "数据不存在!",null);
            }
            linePoint.setCode(linePoint.getCode());
        }else {
            String pointCode = entityService.getPointCode();
            linePoint.setValid(1);
            linePoint.setCode(pointCode);
            linePoint.calcGeoHash();
            String  message=  checkNearPoint(linePoint);
            if(!message.equals(""))
            {
                return new ResponseBean(ResponseCode.FAIL.getCode(), message,null);
            }


        }
        String isHarbour = request.getParameter("harbour");
        String needBroadcast = request.getParameter("needBroadcast");
        if (StringUtils.isEmpty(needBroadcast)) {
            linePoint.setNeedBroadcast(0);
        }
        //yulj:保存转向点时，如果是码头，则同时要保存到码头表中
        //      如果相同Code的码头已经存在，应该只改变名称
        Harbour harbourByCode = harbourTestService.getHarbourByCode(linePoint.getCode());
        if (strToBoolean(isHarbour)) {
            if (StringUtils.isEmpty(linePoint.getName())) {
                return new ResponseBean(ResponseCode.FAIL.getCode(), "港口名字不能为空!",null);
            }
            Harbour harbour = new Harbour();
            if (harbourByCode != null) {
                harbour.setId(harbourByCode.getId());
            }
            harbour.setName(linePoint.getName());
            harbour.setLinePointCode(linePoint.getCode());
            harbour.setNameLat(linePoint.getLat().doubleValue());
            harbour.setNameLon(linePoint.getLon().doubleValue());
            harbour.setValid(1);

            //yulj:linePoint的HarbourType有地方赋值了吗
            if(linePoint.getHarbourType().intValue()!=-1)
            {
                harbour.setHarbourType(linePoint.getHarbourType());
            }


            harbourTestService.saveOrUpdate(harbour);
        }else {
            //原来是港口，现在不是，则删除港口
            if (harbourByCode != null) {
                harbourTestService.logicRemove(harbourByCode.getId());
            }
        }
        linePoint.setImportState(1);


        //操作记录
        Long userId=    getCurrentUser(request).getId();
        String action="update";
        if(linePoint.getId()==null) action="insert";
        NavOperationRecordAction.SavePointOperationRecord(operationRecordService,userId,linePoint,action);

        //检测航线
        CheckLine( linePoint, action);

        Long id = entityService.saveOrUpdate(linePoint);
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("id", id);
        result.put("code", linePoint.getCode());
        return new ResponseBean(ResponseCode.SUCCESS, result);


    }

    String   checkNearPoint(LinePoint linePoint)
    {
        String geohashcontent= linePoint.getGeohash().substring(0,23);
        List<LinePoint> nearPoints=  entityService.getNearPoints(geohashcontent);
        Epoint  startp=new Epoint(linePoint.getLon(),linePoint.getLat());
        startp= Epoint.LonLatToXY(startp);
        for(LinePoint point :nearPoints)
        {

            Epoint epoint = new Epoint(point.getLon(), point.getLat());
            epoint=  Epoint.LonLatToXY(epoint);
            double distance= Epoint.Distance(startp,epoint);
            if(distance<30)
            {
                String  message="保存失败,距转向点"+point.getCode()+"太近，经纬度有重合";
                return message;
            }
        }
        return "";
    }

    @Override
    public ResponseBean delete(HttpServletRequest request,@PathVariable("ids") String ids) {

        Long userId=    getCurrentUser(request).getId();

        for (String id : ids.split(",")) {
            LinePoint linePointTest = entityService.get(Long.valueOf(id));
            //操作记录
            NavOperationRecordAction.SavePointOperationRecord(operationRecordService,userId,linePointTest,"delete");

            if (navBaseLineTestService.checkPointHasLine(linePointTest.getCode())) {
                return new ResponseBean(ResponseCode.FAIL.getCode(), "删除失败！该点不是孤立点，不能删除！",null);
            }
            entityService.logicRemove(Long.valueOf(id));
            Harbour harbourByCode = harbourTestService.getHarbourByCode(linePointTest.getCode());
            if (harbourByCode != null) {
                harbourTestService.logicRemove(harbourByCode.getId());
            }
        }
        return new ResponseBean(ResponseCode.SUCCESS, messageSuccessWrap());
    }


    @ResponseBody
    @RequestMapping(value="insertLinePoint")
    public ResponseBean insertLinePoint(HttpServletRequest request,String startCode,String endCode,Long lineid,String name,Boolean harbour, Float lon, Float lat,Integer needBroadcast )
    {

        LinePoint startPoint=entityService.getPointByCode(startCode);
        LinePoint endPoint=entityService.getPointByCode(endCode);

        String message="";
        boolean samepos=false;
        Epoint startp=new Epoint(startPoint.getLon(),startPoint.getLat());
        startp=Epoint.LonLatToXY(startp);
        Epoint endp=new Epoint(endPoint.getLon(),endPoint.getLat());
        endp=Epoint.LonLatToXY(endp);
        Epoint epoint=new Epoint(lon,lat);
        epoint=Epoint.LonLatToXY(epoint);


        if(Epoint.Distance(startp,epoint)<30)
        {
            message="操作失败,距转向点"+startCode+"太近，经纬度有重合";
            samepos=true;
        }

        if(Epoint.Distance(endp,epoint)<30)
        {
            message="操作失败,距转向点"+endCode+"太近，经纬度有重合";
            samepos=true;
        }

        if(samepos)
        {
            JSONObject result=new JSONObject();
            result.put("操作失败",true);
            return new ResponseBean(ResponseCode.FAIL.getCode(),message, result);

        }

        double distance=Epoint.Distance(startp,endp);
        Epoint vec=new Epoint(endp.getX()-startp.getX(),endp.getY()-startp.getY());
        Epoint unitvec=new Epoint(vec.getX()/distance,vec.getY()/distance);
        double sdis=Epoint.Distance(startp,epoint);
        double x=unitvec.getX()*sdis+startp.getX();
        double y=unitvec.getY()*sdis+startp.getY();
        Epoint mpoint=new Epoint(x,y);
        mpoint=Epoint.XYToLonLat(mpoint);

        lon=(float)mpoint.getX();
        lat=(float)mpoint.getY();

        String pointCode = entityService.getPointCode();
        LinePoint linePoint=new LinePoint();

        linePoint.setName(name);
        linePoint.setIsolated(false);
        linePoint.setRemark("");
        linePoint.setLon(lon);
        linePoint.setLat(lat);
        linePoint.setNeedBroadcast(0);
        linePoint.setHarbour(false);
        linePoint.setImportState(0);
        linePoint.setCode(pointCode);
        linePoint.setHarbour(harbour);
        linePoint.setNeedBroadcast(needBroadcast);
        linePoint.calcGeoHash();
        linePoint.setValid(1);

        //操作记录
        Long userId=    getCurrentUser(request).getId();
        String action="insertlinepoint";
        NavOperationRecordAction.SavePointOperationRecord(operationRecordService,userId,linePoint,action);


        entityService.saveOrUpdate(linePoint);


        List<BaseLine> baseLines=new ArrayList<>();

        BaseLine oldline=navBaseLineTestService.getBaseLineByStartAndEnd(startCode,endCode);
        oldline.setValid(0);
        navBaseLineTestService.saveOrUpdate(oldline);

        BaseLine baseLine=new BaseLine();
        baseLine.setStartCode(startCode);
        baseLine.setEndCode(pointCode);
        baseLine.setType(oldline.getType());
        baseLine.setDistance((long)NoData);
        baseLine.setDraught(oldline.getDraught());
        baseLine.setHigh(oldline.getHigh());
        baseLine.setTonnage(oldline.getTonnage());
        baseLine.setLane(oldline.getLane());
        baseLine.setOneWayStreet(oldline.getOneWayStreet());
        baseLine.setImportState(oldline.getImportState());
        baseLine.setName(oldline.getName());
        baseLine.setWaterwayWidth(oldline.getWaterwayWidth());
        baseLine.setValid(1);
        baseLines.add(baseLine);
        navBaseLineTestService.saveOrUpdate(baseLine);



        baseLine=new BaseLine();
        baseLine.setStartCode(pointCode);
        baseLine.setEndCode(endCode);
        baseLine.setType(oldline.getType());
        baseLine.setDistance((long)NoData);
        baseLine.setDraught(oldline.getDraught());
        baseLine.setHigh(oldline.getHigh());
        baseLine.setTonnage(oldline.getTonnage());
        baseLine.setLane(oldline.getLane());
        baseLine.setOneWayStreet(oldline.getOneWayStreet());
        baseLine.setImportState(oldline.getImportState());
        baseLine.setName(oldline.getName());
        baseLine.setWaterwayWidth(oldline.getWaterwayWidth());
        baseLine.setValid(1);
        baseLines.add(baseLine);
        navBaseLineTestService.saveOrUpdate(baseLine);




        WebService.checkRouteLine(baseLines);

        JSONObject result=new JSONObject();
        result.put("操作成功",true);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),"操作成功", result);

    }

   private void CheckLine(LinePoint linePoint,String action)
    {
        if(action!="update") return;

        List<BaseLine> baseLines= navBaseLineTestService.getBaseLinesByCode(linePoint.getCode());
        if(baseLines!=null  &&baseLines.size()>0)
        {
            WebService.checkRouteLine(baseLines);
        }

    }

    @ResponseBody
    @RequestMapping(value="mergePoints")
    public ResponseBean mergePoints(HttpServletRequest request,String mainCode,String otherCodes) {

        LinePoint mainpoint=   entityService.getPointByCode(mainCode);
        //操作记录
        Long userId=    getCurrentUser(request).getId();
        String action="mergePoints";

        NavOperationRecordAction.SavePointOperationRecord(operationRecordService,userId,mainpoint,action);

        List<BaseLine> alllines=new ArrayList<>();

        String [] items= otherCodes.split(",");
        for (String item :
                items) {
            String code=item.trim();
            if(!code.equals(""))
            {

                List<BaseLine> baseLines= navBaseLineTestService.getBaseLinesByCode(code);
                for (BaseLine line: baseLines )
                {

                    if(line.getStartCode().equals(mainCode)||line.getEndCode().equals(mainCode))
                    {//如果 次点和主点 本就在一条航线上，那么就需要删除这条航线
                        line.setValid(0);
                        navBaseLineTestService.saveOrUpdate(line);
                        continue;
                    }

                    if( line.getStartCode().equals(code))
                    {
                        line.setStartCode(mainCode);
                    }

                    if( line.getEndCode().equals(code))
                    {
                        line.setEndCode(mainCode);
                    }

                    boolean isexist= navBaseLineTestService.lineExist(line.getStartCode(),line.getEndCode());

                    if(isexist==false) {
                    navBaseLineTestService.saveOrUpdate(line);
                    }


                }

                alllines.addAll(baseLines);

                LinePoint linePoint=   entityService.getPointByCode(code);
                linePoint.setValid(0);
                entityService.saveOrUpdate(linePoint);

            }
        }

        WebService.checkRouteLine(alllines);

        JSONObject result=new JSONObject();
        result.put("操作成功",true);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),"操作成功", result);

    }


    @ResponseBody
    @RequestMapping(value="backup")
    public ResponseBean backup(HttpServletRequest request, String note)  {



        List<Harbour> harbours = harbourTestService.getAllData();
        List<LinePoint> points = entityService.getAllData();
        List<BaseLine> baseLines = navBaseLineTestService.getAllData();




        BackupData backupData=new BackupData(harbours,points,baseLines);
        String json= JSON.toJSONString(backupData);
        SimpleDateFormat dateformat=new SimpleDateFormat("yyyyMMddHHmmss");
        long now=System.currentTimeMillis();
        String filename=dateformat.format(now)+".json";

        String backupdir = System.getProperty("user.dir") + "/backup";
        File fpath=new File(backupdir);
        if(!fpath.exists())
        {
            fpath.mkdir();
        }
        String backupfile=backupdir+"/"+filename;


        String state="备份成功";
        FileWriter fileWriter= null;
        try {
            fileWriter = new FileWriter(backupfile);
            fileWriter.write(json);
            fileWriter.close();
            String notefile=  backupdir+"/"+dateformat.format(now)+".txt";

            fileWriter = new FileWriter(notefile);
            fileWriter.write(note);
            fileWriter.close();

        } catch (IOException e) {
            state="备份失败";
            File backfile=new File(backupfile);
            if(backfile.exists()) backfile.delete();
        }


        //操作记录
        Long userId=    getCurrentUser(request).getId();
        String action="backup";
        NavOperationRecordAction.SaveFileOperationRecord(operationRecordService,userId,filename,action);



        List<BackupTime> times= GetTimeList();

        JSONObject result=new JSONObject();
        result.put("times",times);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),state, result);
    }

    @ResponseBody
    @RequestMapping(value="timelist")
    public ResponseBean timeList(HttpServletRequest request) {

        List<BackupTime> times= GetTimeList();
        JSONObject result=new JSONObject();
        result.put("times",times);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),"操作成功", result);
    }

    List<BackupTime> GetTimeList()
    {
        String backupdir = System.getProperty("user.dir") + "/backup";
        File fpath=new File(backupdir);
        if(!fpath.exists())
        {

            return new ArrayList<>() ;
        }
        SimpleDateFormat dateformat=new SimpleDateFormat("yyyyMMddHHmmss");
        File[] files= fpath.listFiles();
        List<String>filetimes= Arrays.asList(files).stream()
                .filter(a->a.getName().endsWith(".json"))
                .map(a->a.getName().replace(".json",""))
                .collect(Collectors.toList());
        Collections.sort(filetimes, Comparator.reverseOrder());
        if(filetimes.size()>20)
        {
            for(int i=20;i<filetimes.size();++i)
            {
                String backupfile = backupdir+ "/"+filetimes.get(i)+".json";
                File tmpfile=new File(backupfile);
                if(tmpfile.exists())
                {
                    tmpfile.delete();
                }


                backupfile =  backupdir+ "/"+filetimes.get(i)+".txt";
                tmpfile=new File(backupfile);
                if(tmpfile.exists())
                {
                    tmpfile.delete();
                }


            }
            filetimes=   filetimes.subList(0,20);

        }
        List<BackupTime> times=new ArrayList<>();
        for(int i=0;i<filetimes.size();++i)
        {
            try {
                String timestr=filetimes.get(i);
                String backupfile =  backupdir+ "/"+timestr+".txt";
                File tmpfile=new File(backupfile);
                if(tmpfile.exists())
                {
                    String note= readToString(backupfile);
                    Date date= dateformat.parse(timestr);
                    String time=  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                    times.add(new BackupTime(time,note));

                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }



        return times;
    }

    @ResponseBody
    @RequestMapping(value="rollback/{time}")
    public ResponseBean rollback(HttpServletRequest request,@PathVariable String time)
    {
        int statecode=ResponseCode.SUCCESS.getCode();
        String state="回退成功";

        String filename="";
        try {
            SimpleDateFormat dateFormat=     new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            filename=  new SimpleDateFormat("yyyyMMddHHmmss").format(dateFormat.parse(time))+".json";

            String filepath = System.getProperty("user.dir") + "/backup/"+filename;
            File backupfile=new File(filepath);
            if(!backupfile.exists())
            {
                state="回退失败，文件"+filename+"不存在。";
                statecode=ResponseCode.FAIL.getCode();
            }else
            {


                String text=  readToString(filepath);

                BackupData backupData=JSON.parseObject(text,BackupData.class);


                harbourTestService.clean();
                entityService.clean();
                navBaseLineTestService.clean();

                harbourTestService.insertList(backupData.getHarbours());
                entityService.insertList(backupData.getLinePoints());
                navBaseLineTestService.insertList(backupData.getBaseLines());


            }
        }
        catch (Exception e) {
            e.printStackTrace();
            state="回退失败";
            statecode=ResponseCode.FAIL.getCode();
        }


        //操作记录
        Long userId=    getCurrentUser(request).getId();
        String action="rollback";
        NavOperationRecordAction.SaveFileOperationRecord(operationRecordService,userId,filename,action);


        JSONObject result = new JSONObject();
        if(statecode==ResponseCode.SUCCESS.getCode())
        {
            List<Harbour> harbours = harbourTestService.getHarbours();
            List<LinePoint> points = entityService.getAll();
            List<BaseLine> baseLines = navBaseLineTestService.getAll();

            baseLines =   baseLines.stream().filter(a->a.getValid()!=null&&a.getValid()==1).collect(Collectors.toList());
            points=points.stream().filter(a->a.getValid()!=null&&a.getValid()==1).collect(Collectors.toList());
            harbours=harbours.stream().filter(a->a.getValid()!=null&&a.getValid()==1).collect(Collectors.toList());



            QueryAllData(harbours, points, baseLines);

            result.put("baseLines", JSONArray.parseArray(JSONObject.toJSONString(baseLines)));
            result.put("points", JSONArray.parseArray(JSONObject.toJSONString(points)));

        }


        return new ResponseBean(statecode,state, result);

    }

    static void  QueryAllData(List<Harbour> harbours, List<LinePoint> points, List<BaseLine> baseLines) {



        /**
         * 找出不是港口不和别的点连接的孤立点
         */
        for (LinePoint point : points) {
            point.setHarbourType(-1);

            for (Harbour harbour : harbours) {
                if (harbour.getPid() != null && harbour.getPid() == 0) {
                    continue;
                }
                if (harbour.getLinePointCode().trim().equals(point.getCode().trim())) {
                    point.setHarbour(true);
                    point.setName(harbour.getName());

                    if(harbour.getHarbourType()!=null)
                    point.setHarbourType(harbour.getHarbourType());
                    break;
                }
            }
            //是否是孤立点
            boolean isolated = true;
            for (BaseLine baseLine : baseLines) {
                if (baseLine.getStartCode().trim().equals(point.getCode().trim())) {
                    for (BaseLine baseLine2 : baseLines) {
                        if (baseLine2.getEndCode().trim().equals(point.getCode().trim())) {
                            isolated = false;
                            break;
                        }
                    }
                    if (!isolated) {
                        break;
                    }
                }
            }
            point.setIsolated(isolated);
        }


    }

    private String readToString(String fileName)
    {

        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        FileInputStream in =null;
        try {
            in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new String(filecontent);
    }

    @ResponseBody
    @RequestMapping(value="delBackup/{time}")
    public ResponseBean delBackup(HttpServletRequest request,@PathVariable String time)
    {
        String state="删除备份成功";

        String filename="";
        try {
            SimpleDateFormat dateFormat=     new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String  formattime=new SimpleDateFormat("yyyyMMddHHmmss").format(dateFormat.parse(time));
            filename=formattime+".json";
            String filepath = System.getProperty("user.dir") + "/backup/"+filename;
            File backupfile=new File(filepath);
            if(backupfile.exists())
            {
                backupfile.delete();
            }

            filepath = System.getProperty("user.dir") + "/backup/"+formattime+".txt";
            backupfile=new File(filepath);
            if(backupfile.exists())
            {
                backupfile.delete();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            state="删除备份失败";
        }


        //操作记录
        Long userId=    getCurrentUser(request).getId();
        String action="delBackup";
        NavOperationRecordAction.SaveFileOperationRecord(operationRecordService,userId,filename,action);


        JSONObject result=new JSONObject();
        result.put(state,true);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),state, result);
    }

}
