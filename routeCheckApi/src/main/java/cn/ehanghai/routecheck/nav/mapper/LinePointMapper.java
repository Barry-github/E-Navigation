/**
 * 
 */
package cn.ehanghai.routecheck.nav.mapper;


import cn.ehanghai.routecheck.nav.domain.LinePoint;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
* @author 胡恒博 E-mail:huhb@grassinfo.cn
* @version 创建时间：2017年7月21日 上午9:03:28
* 转折点Mapper
*/
public interface LinePointMapper extends Mapper<LinePoint> {
	@Select("select * from nav_t_line_point_test where code in (select end_code from nav_t_base_line_test where start_code=#{startCode} and valid = 1)")
	List<LinePoint> getPointChoosed(String startCode);
	
	@Select("delete from nav_t_base_line_test where start_code = #{code} or end_code=#{code}")
	void deleteBaseLine(String code);
	
	@Select("select id from nav_t_line_point_test where valid=1 order by id desc limit 1")
	Long getLatestId();

	@Select("SELECT * from nav_t_line_point_test where ABS(lon- #{param1})<1e-5 and ABS(lat-#{param2})<1e-5")
	//@ResultMap("linepointmap")
	List<LinePoint> getEqualPoints(Double lon, Double lat);

}
