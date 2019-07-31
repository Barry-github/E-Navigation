package cn.ehanghai.route.nav.mapper;

import cn.ehanghai.route.nav.domain.HarbourType;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;

public interface HarbourTypeMapper extends Mapper<HarbourType>, InsertListMapper<HarbourType> {

    @Select("select * from nav_t_harbour_type")
    List<HarbourType> getAll();
}
