package cn.ehanghai.route.nav.mapper;

import cn.ehanghai.route.nav.domain.RouteLineCheck;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;

public interface RouteLineCheckMapper extends Mapper<RouteLineCheck>, InsertListMapper<RouteLineCheck>
{
    @Select("SELECT * from nav_t_route_line_check where line_check=FALSE order by id limit 2000")
    @ResultMap("routelinecheckmap")
    List<RouteLineCheck> getUnCheckLines();

    @Update("UPDATE  nav_t_route_line_check SET line_check=TRUE WHERE line_id=#{lineId}")
    int deleteData(String lineId);

}


