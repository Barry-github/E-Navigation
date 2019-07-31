/**
 *
 */
package cn.ehanghai.routecalc.nav.service;


import cn.ehanghai.routecalc.common.service.BaseService;
import cn.ehanghai.routecalc.nav.domain.LinePoint;
import cn.ehanghai.routecalc.nav.mapper.LinePointMapper;
import net.ryian.commons.StringUtils;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.text.DecimalFormat;
import java.util.*;

/**
 * @author 胡恒博 E-mail:huhb@grassinfo.cn
 * @version 创建时间：2017年7月21日 上午9:03:45
 * 类说明
 */
@Component
public class LinePointService extends BaseService<LinePoint, LinePointMapper> {

//    private BaseLineMapper baseLineMapper;

    public List<LinePoint> query(Map<String, String> paramMap) {

//        baseLineMapper.insertList()
        Example example = new Example(LinePoint.class);
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

    public LinePoint getPointByCode(String code) {
        LinePoint point = new LinePoint();
        point.setValid(1);
        point.setCode(code);
        return mapper.selectOne(point);
    }



    public LinePoint getPointByLonLat(Double lat, Double lon) {

        List<LinePoint>  linePoints= mapper.getEqualPoints(lon,lat);

        if(linePoints.size()>0)
        {
            return linePoints.get(0);

        }
        return null;

    }


    public  void insertList(List<LinePoint> points)
    {
        if(points.size()>0)
        mapper.insertList(points);
    }

    public   List<LinePoint> getAllData()
    {
        return  mapper.getAllData();
    }


    public   void clean()
    {
        mapper.clean();
    }



    public List<LinePoint> getNearPoints(String hash)
    {

       // hash="'"+hash+"%'";
        return  mapper.getNearPoints(hash);
    }
    public List<LinePoint> getAreaPoints(String areaCode)
    {
        return  mapper.getAreaPoints(areaCode);
    }

    public static void main(String[] args) {
        Long maxId = 1L;
        DecimalFormat df  = new DecimalFormat("0000000");
        String value = df.format(maxId);
        System.out.println("value的值是：---" + value + "，当前方法=LinePointService.main()");

    }


}
