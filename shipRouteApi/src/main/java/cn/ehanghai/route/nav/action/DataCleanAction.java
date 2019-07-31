package cn.ehanghai.route.nav.action;

import cn.ehanghai.route.common.action.BaseApiAction;
import cn.ehanghai.route.common.constants.ResponseCode;
import cn.ehanghai.route.common.utils.Epoint;
import cn.ehanghai.route.common.utils.ResponseBean;
import cn.ehanghai.route.nav.domain.BaseLine;
import cn.ehanghai.route.nav.domain.Harbour;
import cn.ehanghai.route.nav.domain.LinePoint;
import cn.ehanghai.route.nav.service.HarbourService;
import cn.ehanghai.route.nav.service.LinePointService;
import cn.ehanghai.route.nav.service.NavBaseLineService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/nav/clean")
public class DataCleanAction extends BaseApiAction<LinePoint, LinePointService> {

    @Autowired
    private NavBaseLineService baseLineService;

    @Autowired
    private HarbourService harbourService;

    /**
     * 重复数据的清理
     * */
    @ResponseBody
    @RequestMapping({"cleanalldata"})
    public   ResponseBean clean()
    {
        //yulj:其实geetAllData的SQL语句中已经限定了valid=1，这里没必要再筛选
        List<LinePoint> linePoints= this.entityService.getAllData();
        linePoints=linePoints.stream().filter(a->a.getValid()==1).collect(Collectors.toList());

        List<BaseLine> baseLines=baseLineService.getAllData();
        baseLines=baseLines.stream().filter(a->a.getValid()==1).collect(Collectors.toList());

        List<Harbour> harbours=harbourService.getAllData();
        harbours=harbours.stream().filter(a->a.getValid()==1).collect(Collectors.toList());


        List<LinePoint> newPoints=new ArrayList<>();

        for(LinePoint point:linePoints)
        {
            boolean check=  pointCheck( point,newPoints);
            if(check)
            {
                newPoints.add(point);
            }
        }

        List<Harbour> newharbours=new ArrayList<>();
        for(Harbour harbour:harbours)
        {
            Optional<LinePoint> find= newPoints.stream().filter(a->a.getCode().equals(harbour.getLinePointCode())).findFirst();
            if(find.isPresent())
            {
                newharbours.add(harbour);
            }
        }

        HashMap<String,BaseLine> lineHashMap=new HashMap<>();
        for (BaseLine line:baseLines)
        {
            Optional<LinePoint> start= newPoints.stream().filter(a->a.getCode().equals(line.getStartCode())).findFirst();
            Optional<LinePoint> end= newPoints.stream().filter(a->a.getCode().equals(line.getEndCode())).findFirst();
            if(start.isPresent()&&end.isPresent())
            {
                if(start.get().getCode().equals(end.get().getCode())) continue;

                String code=   codeToKey( line);
                if(!lineHashMap.containsKey(code))
                {
                    lineHashMap.put(code,line);
                }


            }

        }

        List<BaseLine> newLines= new ArrayList<>(lineHashMap.values());

//        List<LinePoint> list=  newPoints.stream().filter(a->a.getCode().equals("E-0001005")).collect(Collectors.toList());
//        System.out.println(list.size());


        this.entityService.clean();
        baseLineService.clean();
        harbourService.clean();

        this.entityService.insertList(newPoints);
        baseLineService.insertList(newLines);
        harbourService.insertList(newharbours);




        String   message="clean";
        JSONObject result=new JSONObject();
        result.put(message,true);
        return new ResponseBean(ResponseCode.SUCCESS.getCode(),"清理完毕", result);

    }

    private   String codeToKey(BaseLine line)
    {
        String code1=line.getStartCode();
        String code2=line.getEndCode();


        Long id1=  Long.parseLong(code1.replace("E-",""));
        Long id2=  Long.parseLong(code2.replace("E-",""));
        if(id1<id2)
        {
            return code1+code2;
        }

        return  code2+code1;
    }


    private    Boolean pointCheck(LinePoint point,List<LinePoint> linePoints)
    {
        Epoint epoint=new Epoint(point.getLon(),point.getLat());
        epoint= Epoint.LonLatToXY(epoint);
        for(LinePoint linePoint:linePoints)
        {
            Epoint checkp=new Epoint(linePoint.getLon(),linePoint.getLat());
            checkp= Epoint.LonLatToXY(checkp);
            if(linePoint.getCode().equals(point.getCode())||Epoint.Distance(checkp,epoint)<1e-5)
            {
                return  false;
            }
        }

        return  true;
    }

}
