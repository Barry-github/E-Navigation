package cn.ehanghai.route.nav.action;


import cn.ehanghai.route.common.action.BaseApiAction;
import cn.ehanghai.route.common.constants.ResponseCode;
import cn.ehanghai.route.common.utils.ResponseBean;
import cn.ehanghai.route.common.utils.Tool;
import cn.ehanghai.route.nav.domain.*;
import cn.ehanghai.route.nav.service.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 胡恒博
 **/
@RestController
@RequestMapping("/nav/navNewLine")
public class NavNewLineAction extends BaseApiAction<NavLine, NavLineService> {

    @Autowired
    private LinePointService navLinePointService;
    @Autowired
    private HarbourService harbourService;
    @Autowired
    private NavBaseLineService navBaseLineService;


    @Autowired
    private LinePointAllService linePointAllService;

    @Autowired
    private BaseLineAllService baseLineAllService;

    @Autowired
    private RouteAreaService routeAreaService;

    @Autowired
    private HarbourTypeService harbourTypeService;

    /**
     * 获取航线编辑的数据
     *
     */
    @GetMapping("getNavPointAndLine")
    @RequiresAuthentication
    public ResponseBean getNavPointAndLine(HttpServletRequest request)
    {

        JSONObject result = new JSONObject();
        List<Harbour> harbours = harbourService.getHarbours();
        List<LinePoint> points = navLinePointService.getAll();
        List<BaseLine> baseLines =navBaseLineService.getAll();

//        List<LinePoint> points =linePointAllService.getAllData().stream().map(a->a.toLinePoint()).collect(Collectors.toList());
//        List<BaseLine> baseLines =baseLineAllService.getAllData().stream().map(a->a.toBaseLine()).collect(Collectors.toList());

        /**
         * 找出不是港口不和别的点连接的孤立点
         */

        baseLines =   baseLines.stream().filter(a->a.getValid()!=null&&a.getValid()==1).collect(Collectors.toList());
        points=points.stream().filter(a->a.getValid()!=null&&a.getValid()==1).collect(Collectors.toList());
        harbours=harbours.stream().filter(a->a.getValid()!=null&&a.getValid()==1).collect(Collectors.toList());

        NavPointAction.QueryAllData(harbours, points, baseLines);

        result.put("baseLines", JSONArray.parseArray(JSONObject.toJSONString(baseLines)));
        result.put("points", JSONArray.parseArray(JSONObject.toJSONString(points)));
        return new ResponseBean(ResponseCode.SUCCESS, result);
    }


    /**
     * 按照区域获取航线编辑的数据
     *
     */
    @GetMapping("getNavPointAndLine/{areaCode}")
    @RequiresAuthentication
    public ResponseBean getNavPointAndLine(HttpServletRequest request,@PathVariable("areaCode") String areaCode)
    {

        JSONObject result = new JSONObject();
        List<Harbour> harbours = harbourService.getHarbours();
        List<LinePoint> points = navLinePointService.getAreaPoints(areaCode);
        List<BaseLine> baseLines =navBaseLineService.getAll();

//        List<LinePoint> points =linePointAllService.getAllData().stream().map(a->a.toLinePoint()).collect(Collectors.toList());
//        List<BaseLine> baseLines =baseLineAllService.getAllData().stream().map(a->a.toBaseLine()).collect(Collectors.toList());

        /**
         * 找出不是港口不和别的点连接的孤立点
         */

        baseLines =   baseLines.stream().filter(a->a.getValid()!=null&&a.getValid()==1).collect(Collectors.toList());
        points=points.stream().filter(a->a.getValid()!=null&&a.getValid()==1).collect(Collectors.toList());
        harbours=harbours.stream().filter(a->a.getValid()!=null&&a.getValid()==1).collect(Collectors.toList());

        //区域过滤
        List<String> pointCodes=points.stream().map(a->a.getCode()).collect(Collectors.toList());
        harbours=  harbours.stream().filter(a->pointCodes.contains(a.getLinePointCode())).collect(Collectors.toList());
        baseLines =  baseLines.stream().filter(a->pointCodes.contains(a.getStartCode())&&pointCodes.contains(a.getEndCode())).collect(Collectors.toList());


        NavPointAction.QueryAllData(harbours, points, baseLines);

        result.put("baseLines", JSONArray.parseArray(JSONObject.toJSONString(baseLines)));
        result.put("points", JSONArray.parseArray(JSONObject.toJSONString(points)));
        return new ResponseBean(ResponseCode.SUCCESS, result);
    }


    /**
     *获取所有的区域code 信息
     */
    @GetMapping("getAreaCodes")
    @RequiresAuthentication
    public ResponseBean getAreaCodes(HttpServletRequest request) {
        List<RouteArea> routeAreas=  routeAreaService.getAllData();
        JSONObject result = new JSONObject();
        result.put("routeAreas", JSONArray.parseArray(JSONObject.toJSONString(routeAreas)));
        return new ResponseBean(ResponseCode.SUCCESS, result);
    }

    @GetMapping("getHarbourTypes")
    @RequiresAuthentication
    public ResponseBean getHarbourTypes(HttpServletRequest request) {
        List<HarbourType> harbourTypes=  harbourTypeService.getAll();
        List<HarbourType> types=new ArrayList<>();
        List<Integer> typevalues=  harbourTypes.stream().map(a->a.getType()).collect(Collectors.toList());
        List<List<Integer>> combinations=Tool.combinations(typevalues);
        for(List<Integer> combination:combinations)
        {
            Integer data=  Tool.toBinNum(combination);
            List<String>names=   combination.stream().map(a->harbourTypes.stream().filter(f->f.getType()==a).findFirst().get().getName()).collect(Collectors.toList());
            String name=String.join(",",names);

            types.add(new HarbourType(name,data));
        }


        types.add(new HarbourType("未知类型",-1));

        JSONObject result = new JSONObject();
        result.put("routeAreas", JSONArray.parseArray(JSONObject.toJSONString(types)));
        return new ResponseBean(ResponseCode.SUCCESS, result);
    }


    @GetMapping("pointLineFilter")
    @RequiresAuthentication
    public ResponseBean pointLineFilter(HttpServletRequest request)
    {
        Map<String, String> parameterMap = getParameterMap(request);
        List<BaseLine> baseLines = navBaseLineService.getBaseLineListByParam(parameterMap);
        JSONObject result = new JSONObject();
        List<Harbour> harbours = harbourService.getHarbours();
        List<LinePoint> points = navLinePointService.getAll();


        /**
         * 找出不是港口不和别的点连接的孤立点
         */

        baseLines =   baseLines.stream().filter(a->a.getValid()!=null&&a.getValid()==1).collect(Collectors.toList());
        points=points.stream().filter(a->a.getValid()!=null&&a.getValid()==1).collect(Collectors.toList());

        harbours=harbours.stream().filter(a->a.getValid()!=null&&a.getValid()==1).collect(Collectors.toList());

        List<LinePoint> linePoints=new ArrayList<>();
        for (LinePoint point:points)
        {
            for(BaseLine baseLine:baseLines)
            {
                if(baseLine.getStartCode().equals(point.getCode())||baseLine.getEndCode().equals(point.getCode()))
                {
                    linePoints.add(point);
                }
            }
        }



        NavPointAction.QueryAllData(harbours, linePoints, baseLines);


        result.put("baseLines", JSONArray.parseArray(JSONObject.toJSONString(baseLines)));
        result.put("points", JSONArray.parseArray(JSONObject.toJSONString(linePoints)));
        return new ResponseBean(ResponseCode.SUCCESS, result);

    }


}
