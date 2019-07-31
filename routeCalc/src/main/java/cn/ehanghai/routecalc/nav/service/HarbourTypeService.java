package cn.ehanghai.routecalc.nav.service;

import cn.ehanghai.routecalc.common.service.BaseService;
import cn.ehanghai.routecalc.nav.domain.HarbourType;
import cn.ehanghai.routecalc.nav.mapper.HarbourTypeMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HarbourTypeService extends BaseService<HarbourType, HarbourTypeMapper> {

    public   List<HarbourType> getAll()
    {

        return  mapper.getAll();
    }

    public  HarbourType getType(String name)
    {
        return  mapper.getType(name);
    }
}
