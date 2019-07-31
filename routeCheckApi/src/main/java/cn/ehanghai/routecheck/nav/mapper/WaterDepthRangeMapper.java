package cn.ehanghai.routecheck.nav.mapper;

import cn.ehanghai.routecheck.nav.domain.WaterDepth;
import cn.ehanghai.routecheck.nav.domain.WaterDepthRange;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;

public interface WaterDepthRangeMapper  extends Mapper<WaterDepthRange>, InsertListMapper<WaterDepthRange> {

    @Select("SELECT * from nav_t_water_depth_range where  resolution=#{resolution}")
    List<WaterDepthRange> getAllData(String resolution);


}
