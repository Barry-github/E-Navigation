package cn.ehanghai.routecheck.nav.service;

import cn.ehanghai.routecheck.common.service.BaseService;
import cn.ehanghai.routecheck.nav.domain.WaterDepth;
import cn.ehanghai.routecheck.nav.mapper.LinePointAllMapper;
import cn.ehanghai.routecheck.nav.mapper.WaterDepthMapper;
import net.ryian.commons.GenericsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public  class WaterDepthService  {


    @Autowired
    protected WaterDepthMapper mapper;

    protected Class<WaterDepth> entityClass;

    public WaterDepthService() {
        entityClass = GenericsUtils.getSuperClassGenricType(getClass());
    }


    public  void saveList(List<WaterDepth> list)
    {
         mapper.insertList(list);
//        for (WaterDepth data :
//                list) {
//            mapper.insertdepth(data.getLon(),data.getLat(),data.getDepth(),data.getGeohash(),data.getResolution());
//        }
    }

    public List<WaterDepth> getNearPoints(String geohash,String resolution)
    {
        return mapper.getNearPoints(geohash,resolution);
    }

    public List<WaterDepth> getAllData(String resolution)
    {

        return mapper.getAllData(resolution);
    }
}