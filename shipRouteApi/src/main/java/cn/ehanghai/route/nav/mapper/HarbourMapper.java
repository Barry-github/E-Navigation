package cn.ehanghai.route.nav.mapper;


import cn.ehanghai.route.nav.domain.BaseLine;
import cn.ehanghai.route.nav.domain.Harbour;
import cn.ehanghai.route.nav.domain.LinePoint;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;

/**
 * @author 胡恒博
 */
public interface HarbourMapper extends Mapper<Harbour> ,InsertListMapper<Harbour> {

    @Select("SELECT * from nav_t_harbour_test  where valid=1 ")
    @ResultMap("harbourmap")
    List<Harbour> getAllData();


    @Delete("truncate table  nav_t_harbour_test ")
    void clean();

    @Delete("Delete from nav_t_harbour_test Where line_point_code>'A-0100000' and line_point_code<'A-0200000'")
    void DeletePoiHarbour();
}
