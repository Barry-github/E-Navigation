package cn.ehanghai.route.nav.service;


import cn.ehanghai.route.common.service.BaseService;
import cn.ehanghai.route.nav.domain.NavLineInfo;
import cn.ehanghai.route.nav.mapper.NavLineInfoMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *  航线详细信息
 * @author 胡恒博
 **/
@Component
public class NavLineInfoService extends BaseService<NavLineInfo, NavLineInfoMapper> {

    /**
     * 根据航线ID获取航线
     *
     * @param lineId
     * @return
     */
    public List<NavLineInfo> getNavLineInfoListByLineId(Long lineId) {
        return mapper.getNavLineInfoListByLineId(lineId);
    }

    public void insertList(List<NavLineInfo> list) {
//        Example example = new Example(NavLineInfoTest.class);
//        example.createCriteria().andEqualTo("valid",1)
//                .andEqualTo("lineId",lineId);
//        mapper.deleteByExample(example);
        mapper.insertList(list);
    }
}
