package cn.ehanghai.routecheck.nav.mapper;

import cn.ehanghai.routecheck.nav.domain.WaterDepth;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;


public interface WaterDepthMapper  extends Mapper<WaterDepth>, InsertListMapper<WaterDepth> {

    @Insert("INSERT INTO nav_t_water_depth  (lon,lat,depth,geohash,resolution ) VALUES(#{param1},#{param2},#{param3},#{param4},#{param5} )")
    int insertdepth(double lon, double lat, double depth, String geohash, String resolution);

    @Select("SELECT * from nav_t_water_depth where geohash like '${param1}%' and resolution='${param2}'")
    List<WaterDepth> getNearPoints(String geohash, String resolution);

    @Select("SELECT * from nav_t_water_depth where  resolution=#{resolution}")
    List<WaterDepth> getAllData(String resolution);
}