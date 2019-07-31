package cn.ehanghai.route.nav.action;


import cn.ehanghai.route.common.action.BaseApiAction;
import cn.ehanghai.route.common.constants.ResponseCode;
import cn.ehanghai.route.common.utils.ResponseBean;
import cn.ehanghai.route.common.utils.WebService;
import cn.ehanghai.route.nav.domain.BaseLine;
import cn.ehanghai.route.nav.domain.LinePoint;
import cn.ehanghai.route.nav.domain.OperationRecord;
import cn.ehanghai.route.nav.service.NavBaseLineService;
import cn.ehanghai.route.nav.service.OperationRecordService;
import cn.ehanghai.route.sys.domain.User;
import com.alibaba.fastjson.JSONObject;
//import com.sun.xml.internal.rngom.parse.host.Base;
import net.ryian.commons.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 胡恒博
 **/
@RestController
@RequestMapping("/nav/navBaseLine")
public class NavBaseLineAction extends BaseApiAction<BaseLine, NavBaseLineService> {


    @Autowired
    private OperationRecordService operationRecordService;


    @Override
    public ResponseBean save(HttpServletRequest request) throws Exception {

        BaseLine o = bindEntity(request, entityClass);
        if (StringUtils.isEmpty(o.getEndCode()) || StringUtils.isEmpty(o.getStartCode())) {
            return new ResponseBean(ResponseCode.FAIL.getCode(), "参数不能为空！", null);
        }
        //1、新增航线A到B保存成功
        //2、新增航线B到A保存的时候，应该不能保存
        BaseLine baseLineByStartAndEnt = entityService.getBaseLineByStartAndEnd(o.getEndCode(), o.getStartCode());
        if (baseLineByStartAndEnt != null) {
            return new ResponseBean(ResponseCode.FAIL.getCode(), "保存失败！航段已经存在！", null);
        }
        baseLineByStartAndEnt = entityService.getBaseLineByStartAndEnd(o.getStartCode(), o.getEndCode());
        if (o.getId() ==null && baseLineByStartAndEnt != null) {
            return new ResponseBean(ResponseCode.FAIL.getCode(), "保存失败！航段已经存在！", null);
        }

        String oneWayStreet = request.getParameter("oneWayStreet");

        String lane = request.getParameter("lane");
        o.setOneWayStreet(Integer.parseInt(oneWayStreet));
        o.setLane(strToBoolean(lane));
        o.setImportState(1);

        //操作记录
        Long userId=    getCurrentUser(request).getId();
        String action="update";
        if(o.getId()==null) {
            action="insert";
            o.setValid(1);
        }
        NavOperationRecordAction.SaveLineOperationRecord(operationRecordService,userId,o,action);

        Long id = entityService.saveOrUpdate(o);
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("id", id);

        //检测航线
        WebService.checkRouteLine(o);

        return new ResponseBean(ResponseCode.SUCCESS, result);
    }



    @Override
    protected void beforeDelete(Long id) {
        super.beforeDelete(id);
        //操作记录
        BaseLine baseLine=  entityService.get(id);
        Long userId=getCurrentUser(null).getId();
        NavOperationRecordAction.SaveLineOperationRecord( operationRecordService,userId,baseLine, "delete");
    }




}
