package cn.ehanghai.routecalc.nav.mapper;

import cn.ehanghai.routecalc.nav.domain.HarbourType;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface HarbourTypeMapper extends Mapper<HarbourType>, InsertListMapper<HarbourType> {

    @Select("select * from nav_t_harbour_type")
    List<HarbourType> getAll();

    @Select("select * from nav_t_harbour_type where name=#{name}")
    HarbourType getType(String name);
}
