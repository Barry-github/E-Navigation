package cn.ehanghai.route.nav.service;


import cn.ehanghai.route.common.service.BaseService;
import cn.ehanghai.route.nav.domain.LinePoint;
import cn.ehanghai.route.nav.domain.NavLine;
import cn.ehanghai.route.nav.domain.NavLineInfo;
import cn.ehanghai.route.nav.mapper.LinePointMapper;
import cn.ehanghai.route.nav.mapper.NavLineInfoMapper;
import cn.ehanghai.route.nav.mapper.NavLineMapper;
import net.ryian.commons.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * 航线服务
 *
 * @author 胡恒博
 **/
@Component
public class NavLineService extends BaseService<NavLine, NavLineMapper> {
    @Autowired
    private LinePointMapper linePointMapper;
    @Autowired
    private NavLineInfoMapper navLineInfoMapper;

    @Override
    public List<NavLine> query(Map<String, String> paramMap) {
        Example example = new Example(NavLine.class);
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(paramMap.get("startHarbourId"))) {
            criteria.andEqualTo("startHarbourId", paramMap.get("startHarbourId"));
        }
        if (!StringUtils.isEmpty(paramMap.get("endHarbourId"))) {
            criteria.andEqualTo("endHarbourId", paramMap.get("endHarbourId"));
        }
        criteria.andEqualTo("valid", 1);
        return mapper.selectByExample(example);
    }

    /**
     * 根据起始港口获取航线
     *
     * @param startHarbourId
     * @param endHarbourId
     * @return
     */
    public List<NavLine> searchNavLineByStartAndEnd(Long startHarbourId, Long endHarbourId, Long lineId) {
        Example example = new Example(NavLine.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("valid", 1);
        if (startHarbourId != null) {
            criteria.andEqualTo("startHarbourId", startHarbourId);
        }
        if (lineId != null) {
            criteria.andEqualTo("id", lineId);
        }
        if (endHarbourId != null) {
            criteria.andEqualTo("endHarbourId", endHarbourId);
        }
        List<NavLine> list = mapper.selectByExample(example);
        for (NavLine navLine : list) {
            Example lineInfoExample = new Example(NavLineInfo.class);
            Example.Criteria lineInfoExampleCriteria = lineInfoExample.createCriteria();
            lineInfoExampleCriteria.andEqualTo("valid", 1).andEqualTo("lineId", navLine.getId());
            lineInfoExample.setOrderByClause("sort");
            List<NavLineInfo> lineInfos = navLineInfoMapper.selectByExample(lineInfoExample);
            for (NavLineInfo lineInfo : lineInfos) {
                LinePoint point = new LinePoint();
                point.setValid(1);
                point.setCode(lineInfo.getLinePointCode());
                LinePoint point1 = linePointMapper.selectOne(point);
                lineInfo.setLon(point1.getLon().doubleValue());
                lineInfo.setLat(point1.getLat().doubleValue());
            }
            navLine.setNavLineInfos(lineInfos);
        }
        return list;
    }

}
