package cn.ehanghai.route.nav.action;

import cn.ehanghai.route.common.action.BaseApiAction;
import cn.ehanghai.route.common.excelimport.*;
import cn.ehanghai.route.common.constants.ResponseCode;
import cn.ehanghai.route.common.utils.ResponseBean;
import cn.ehanghai.route.common.utils.WebService;
import cn.ehanghai.route.nav.domain.*;
import cn.ehanghai.route.nav.service.*;
import cn.ehanghai.route.sys.domain.User;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping("/nav/excelImport")
public class NavExcelImportAction  extends BaseApiAction<BaseLine, NavBaseLineService>
{
    @Autowired
    private NavBaseLineService baseLineService;
    @Autowired
    private LinePointService linePointService;

    @Autowired
    LinePointAllService linePointAllService;

    @Autowired
    private OperationRecordService operationRecordService;

    @Autowired
    private HarbourTypeService harbourTypeService;

    @Autowired
    private HarbourService harbourService;

    @Autowired
    private TDepthService tdepthService;

    private  double NoData=-9999;



    @ResponseBody
    @RequestMapping(value="importData", method=RequestMethod.POST)
    public  ResponseBean importData(HttpServletRequest request) throws Exception
    {

        String message="数据文件导入失败";
        Part part=   request.getPart("file");
        String filename=  FileProcess( part);
        File file=new File(filename);
        if(!file.exists())
        {

            JSONObject result=new JSONObject();
            result.put(message,true);
            return new ResponseBean(ResponseCode.FAIL.getCode(),"文件不存在", result);
        }
        if(filename.endsWith(".xls")||filename.endsWith(".xlsx"))
        {
            String error=  importRouteLineExcel( request, part, filename);
            if(!error.equals("")) {

                JSONObject result=new JSONObject();
                result.put(message,true);
                return new ResponseBean(ResponseCode.FAIL.getCode(),error, result);

            }
        }


        if(filename.endsWith(".csv"))
        {
            importHangBiaoCsv( filename);
        }


        DeleteUploadFiles();
        message="数据文件导入成功";
        JSONObject result=new JSONObject();
        result.put(message,true);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),"数据文件导入成功", result);
    }

    private  String  importRouteLineExcel(HttpServletRequest request,Part part,String filename) throws Exception {
        ImportExcelImpl importExcel = new ImportExcelImpl();
        String error= importExcel.checkExcel(filename);
        if(!error.equals("")){

//            JSONObject result=new JSONObject();
//            result.put(message,true);
//            return new ResponseBean(ResponseCode.FAIL.getCode(),error, result);

            return error;
        }
        List<ImportRouteLine> lines= importExcel.readExcel();

        List<LinePoint> allpoints=new ArrayList<>();
        List<BaseLine>alllines=new ArrayList<>();
        Long maxId= linePointService.getMaxId();

        for(int i=0;i<lines.size();++i)
        {
            List<ImportRouteNode>nodes=lines.get(i).getNodes();
            String name=nodes.get(0).getName();
            String startname=name;
            String endname=name;
            String[]items= name.split("至|-");
            if(items.length==2)
            {
                startname=items[0];
                endname=items[1];
            }

            List<String> nodecodes=new ArrayList<>();
            List<LinePoint> linePoints=new ArrayList<>();

            for(int j=0;j<nodes.size();++j)
            {
                String pointname="";
                if(j==0)
                {
                    pointname=startname;
                }
                if(j==nodes.size()-1)
                {
                    pointname=endname;
                }

                double lon=nodes.get(j).getLon();
                double lat=nodes.get(j).getLat();
                int  index=  linepointexist(allpoints, lon, lat);
                if(index>0) {
                    nodecodes.add(allpoints.get(index).getCode());
                    continue;
                }
                LinePoint existpoint  = linePointService.getPointByLonLat(lat,lon);
                if(existpoint!=null)
                {
                    nodecodes.add(existpoint.getCode());
                    continue;
                }



                LinePoint    linePoint=new LinePoint();

                linePoint.setName(pointname);
                linePoint.setIsolated(false);
                linePoint.setRemark(nodes.get(j).getRemark());
                linePoint.setLon((float)nodes.get(j).getLon());
                linePoint.setLat((float)nodes.get(j).getLat());
                linePoint.setNeedBroadcast(0);
                linePoint.setHarbour(false);
                linePoint.setImportState(0);
                linePoint.calcGeoHash();
                linePoint.setValid(1);

                String code = linePointService.toCode(maxId++);
                linePoint.setCode(code);

                linePoints.add(linePoint);
                nodecodes.add(code);

            }

            List<BaseLine> baseLines=new ArrayList<>();
            for(int j=0;j<nodecodes.size()-1;++j)
            {

                String startCode=nodecodes.get(j);
                String endCode=nodecodes.get(j+1);
                int index=lineexist(alllines,startCode,endCode);
                if(index>0) continue;
                BaseLine existline=  baseLineService.getBaseLineByStartAndEnd(startCode,endCode);
                if(existline!=null) continue;
                existline=  baseLineService.getBaseLineByStartAndEnd(endCode,startCode);
                if(existline!=null) continue;

                BaseLine baseLine=new BaseLine();
                baseLine.setStartCode(startCode);
                baseLine.setEndCode(endCode);
                baseLine.setType(1);
                baseLine.setDistance((long)NoData);
                baseLine.setDraught(NoData);
                baseLine.setHigh(NoData);
                baseLine.setTonnage((int)NoData);
                baseLine.setLane(false);
                baseLine.setOneWayStreet(0);
                baseLine.setImportState(0);
                baseLine.setName(name);
                baseLine.setWaterwayWidth(NoData);
                baseLine.setValid(1);
                baseLines.add(baseLine);
            }


            if(linePoints.size()>0) allpoints.addAll(linePoints);
            if(baseLines.size()>0) alllines.addAll(baseLines);
        }

        //操作记录
        String orgfilename=GetFileName(part);
        Long userId=    getCurrentUser(request).getId();
        NavOperationRecordAction.SaveFileOperationRecord(operationRecordService,userId,orgfilename,"import");
        linePointService.insertList(allpoints);
        baseLineService.insertList(alllines);

        WebService.checkRouteLine(alllines);

        return "";
    }

    private  void  importHangBiaoCsv(String filename) throws IOException {
        List<LinePoint> linePoints=new ArrayList<>();

        File file=new File(filename);
        List<String> lines= FileUtils.readLines(file,"GBK");

        Long maxId= linePointService.getMaxId();

        for(int i=1;i<lines.size();++i)
        {
            String[] items=lines.get(i).split(",");
            if(items.length==2)
            {
                String name=items[0];
                //yulj:应李乐乐要求，增加经纬度分隔符为"、"的情况。
                String []iitems;
                if(items[1].contains("、"))
                    iitems=items[1].split("、");
                else
                    iitems=items[1].split(" |\t");

                if(iitems.length<2) continue;

                double lat=ImportRouteNode.ParseAngle(iitems[0]);
                double lon=ImportRouteNode.ParseAngle(iitems[1]);

                //yulj:加入判断，如果同一位置已经有转向点并且名称相同，则不再加入
                LinePoint existpoint  = linePointService.getPointByLonLat(lat,lon);
                if(existpoint!=null&&existpoint.getName().equals(name))continue;

                LinePoint    linePoint=new LinePoint();

                linePoint.setName(name);
                linePoint.setIsolated(false);
                linePoint.setRemark("");
                linePoint.setLon((float)lon);
                linePoint.setLat((float)lat);
                linePoint.setNeedBroadcast(0);
                linePoint.setHarbour(false);
                linePoint.setImportState(0);
                linePoint.calcGeoHash();
                linePoint.setValid(1);

                String code = linePointService.toCode(maxId++);
                linePoint.setCode(code);

                linePoints.add(linePoint);

            }
        }

        linePointService.insertList(linePoints);
    }


    @ResponseBody
    @RequestMapping({"importHarbourType"})
    public ResponseBean importPortTypeData() throws Exception {

        //String file="C:\\Users\\Administrator\\Desktop\\test\\港口类型20190316.xlsx";
        String file="E:\\ShipRoute\\全国码头分类（从北到南）.xlsx";
        List<ImportHarbourType> datas= ImportPortTypeExcel.ParseFile( file);
        List<HarbourType> harbourTypes=  harbourTypeService.getAll();

        for(HarbourType ht:harbourTypes)
        {
            int bit=ht.getType();
            ht.setType((int)(Math.pow(2,bit-1)));
        }

        List<Harbour> harbours=  harbourService.getAllData();
        for(ImportHarbourType importHarbourType:datas)
        {
            Optional<Harbour> findOp=  harbours.stream().filter(a->a.getLinePointCode().equals(importHarbourType.getCode())).findFirst();
            if(findOp.isPresent())
            {
                Optional<HarbourType> findTypeOp=   harbourTypes.stream().filter(a->a.getName().equals(importHarbourType.getName())).findFirst();
                if(findTypeOp.isPresent())
                {
                    int oldtype=0;
                    if(findOp.get().getHarbourType()!=null)
                        oldtype=findOp.get().getHarbourType();
                    int newtype=oldtype|findTypeOp.get().getType();
                    findOp.get().setHarbourType(newtype);

                    harbourService.saveOrUpdate(findOp.get());
                }
            }
        }

        String   message="数据文件导入成功";
        JSONObject result=new JSONObject();
        result.put(message,true);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),"数据文件导入成功", result);
    }

    //yulj:导入Poi港口,设定导入的港口Code在'A-0100000'和'A-0200000'之间
    @ResponseBody
    @RequestMapping({"importPoiHarbour"})
    public ResponseBean importPoiHarbour() throws Exception {

        //String file="C:\\Users\\Administrator\\Desktop\\test\\港口类型20190316.xlsx";
        String file="E:\\ShipRoute\\nav_focus_t_poi.xlsx";
        List<Harbour> datas= ImportPoiHabour.ParseFile( file);

        //yulj:导入的数据同时保存到nav_t_line_point_all_test和nav_t_harbour_test两个表中
        harbourService.DeletePoiHarbour();
        LinePoint linePoint=new LinePoint();
        linePoint.setValid(1);
        linePointService.DeletePoiHarbour();

        for(Harbour har:datas)
        {
            linePoint.setId(0L);
            linePoint.setCode(har.getLinePointCode());
            linePoint.setName(har.getName());
            linePoint.setLon(Float.parseFloat(har.getNameLon().toString()));
            linePoint.setLat(Float.parseFloat(har.getNameLat().toString()));
            linePoint.setNeedBroadcast(0);
            linePoint.setImportState(1);
            linePointService.saveOrUpdate(linePoint);
            harbourService.saveOrUpdate(har);
        }

        String   message="Poi港口数据文件导入成功";
        JSONObject result=new JSONObject();
        result.put(message,true);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),message, result);
    }


    //yulj:从excel导入水深数据到自建表TDepth，检查数据用
    @ResponseBody
    @RequestMapping({"importDepth"})
    public ResponseBean importDepth() throws Exception {

        //String file="C:\\Users\\Administrator\\Desktop\\test\\港口类型20190316.xlsx";
        String fileDir="E:\\ShipRoute\\水深数据全国\\";

        File file=new File(fileDir); //用路径实例化一个文件对象
        File[] files=file.listFiles(); //

        for(int i=0;i<files.length;i++) {
            List<TDepth> datas = ImportDepth.ParseFile(files[i]);
            tdepthService.saveList(datas);
        }

        String   message="水深数据文件导入成功";
        JSONObject result=new JSONObject();
        result.put(message,true);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),message, result);
    }



    int linepointexist(List<LinePoint> linePoints,double lon,double lat)
    {

       for(int i=0;i<linePoints.size();++i)
       {
           if(Math.abs(linePoints.get(i).getLon()-lon)<1e-5&&
                   Math.abs(linePoints.get(i).getLat()-lat)<1e-5)
           {
               return i;

           }

       }
       return -1;
    }


    int lineexist(List<BaseLine> baseLines,String startcode,String endcode)
    {
        for(int i=0;i<baseLines.size();++i)
        {
            if(baseLines.get(i).getStartCode().equals(startcode)&&
                   baseLines.get(i).getEndCode().equals(endcode))
            {
                return i;

            }

            if(baseLines.get(i).getEndCode().equals(startcode)&&
                    baseLines.get(i).getStartCode().equals(endcode))
            {
                return i;

            }

        }
        return -1;

    }


    void DeleteUploadFiles()
    {

        String uploadpath = System.getProperty("user.dir") + "/upload";
        File file=new File(uploadpath);
        if(file.exists())
        {
            File[] files = file.listFiles();// 获取目录下的所有文件或文件夹
            for (File f:files) {

                if(f.isFile())
                {
                    f.delete();
                }
            }
        }

    }

    private String GetFileName(Part part){
        if(part.getName().equals("file")) {
            String cd = part.getHeader("Content-Disposition");
            String[] cds = cd.split(";");
            String filename = cds[2].substring(cds[2].indexOf("=") + 1).substring(cds[2].lastIndexOf("//") + 1).replace("\"", "");
        return filename;
        }
        return "";
    }

    private String FileProcess(Part part) throws IOException
    {

        if(part.getName().equals("file")){
            String cd = part.getHeader("Content-Disposition");
            String[] cds = cd.split(";");
            String filename = cds[2].substring(cds[2].indexOf("=")+1).substring(cds[2].lastIndexOf("//")+1).replace("\"", "");
            String prefix= filename.substring(0,filename.lastIndexOf("."));

            // 获取文件的扩展名
            String extension = filename.substring(filename.lastIndexOf(".")+1);

            Date d = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            String formatDate = format.format(d);
            String str = "";
            for(int i=0 ;i <5; i++) {
                int n = (int) (Math.random() * 90) + 10;
                str += n;
            }

            //yulj:应李乐乐要求，增加经纬度分隔符为"、"的情况。
            //      此时发现这边处理文件名称竟然不对的，tmpfile会包含路径，
            //      之后加到uploadfile上会出现错误。简单处理了文件名；
            //      并且调整了文件复制方法。

            // 文件名
            String tmpfile = formatDate + str + "." + extension;//prefix+"_"+formatDate + str + "." + extension;

            String uploadpath = System.getProperty("user.dir") + "/upload";
            File fpath=new File(uploadpath);
            if(!fpath.exists())
            {
                fpath.mkdir();
            }
            String uploadfile=uploadpath+"/"+tmpfile;

            //yulj:换成这种复制方法多简单
            File dest = new File(uploadfile);
            InputStream is = part.getInputStream();
            Files.copy(is,dest.toPath());
            //这是原来的文件复制方法
            /*FileOutputStream    fos=new FileOutputStream(new File(uploadfile));
            int b = 0;
            while((b = is.read())!=-1){
                fos.write(b);
            }
            fos.close();*/
            is.close();

            return uploadfile;
        }

        return "";
    }





}
