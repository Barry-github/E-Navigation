package cn.ehanghai.routecheck.nav.mapper;

import cn.ehanghai.routecheck.nav.domain.LinePointAll;
import cn.ehanghai.routecheck.nav.domain.WaterDepth;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;

public interface LinePointAllMapper extends Mapper<LinePointAll>, InsertListMapper<LinePointAll> {
    @Select("select * from nav_t_line_point_all_test where code in (select end_code from nav_t_base_line_test where start_code=#{startCode} and valid = 1)")
    List<LinePointAll> getPointChoosed(String startCode);

    @Select("delete from nav_t_line_point_all_test where start_code = #{code} or end_code=#{code}")
    void deleteBaseLine(String code);

    @Select("select id from nav_t_line_point_all_test where valid=1 order by id desc limit 1")
    Long getLatestId();

    @Select("SELECT * from nav_t_line_point_all_test where ABS(lon- #{param1})<1e-5 and ABS(lat-#{param2})<1e-5")
    @ResultMap("linepointallmap")
    List<LinePointAll> getEqualPoints(Double lon, Double lat);


    @Select("SELECT * from nav_t_line_point_all_test where valid=1 ")
    @ResultMap("linepointallmap")
    List<LinePointAll> getAllData();


    @Delete("truncate table  nav_t_line_point_all_test ")
    void clean();

    //@Select("SELECT * from nav_t_line_point_test where geohash like '${hash}%'")
    @Select("SELECT * from nav_t_line_point_all_test where geohash like #{hash}\"%\" ")
    @ResultMap("linepointallmap")
    List<LinePointAll> getNearPoints(String hash);



    @Select("SELECT MAX(`code`) `code`  from nav_t_line_point_all_test")
    String getMaxCode();


}
