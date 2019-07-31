package cn.ehanghai.routecheck.poi.service;

import cn.ehanghai.routecheck.common.service.BaseService;
import cn.ehanghai.routecheck.poi.domain.ErrorLine;
import cn.ehanghai.routecheck.poi.mapper.ErrorLineMapper;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;


@Component
public class ErrorLineService extends BaseService<ErrorLine, ErrorLineMapper> {

    public void Insert(ErrorLine errorLine)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        mapper.InsertErrorInfo(errorLine.getStartCode(),errorLine.getEndCode(),time,errorLine.getErrorMsg());
    }
}
