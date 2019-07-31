package cn.ehanghai.routecalc.nav.mapper;

import cn.ehanghai.routecalc.nav.domain.RouteLineCheck;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;

public interface RouteLineCheckMapper extends Mapper<RouteLineCheck>, InsertListMapper<RouteLineCheck>
{
    @Select("SELECT * from nav_t_route_line_check ")
    @ResultMap("routelinecheckmap")
    List<RouteLineCheck> getAllData();

    @Insert("INSERT INTO nav_t_route_line_check (line_id,line_type,path,line_check) VALUES  (#{param1},#{param2},#{param3},#{param4} )")
    int insertData(String lineId, int lineType, String path, boolean lineCheck);

    @Delete("DELETE FROM nav_t_route_line_check WHERE line_id=#{lineId}")
    int deleteData(String lineId);

    @Delete("truncate table  nav_t_route_line_check ")
    void clean();

    @Select("SELECT line_id FROM nav_t_route_line_check where line_check=1")
    List<String>getCheckedLineIds();

    @Delete("DELETE FROM nav_t_route_line_check WHERE line_check=0")
    void cleanExist();

}
