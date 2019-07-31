package cn.ehanghai.routecheck.nav.service;

import cn.ehanghai.routecheck.common.service.BaseService;
import cn.ehanghai.routecheck.nav.domain.BaseLine;
import cn.ehanghai.routecheck.nav.domain.BaseLineAll;
import cn.ehanghai.routecheck.nav.mapper.BaseLineMapper;
import net.ryian.commons.StringUtils;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * @author 胡恒博
 **/
@Component
public class NavBaseLineService extends BaseService<BaseLine, BaseLineMapper> {

    @Override
    public List<BaseLine> query(Map<String, String> paramMap) {


        return super.query(paramMap);
    }

    /**
     * 检查转向点是否有未删除的航段在
     *
     * @param code
     * @return
     */
    public boolean checkPointHasLine(String code) {
        Example example = new Example(BaseLine.class);
        example.createCriteria().andEqualTo("valid", 1)
                .andEqualTo("startCode", code);
        List<BaseLine> baseLineTests = mapper.selectByExample(example);

        if (baseLineTests == null || baseLineTests.isEmpty()) {
            example.createCriteria().andEqualTo("valid", 1)
                    .andEqualTo("endCode", code);
            baseLineTests = mapper.selectByExample(example);
        }
        if (baseLineTests != null && !baseLineTests.isEmpty()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 转向点编号变化时变更航段编号
     * @param oldCode
     * @param newCode
     */
    public void updateCodeByOldCode(String oldCode, String newCode) {
        mapper.updateEndCode(oldCode, newCode);
        mapper.updateStartCode(oldCode, newCode);
    }
    public BaseLine getBaseLineByStartAndEnd(String startCode, String endCode) {
        BaseLine baseLineTest = new BaseLine();
        baseLineTest.setValid(1);
        baseLineTest.setStartCode(startCode);
        baseLineTest.setEndCode(endCode);
        BaseLine result= mapper.selectOne(baseLineTest);
        if(result==null){
            baseLineTest = new BaseLine();
            baseLineTest.setValid(1);
            baseLineTest.setStartCode(endCode);
            baseLineTest.setEndCode(startCode);
            baseLineTest.setOneWayStreet(0);
            result= mapper.selectOne(baseLineTest);
        }
        else
        {
            if(result.getOneWayStreet().intValue()==-1)
            {
                return null;
            }
        }

        return result;
    }

    public List<BaseLine> getBaseLineListByParam(Map<String, String> paramMap) {
        if (paramMap == null) {
            return getAll();
        }
        Example example = new Example(entityClass);
        Example.Criteria criteria = example.createCriteria().andEqualTo("valid", 1);
        //类型0普通航线,1内航线，2外航线,3 中航线，4内河航线
        String type = paramMap.get("type");
        //最大吃水
        String draught = paramMap.get("draught");
        //最大高度
        String high = paramMap.get("high");
        //最大吃水
        String tonnage = paramMap.get("tonnage");
        if (!StringUtils.isEmpty(type)) {
            criteria.andEqualTo("type", type);
        }
        if (!StringUtils.isEmpty(draught)) {
            criteria.andGreaterThanOrEqualTo("draught", draught);
        }
        if (!StringUtils.isEmpty(high)) {
            criteria.andGreaterThanOrEqualTo("high", high);
        }
        if (!StringUtils.isEmpty(tonnage)) {
            criteria.andGreaterThanOrEqualTo("tonnage", tonnage);
        }
        return mapper.selectByExample(example);
    }

    public  List<BaseLine> getNoDepths(){return mapper.getNoDepths();}
}
