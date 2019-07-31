package cn.ehanghai.route.nav.service;

import cn.ehanghai.route.common.service.BaseService;
import cn.ehanghai.route.nav.domain.ErrorLine;
import cn.ehanghai.route.nav.domain.Harbour;
import cn.ehanghai.route.nav.mapper.ErrorLineMapper;
import cn.ehanghai.route.nav.mapper.HarbourMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ErrorLineService extends BaseService<ErrorLine, ErrorLineMapper> {


    public List<ErrorLine> getAllData()
    {
        return mapper.getAllData();
    }

    public  int deleteErrorLine(int errorId )
    {
        return mapper.deleteErrorLine(errorId);
    }

    public  int deleteAll(){return mapper.deleteAll();}
}
