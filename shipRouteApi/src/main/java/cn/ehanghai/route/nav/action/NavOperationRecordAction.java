package cn.ehanghai.route.nav.action;

import cn.ehanghai.route.nav.domain.BaseLine;
import cn.ehanghai.route.nav.domain.LinePoint;
import cn.ehanghai.route.nav.service.OperationRecordService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NavOperationRecordAction
{


    public  static     void SavePointOperationRecord(OperationRecordService operationRecordService,Long userId,LinePoint linePoint, String action)
    {

        String describe="point:"+linePoint.getCode();
        Date time=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestr=sdf.format(time);
        operationRecordService.insertOperationRecord(userId,action,describe,timestr);
    }



    public  static   void SaveLineOperationRecord(OperationRecordService operationRecordService,Long userId,BaseLine baseLine,String action)
    {

        String describe="Line:"+baseLine.getStartCode()+","+baseLine.getEndCode();
        Date time=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestr=sdf.format(time);
        operationRecordService.insertOperationRecord(userId,action,describe,timestr);
    }


    public  static     void SaveFileOperationRecord(OperationRecordService operationRecordService, Long userId,  String filename,String action)
    {

        String describe="file:"+filename;
        Date time=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestr=sdf.format(time);
        operationRecordService.insertOperationRecord(userId,action,describe,timestr);//"import"
    }




}
