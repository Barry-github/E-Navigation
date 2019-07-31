package cn.ehanghai.route.sys.service;


import cn.ehanghai.route.common.service.BaseService;
import cn.ehanghai.route.sys.domain.Param;
import cn.ehanghai.route.sys.mapper.ParamMapper;
import net.ryian.commons.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * Created by allenwc on 16/3/10.
 */
@Service
public class ParamService extends BaseService<Param, ParamMapper> {

    /**
     * 根据条件查询分页
     *
     * @param paramMap
     * @return
     */
    @Override
    public List<Param> query(Map<String, String> paramMap) {
        Example example = new Example(Param.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("valid", "1");
        String name = paramMap.get("name");
        if (!StringUtils.isEmpty(name)) {
            criteria.andLike("name", "%" + name + "%");
        }
        String value = paramMap.get("value");
        if (!StringUtils.isEmpty(value)) {
            criteria.andLike("value", "%" + value + "%");
        }
        return mapper.selectByExample(example);
    }

    public Param getValue(String code) {
        Param param = new Param();
        param.setValid(1);
        param.setName(code);
        return mapper.selectOne(param);
    }

}
