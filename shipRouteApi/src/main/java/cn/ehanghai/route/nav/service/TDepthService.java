package cn.ehanghai.route.nav.service;

import cn.ehanghai.route.nav.domain.TDepth;
import cn.ehanghai.route.nav.mapper.TDepthMapper;
import net.ryian.commons.GenericsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TDepthService {

    @Autowired
    protected TDepthMapper mapper;

    protected Class<TDepth> entityClass;

    public TDepthService() {
        entityClass = GenericsUtils.getSuperClassGenricType(getClass());
    }


    public  void saveList(List<TDepth> list)
    {
        mapper.insertList(list);
//        for (WaterDepth data :
//                list) {
//            mapper.insertdepth(data.getLon(),data.getLat(),data.getDepth(),data.getGeohash(),data.getResolution());
//        }
    }

    public List<TDepth> getNearPoints(String geohash,String resolution)
    {
        return mapper.getNearPoints(geohash,resolution);
    }

    public List<TDepth> getAllData(String resolution)
    {

        return mapper.getAllData(resolution);
    }
}
