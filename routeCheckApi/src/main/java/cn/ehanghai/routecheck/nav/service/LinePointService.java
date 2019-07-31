/**
 *
 */
package cn.ehanghai.routecheck.nav.service;


import cn.ehanghai.routecheck.common.service.BaseService;
import cn.ehanghai.routecheck.common.utils.LonLatUtil;
import cn.ehanghai.routecheck.nav.domain.BaseLine;
import cn.ehanghai.routecheck.nav.domain.LinePoint;
import cn.ehanghai.routecheck.nav.domain.WaterDepth;
import cn.ehanghai.routecheck.nav.mapper.BaseLineMapper;
import cn.ehanghai.routecheck.nav.mapper.LinePointAllMapper;
import cn.ehanghai.routecheck.nav.mapper.LinePointMapper;
import net.ryian.commons.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 胡恒博 E-mail:huhb@grassinfo.cn
 * @version 创建时间：2017年7月21日 上午9:03:45
 * 类说明
 */
@Component
public class LinePointService extends BaseService<LinePoint, LinePointMapper> {
    @Autowired
    private BaseLineMapper baseLineMapper;

    public List<LinePoint> query(Map<String, String> paramMap) {

        Example example = new Example(LinePoint.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("valid", "1");
        String name = paramMap.get("name");
        if (!StringUtils.isEmpty(name)) {
            criteria.andLike("name", "%" + name + "%");
        }
        return mapper.selectByExample(example);
    }

    public String getPointCode() {
        Long maxId = mapper.getLatestId();
        DecimalFormat df  = new DecimalFormat("0000000");
        if (maxId == null) {
            return "E-000001";
        }
        return "E-" + df.format(maxId);
    }

    public LinePoint getPointByCode(String code) {
        LinePoint point = new LinePoint();
        point.setValid(1);
        point.setCode(code);
        return mapper.selectOne(point);
    }

    public void saveBaseLines(String startCode, String codes) {
        LinePoint pointA = this.getPointByCode(startCode);
        mapper.deleteBaseLine(startCode);
        for (String code : codes.split(",")) {
            LinePoint pointB = this.getPointByCode(code);
            Long distance = Math.round(LonLatUtil.getDistance(pointA.getLon(), pointA.getLat(), pointB.getLon(), pointB.getLat()));
            BaseLine line = new BaseLine();
            line.setStartCode(startCode);
            line.setEndCode(code);
            line.setDistance(distance);
            line.setCreateDate(new Date());
            line.setUpdateDate(new Date());
            line.setValid(1);
            baseLineMapper.insertSelective(line);
            line = new BaseLine();
            line.setDistance(distance);
            line.setCreateDate(new Date());
            line.setUpdateDate(new Date());
            line.setValid(1);
            line.setStartCode(code);
            line.setEndCode(startCode);
            baseLineMapper.insertSelective(line);
        }
    }

    public boolean getPointByLonLat(Double lat, Double lon) {

        List<LinePoint>  linePoints= mapper.getEqualPoints(lon,lat);

        return linePoints.size()>0;

    }
    public static void main(String[] args) {
        Long maxId = 1L;
        DecimalFormat df  = new DecimalFormat("0000000");
        String value = df.format(maxId);
        System.out.println("value的值是：---" + value + "，当前方法=LinePointService.main()");

    }


}
