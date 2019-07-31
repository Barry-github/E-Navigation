/**
 *
 */
package cn.ehanghai.routecalc.nav.mapper;

import cn.ehanghai.routecalc.nav.domain.LinePoint;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;


/**
 * @author 胡恒博 E-mail:huhb@grassinfo.cn
 * @version 创建时间：2017年7月21日 上午9:03:28
 * 转折点Mapper
 */
public interface LinePointMapper extends Mapper<LinePoint>, InsertListMapper<LinePoint> {
    @Select("select * from nav_t_line_point_test where code in (select end_code from nav_t_base_line_test where start_code=#{startCode} and valid = 1)")
    List<LinePoint> getPointChoosed(String startCode);

    @Select("delete from nav_t_base_line_test where start_code = #{code} or end_code=#{code}")
    void deleteBaseLine(String code);

    @Select("select id from nav_t_line_point_test where valid=1 order by id desc limit 1")
    Long getLatestId();

    @Select("SELECT * from nav_t_line_point_test where ABS(lon- #{param1})<1e-5 and ABS(lat-#{param2})<1e-5")
    @ResultMap("linepointmap")
    List<LinePoint> getEqualPoints(Double lon, Double lat);


    @Select("SELECT * from nav_t_line_point_test  where valid=1 ")
    @ResultMap("linepointmap")
    List<LinePoint> getAllData();


    @Delete("truncate table  nav_t_line_point_test ")
    void clean();

    //@Select("SELECT * from nav_t_line_point_test where geohash like '${hash}%'")
    @Select("SELECT * from nav_t_line_point_test where geohash like #{hash}\"%\" ")
    @ResultMap("linepointmap")
    List<LinePoint> getNearPoints(String hash);



    @Select("SELECT MAX(`code`) `code`  from nav_t_line_point_test")
    String getMaxCode();

    @Select("SELECT * from nav_t_line_point_test point ,nav_t_route_area  area\n" +
            "WHERE point.valid=1 and   point.lon>=area.lon_min\n" +
            "and point.lon<=area.lon_max and point.lat>=area.lat_min and point.lat<=area.lat_max AND\n" +
            "area.`code`=#{areaCode} ")
    @ResultMap("linepointmap")
    List<LinePoint> getAreaPoints(String areaCode);


}
