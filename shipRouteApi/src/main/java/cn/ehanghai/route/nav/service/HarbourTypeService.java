package cn.ehanghai.route.nav.service;

import cn.ehanghai.route.common.service.BaseService;
import cn.ehanghai.route.nav.domain.HarbourType;
import cn.ehanghai.route.nav.mapper.HarbourTypeMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HarbourTypeService extends BaseService<HarbourType, HarbourTypeMapper> {

    public   List<HarbourType> getAll()
    {

        return  mapper.getAll();
    }
}
