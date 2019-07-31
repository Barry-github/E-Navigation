package cn.ehanghai.routecalc.nav.mapper;

import cn.ehanghai.routecalc.nav.domain.LinePoint;
import cn.ehanghai.routecalc.nav.domain.RouteArea;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;

public interface RouteAreaMapper  extends Mapper<RouteArea>, InsertListMapper<RouteArea> {


    @Select("SELECT * from nav_t_route_area where `code`=#{code}")
    @ResultMap("routeareamap")
    RouteArea getAreaByCode(String code);

    @Select("SELECT * from nav_t_route_area")
    @ResultMap("routeareamap")
    List<RouteArea> getAllData();


}
