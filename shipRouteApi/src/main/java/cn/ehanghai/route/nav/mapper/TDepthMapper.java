package cn.ehanghai.route.nav.mapper;

import cn.ehanghai.route.nav.domain.TDepth;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;

public interface TDepthMapper extends Mapper<TDepth>,InsertListMapper<TDepth>{

    @Insert("INSERT INTO TDepth  (lon,lat,depth,geohash,resolution ) VALUES(#{param1},#{param2},#{param3},#{param4},#{param5} )")
    int insertdepth(double lon, double lat, double depth, String geohash, String resolution);

    @Select("SELECT * from TDepth where geohash like '${param1}%' and resolution='${param2}'")
    List<TDepth> getNearPoints(String geohash, String resolution);

    @Select("SELECT * from TDepth where  resolution=#{resolution}")
    List<TDepth> getAllData(String resolution);
}
