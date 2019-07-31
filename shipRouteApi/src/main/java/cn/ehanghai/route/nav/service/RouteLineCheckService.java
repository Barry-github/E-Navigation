package cn.ehanghai.route.nav.service;

import cn.ehanghai.route.common.service.BaseService;
import cn.ehanghai.route.nav.domain.RouteLineCheck;
import cn.ehanghai.route.nav.mapper.RouteLineCheckMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouteLineCheckService extends BaseService<RouteLineCheck, RouteLineCheckMapper> {

    public  void insertList(List<RouteLineCheck> lines)
    {
        if(lines.size()>0)
            mapper.insertList(lines);
    }

    public  List<RouteLineCheck> getUnCheckLines()
    {

        return mapper.getUnCheckLines();
    }

    public  void Delete(String lineId)
    {
mapper.deleteData(lineId);

    }


}
