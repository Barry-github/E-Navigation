package cn.ehanghai.routecheck.nav.service;

import cn.ehanghai.routecheck.common.service.BaseService;
import cn.ehanghai.routecheck.nav.domain.BaseLine;
import cn.ehanghai.routecheck.nav.domain.BaseLineAll;
import cn.ehanghai.routecheck.nav.mapper.BaseLineAllMapper;
import net.ryian.commons.StringUtils;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BaseLineAllService extends BaseService<BaseLineAll, BaseLineAllMapper> {

    @Override
    public List<BaseLineAll> query(Map<String, String> paramMap) {


        return super.query(paramMap);
    }

    /**
     * 检查转向点是否有未删除的航段在
     *
     * @param code
     * @return
     */
    public boolean checkPointHasLine(String code) {
        Example example = new Example(BaseLineAll.class);
        example.createCriteria().andEqualTo("valid", 1)
                .andEqualTo("startCode", code);
        List<BaseLineAll> baseLineTests = mapper.selectByExample(example);

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
    public BaseLineAll getBaseLineByStartAndEnd(String startCode, String endCode) {
        BaseLineAll baseLineTest = new BaseLineAll();
        baseLineTest.setValid(1);
        baseLineTest.setStartCode(startCode);
        baseLineTest.setEndCode(endCode);
        return mapper.selectOne(baseLineTest);
    }

    public List<BaseLineAll> getBaseLineListByParam(Map<String, String> paramMap) {
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

    public  List<BaseLineAll> getBaseLinesByCode(String code)
    {
        BaseLineAll baseLineTest = new BaseLineAll();
        baseLineTest.setValid(1);
        baseLineTest.setStartCode(code);
        List<BaseLineAll> baseLines=  mapper.select(baseLineTest);


        baseLineTest = new BaseLineAll();
        baseLineTest.setValid(1);
        baseLineTest.setEndCode(code);
        List<BaseLineAll> tmpdatas=  mapper.select(baseLineTest);

        if(baseLines==null) baseLines=new ArrayList<>();
        if(tmpdatas!=null)
        {
            baseLines.addAll(tmpdatas);
        }

        return baseLines;
    }

    public boolean lineExist(String code1,String code2)
    {
        BaseLineAll baseLine=   getBaseLineByStartAndEnd(code1,code2);
        if(baseLine!=null) return true;
        baseLine=   getBaseLineByStartAndEnd(code2,code1);
        if(baseLine!=null) return true;
        return false;
    }

    public void insertList(List<BaseLineAll>lines)
    {
        if(lines.size()>0)
            mapper.insertList(lines);
    }


    public   List<BaseLineAll> getAllData()
    {
        return  mapper.getAllData();
    }

    public  List<BaseLineAll> getNoDepths(){return mapper.getNoDepths();}

    public  void deleteNodepths()
    {
        mapper.deleteNodepth();
    }
    public   void clean()
    {
        mapper.clean();
    }

}
