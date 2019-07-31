package cn.ehanghai.route.nav.service;

import cn.ehanghai.route.common.service.BaseService;
import cn.ehanghai.route.nav.domain.OperationRecord;
import cn.ehanghai.route.nav.mapper.OperationRecordMapper;
import org.springframework.stereotype.Component;

@Component
public class OperationRecordService extends BaseService<OperationRecord,OperationRecordMapper> {


    public  int insertOperationRecord(Long userId,String action,String  description,String time)
    {
        return mapper.insertOperationRecord(userId,action,description,time);
    }

}
