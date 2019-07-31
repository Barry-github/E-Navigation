package cn.ehanghai.route.nav.mapper;

import cn.ehanghai.route.nav.domain.BaseLine;
import cn.ehanghai.route.nav.domain.ErrorLine;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ErrorLineMapper extends Mapper<ErrorLine> {

    @Select("select * from nav_t_errorline_info")
    List<ErrorLine> getAllData();

    @Delete("delete from nav_t_errorline_info where id = #{errorId}")
    int deleteErrorLine(int errorId );

    @Delete("delete from nav_t_errorline_info")
    int deleteAll();
}
