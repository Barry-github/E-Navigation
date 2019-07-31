package cn.ehanghai.routecheck.poi.mapper;

import cn.ehanghai.routecheck.poi.domain.PoiInfo;
import org.apache.ibatis.annotations.*;

import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface  PoiMapper extends Mapper<PoiInfo> {


@Select("SELECT info.`name`,info.path,info.others,type.type from nav_focus_t_poi info,nav_focus_t_type type where info.`name` like '%${name}%' and  info.type_id=type.id")
    //@Select("SELECT * from nav_focus_t_poi where name like '%${name}%' ")

    List<PoiInfo> getPoiByName(@Param(value="name")String name);
}
