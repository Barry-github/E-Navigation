/**
 *
 */
package cn.ehanghai.route.nav.service;


import cn.ehanghai.route.common.service.BaseService;
import cn.ehanghai.route.common.utils.LonLatUtil;
import cn.ehanghai.route.nav.domain.BaseLine;
import cn.ehanghai.route.nav.domain.LinePoint;
import cn.ehanghai.route.nav.mapper.BaseLineMapper;
import cn.ehanghai.route.nav.mapper.LinePointMapper;
import net.ryian.commons.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private BaseLineMapper baseLineMapper;

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

    public LinePoint getPointByLonLat(Double lat, Double lon) {

        List<LinePoint>  linePoints= mapper.getEqualPoints(lon,lat);

        if(linePoints.size()>0)
        {
            return linePoints.get(0);

        }
        return null;

    }

    public    List<LinePoint>  getBaseLineByLimitNum(int num)
    {
        List<LinePoint>  linePoints=new ArrayList<>();
                List<BaseLine> baseLines= baseLineMapper.getBaseLineByLimitNum(num);
                Map<String,Boolean>  codemap=new HashMap<>();
        LinePoint point;
        for (BaseLine line :
                baseLines) {
            String code=line.getStartCode();
            if(!codemap.keySet().contains(code))
            {
                point= getPointByCode(code);
                linePoints.add(point);
                codemap.put(code,true);
            }

            code=line.getStartCode();
            if(!codemap.keySet().contains(code))
            {
                point= getPointByCode(code);
                linePoints.add(point);
                codemap.put(code,true);
            }
        }

        return linePoints;

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


    public void DeletePoiHarbour()
    {
        mapper.DeletePoiHarbour();
    }




}
