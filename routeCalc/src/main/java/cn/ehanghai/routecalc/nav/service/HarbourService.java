package cn.ehanghai.routecalc.nav.service;


import cn.ehanghai.routecalc.common.service.BaseService;
import cn.ehanghai.routecalc.nav.domain.Harbour;
import cn.ehanghai.routecalc.nav.mapper.HarbourMapper;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author 胡恒博
 */
@Component
public class HarbourService extends BaseService<Harbour, HarbourMapper> {
    public List<Harbour> getHarboursByPid(Long pid) {
        Harbour harbour = new Harbour();
        harbour.setPid(pid);
        return mapper.select(harbour);
    }

    public List<Harbour> getHarbours() {
        Example example = new Example(Harbour.class);
        example.createCriteria().andEqualTo("valid", 1);
//                .andNotEqualTo("pid", 0);
        return mapper.selectByExample(example);
    }


    public Harbour getHarbourByCode(String linePointCode) {
        Harbour harbour = new Harbour();
        harbour.setLinePointCode(linePointCode);
        harbour.setValid(1);
        return mapper.selectOne(harbour);
    }

    public void insertList(List<Harbour>harbours)
    {
        if(harbours.size()>0)
            mapper.insertList(harbours);
    }

    public   List<Harbour> getAllData()
    {
        return  mapper.getAllData();
    }


    public   void clean()
    {
        mapper.clean();
    }
}
