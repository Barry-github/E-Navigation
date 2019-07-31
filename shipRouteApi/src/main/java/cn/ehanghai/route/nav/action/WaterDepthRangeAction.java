package cn.ehanghai.route.nav.action;

import cn.ehanghai.route.common.action.BaseApiAction;
import cn.ehanghai.route.common.constants.ResponseCode;
import cn.ehanghai.route.common.utils.ResponseBean;
import cn.ehanghai.route.nav.domain.LinePoint;
import cn.ehanghai.route.nav.domain.WaterDepthRange;
import cn.ehanghai.route.nav.service.LinePointService;
import cn.ehanghai.route.nav.service.NavBaseLineService;
import cn.ehanghai.route.nav.service.WaterDepthRangeService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/nav/water/depth")
public class WaterDepthRangeAction extends BaseApiAction<LinePoint, LinePointService> {

    @Autowired
    private WaterDepthRangeService waterDepthRangeService;

    /**
     * 海图水深数据的覆盖范围
     * */
    @ResponseBody
    @RequestMapping({"ranges"})
    public ResponseBean getRanges(HttpServletRequest request)
    {
        List<WaterDepthRange> waterDepthRanges= waterDepthRangeService.getAllData("50W");
        List<String> paths=   waterDepthRanges.stream().map(a->a.toPath()).collect(Collectors.toList());
        String   message="ranges";
        JSONObject result=new JSONObject();
        result.put(message,paths);
        return new ResponseBean(ResponseCode.SUCCESS, result);
    }
}
