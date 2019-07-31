package cn.ehanghai.route.nav.mapper;

import cn.ehanghai.route.nav.domain.NavLineInfo;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;

/**
 * @program: app-trunk
 * @description: ${description}
 * @author: 胡恒博
 * @create: 2018-03-19 14:36
 **/
public interface NavLineInfoMapper extends Mapper<NavLineInfo>,InsertListMapper<NavLineInfo> {
    /**
     * 根据航线ID获取详细信息
     * @param lineId
     * @return
     */
    @Select("SELECT info.line_point_code linePointCode,info.distance,info.line_id lineId,info.sort" +
            ",point.lat,point.lon FROM nav_t_line_info info ,nav_t_line_point point " +
            "WHERE  info.valid = 1 AND point.valid = 1 AND info.line_id = #{lineId} " +
            "AND info.line_point_code = point.code order by info.sort")
    List<NavLineInfo> getNavLineInfoListByLineId(Long lineId);
}
