package cn.ehanghai.routecheck.nav.service;

import cn.ehanghai.routecheck.common.service.BaseService;
import cn.ehanghai.routecheck.nav.domain.LinePointAll;
import cn.ehanghai.routecheck.nav.mapper.LinePointAllMapper;
import net.ryian.commons.StringUtils;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;


@Component
public class LinePointAllService extends BaseService<LinePointAll, LinePointAllMapper> {

//    private BaseLineMapper baseLineMapper;

    public List<LinePointAll> query(Map<String, String> paramMap) {

//        baseLineMapper.insertList()
        Example example = new Example(LinePointAll.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("valid", "1");
        String name = paramMap.get("name");
        if (!StringUtils.isEmpty(name)) {
            criteria.andLike("name", "%" + name + "%");
        }
        return mapper.selectByExample(example);
    }

    public Long getMaxId()
    {
        String maxCode=   mapper.getMaxCode();
        if(maxCode==null||maxCode.equals(""))  return 1L;

        String []items=maxCode.split("-");
        if(items.length==2)
        {
            Long maxId=   Long.parseLong(items[1]);
            return  maxId+1;
        }
//        Long maxId = mapper.getLatestId();
//        if (maxId == null) {
//            return 1L;
//        }
        return 1L;
    }

    public String toCode(Long maxId)
    {
        DecimalFormat df  = new DecimalFormat("0000000");
        return "E-" + df.format(maxId);

    }

    public String getPointCode() {
        Long maxId = getMaxId();
        return toCode( maxId);
    }

    public LinePointAll getPointByCode(String code) {
        LinePointAll point = new LinePointAll();
        point.setValid(1);
        point.setCode(code);
        return mapper.selectOne(point);
    }



    public LinePointAll getPointByLonLat(Double lat, Double lon) {

        List<LinePointAll>  linePoints= mapper.getEqualPoints(lon,lat);

        if(linePoints.size()>0)
        {
            return linePoints.get(0);

        }
        return null;

    }


    public  void insertList(List<LinePointAll> points)
    {
        if(points.size()>0)
            mapper.insertList(points);
    }

    public   List<LinePointAll> getAllData()
    {
        return  mapper.getAllData();
    }


    public   void clean()
    {
        mapper.clean();
    }



    public List<LinePointAll> getNearPoints(String hash)
    {

        // hash="'"+hash+"%'";
        return  mapper.getNearPoints(hash);
    }


    public static void main(String[] args) {
        Long maxId = 1L;
        DecimalFormat df  = new DecimalFormat("0000000");
        String value = df.format(maxId);
        System.out.println("value的值是：---" + value + "，当前方法=LinePointService.main()");

    }


}
