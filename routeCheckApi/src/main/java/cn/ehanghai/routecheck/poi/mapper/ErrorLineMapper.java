package cn.ehanghai.routecheck.poi.mapper;

import cn.ehanghai.routecheck.poi.domain.ErrorLine;

import org.apache.ibatis.annotations.Insert;
import tk.mybatis.mapper.common.Mapper;



public interface ErrorLineMapper extends Mapper<ErrorLine> {


    @Insert("insert into nav_t_errorline_info (startCode,endCode,time,errorMsg) VALUES( #{param1},#{param2},#{param3},#{param4} )")
    int InsertErrorInfo(String startCode,String endCode,String time,String errorMsg);

}
