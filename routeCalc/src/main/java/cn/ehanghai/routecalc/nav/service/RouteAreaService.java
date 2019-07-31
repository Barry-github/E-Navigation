package cn.ehanghai.routecalc.nav.service;

import cn.ehanghai.routecalc.nav.domain.RouteArea;
import cn.ehanghai.routecalc.nav.mapper.RouteAreaMapper;
import net.ryian.commons.GenericsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouteAreaService {

    @Autowired
    protected RouteAreaMapper mapper;

    protected Class<RouteArea> entityClass;

    public RouteAreaService() {
        entityClass = GenericsUtils.getSuperClassGenricType(getClass());
    }


    public RouteArea getAreaByCode(String code)
    {
      return   mapper.getAreaByCode(code);
    }

    public List<RouteArea> getAllData()
    {
        return  mapper.getAllData();
    }


}
