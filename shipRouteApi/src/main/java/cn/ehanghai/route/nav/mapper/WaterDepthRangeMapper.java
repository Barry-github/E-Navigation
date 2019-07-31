package cn.ehanghai.route.nav.mapper;

import cn.ehanghai.route.nav.domain.WaterDepthRange;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;


public interface WaterDepthRangeMapper extends Mapper<WaterDepthRange>, InsertListMapper<WaterDepthRange> {

    @Select("SELECT * from nav_t_water_depth_range where  resolution=#{resolution}")
    @ResultMap("waterdepthrangemap")
    List<WaterDepthRange> getAllData(String resolution);


}
