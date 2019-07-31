package cn.ehanghai.routecalc.nav.mapper;


import cn.ehanghai.routecalc.nav.domain.BaseLineAll;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;

public interface BaseLineAllMapper extends Mapper<BaseLineAll>,InsertListMapper<BaseLineAll> {

    /**
     * 更新开始转向点编号
     *
     * @param oldCode
     * @param newCode
     * @return
     */
    @Update("update nav_t_base_line_all_test set start_code = #{param2} where start_code = #{param1}")
    int updateStartCode(String oldCode, String newCode);

    /**
     * 更新结束转向点编号
     *
     * @param oldCode
     * @param newCode
     * @return
     */
    @Update("update nav_t_base_line_all_test set end_code = #{param2} where end_code = #{param1}")
    int updateEndCode(String oldCode, String newCode);


    @Select("select * from nav_t_base_line_all_test limit #{num}")
    @ResultMap("baselineallmap")
    List<BaseLineAll> getBaseLineByLimitNum(int num);


    @Select("SELECT * from nav_t_base_line_all_test  where valid=1")
    @ResultMap("baselineallmap")
    List<BaseLineAll> getAllData();


    @Delete("truncate table  nav_t_base_line_all_test ")
    void clean();
}
