package cn.ehanghai.routecheck.poi.service;


import cn.ehanghai.routecheck.common.service.BaseService;
import cn.ehanghai.routecheck.poi.domain.PoiInfo;
import cn.ehanghai.routecheck.poi.mapper.PoiMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PoiService extends BaseService<PoiInfo, PoiMapper> {



    public List<PoiInfo> getPoiInfosByName(String name)
    {
        return mapper.getPoiByName(name);
    }

}
