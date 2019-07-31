package cn.ehanghai.routecalc.nav.mapper;


import cn.ehanghai.routecalc.nav.domain.BaseLine;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;

/**
 * @author 胡恒博
 */
public interface BaseLineMapper extends Mapper<BaseLine>,InsertListMapper<BaseLine> {

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


    @Select("select * from nav_t_base_line_test limit #{num}")
    @ResultMap("baselinemap")
    List<BaseLine> getBaseLineByLimitNum(int num);


    @Select("SELECT * from nav_t_base_line_test  where valid=1 ")
    @ResultMap("baselinemap")
    List<BaseLine> getAllData();


    @Delete("truncate table  nav_t_base_line_test ")
    void clean();

    @Select("SELECT  DISTINCT `name`  from nav_t_base_line_test where lane=1 and `name`<>''")
    List<String> getLaneNames();
}
