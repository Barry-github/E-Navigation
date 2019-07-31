package cn.ehanghai.route.nav.mapper;

import cn.ehanghai.route.nav.domain.BaseLine;
import cn.ehanghai.route.nav.domain.OperationRecord;
import org.apache.ibatis.annotations.Insert;
import tk.mybatis.mapper.common.Mapper;

public interface OperationRecordMapper extends Mapper<OperationRecord> {

    @Insert("insert into nav_t_operation_record (userId,action,description,time) VALUES( #{param1},#{param2},#{param3},#{param4} )")
    int insertOperationRecord(Long userId,String action,String  description,String time);
}
