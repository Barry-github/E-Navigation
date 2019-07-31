package cn.ehanghai.routecalc.nav.service;

import cn.ehanghai.routecalc.nav.domain.RouteLineCheck;
import cn.ehanghai.routecalc.nav.mapper.RouteLineCheckMapper;
import net.ryian.commons.GenericsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouteLineCheckService {


    @Autowired
    protected RouteLineCheckMapper mapper;

    protected Class<RouteLineCheck> entityClass;

    public RouteLineCheckService() {
        entityClass = GenericsUtils.getSuperClassGenricType(getClass());
    }


    public  void insertList(List<RouteLineCheck> lines)
    {
//        if(lines.size()>0)
//            mapper.insertList(lines);

        for(RouteLineCheck line :lines)
        {
            mapper.insertData(line.getLineId(),line.getLineType(),line.getPath(),line.isLineCheck());
        }
    }


    public  List<RouteLineCheck> getAllData()
    {

        return  mapper.getAllData();
    }


    public  void deleteData(String lineId)
    {
        mapper.deleteData(lineId);

    }

    public  void clean()
    {
        mapper.clean();

    }

    public   List<String>getCheckedLineIds()
    {
        return  mapper.getCheckedLineIds();
    }
    public  void cleanExist(){mapper.cleanExist();}
}
