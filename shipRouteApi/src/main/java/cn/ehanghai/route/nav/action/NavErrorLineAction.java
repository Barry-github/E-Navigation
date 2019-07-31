package cn.ehanghai.route.nav.action;


import cn.ehanghai.route.common.action.BaseApiAction;
import cn.ehanghai.route.common.constants.ResponseCode;
import cn.ehanghai.route.common.utils.ResponseBean;
import cn.ehanghai.route.nav.domain.BaseLine;
import cn.ehanghai.route.nav.domain.ErrorInfo;
import cn.ehanghai.route.nav.domain.ErrorLine;
import cn.ehanghai.route.nav.service.ErrorLineService;
import cn.ehanghai.route.nav.service.NavBaseLineService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/nav/errorline")
public class NavErrorLineAction  extends BaseApiAction<ErrorLine, ErrorLineService> {

    @ResponseBody
    @RequestMapping(value="geterrorlines")
    public ResponseBean getErrorLines() {

        List<ErrorInfo> errorInfos=new ArrayList<>();

        List<ErrorLine>  errorLines=  entityService.getAllData();
        for (ErrorLine line: errorLines) {
            errorInfos.add(line.toErrorInfo());
        }


        JSONObject result = new JSONObject();
        result.put("errorLines", JSONArray.parseArray(JSONObject.toJSONString(errorInfos)));
        return new ResponseBean(ResponseCode.SUCCESS, result);
    }


    @ResponseBody
    @RequestMapping(value="deleteerrorline/{errorId}")
    public ResponseBean DeleteErrorLine(@PathVariable("errorId")int errorId )
    {
        entityService.deleteErrorLine(errorId);
        JSONObject result=new JSONObject();
        result.put("Success",true);
        return new ResponseBean(ResponseCode.SUCCESS, result);
    }

    @ResponseBody
    @RequestMapping(value="clear")
    public ResponseBean Clear()
    {
        entityService.deleteAll();
        JSONObject result=new JSONObject();
        result.put("Success",true);
        return new ResponseBean(ResponseCode.SUCCESS, result);

    }


}
