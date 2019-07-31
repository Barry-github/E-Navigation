package cn.ehanghai.routecheck.nav.service;

import cn.ehanghai.routecheck.nav.domain.WaterDepth;
import cn.ehanghai.routecheck.nav.domain.WaterDepthRange;
import cn.ehanghai.routecheck.nav.mapper.WaterDepthMapper;
import cn.ehanghai.routecheck.nav.mapper.WaterDepthRangeMapper;
import net.ryian.commons.GenericsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WaterDepthRangeService {

    @Autowired
    protected WaterDepthRangeMapper mapper;

    protected Class<WaterDepthRange> entityClass;

    public WaterDepthRangeService() {
        entityClass = GenericsUtils.getSuperClassGenricType(getClass());
    }

    public List<WaterDepthRange> getAllData(String resolution)
    {

        return mapper.getAllData(resolution);
    }

    public  void saveList(List<WaterDepthRange> list)
    {
        mapper.insertList(list);
    }
}
