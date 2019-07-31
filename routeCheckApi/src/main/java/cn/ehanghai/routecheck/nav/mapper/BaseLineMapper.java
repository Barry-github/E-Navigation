package cn.ehanghai.routecheck.nav.mapper;



import cn.ehanghai.routecheck.nav.domain.BaseLine;
import cn.ehanghai.routecheck.nav.domain.BaseLineAll;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author 胡恒博
 */
public interface BaseLineMapper extends Mapper<BaseLine> {

    /**
     * 更新开始转向点编号
     * @param oldCode
     * @param newCode
     * @return
     */
    @Update("update nav_t_base_line_test set start_code = #{param2} where start_code = #{param1}")
    int updateStartCode(String oldCode, String newCode);

    /**
     * 更新结束转向点编号
     * @param oldCode
     * @param newCode
     * @return
     */
    @Update("update nav_t_base_line_test set end_code = #{param2} where end_code = #{param1}")
    int updateEndCode(String oldCode, String newCode);


    @Select("SELECT * from nav_t_base_line_test WHERE draught=-9999.0")
    @ResultMap("baselinemap")
    List<BaseLine> getNoDepths();
}
