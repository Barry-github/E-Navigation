package cn.ehanghai.route.common.action;


import cn.ehanghai.route.common.constants.ResponseCode;
import cn.ehanghai.route.common.domain.BaseEntity;
import cn.ehanghai.route.common.exception.MissingParamException;
import cn.ehanghai.route.common.service.BaseService;
import cn.ehanghai.route.common.utils.ResponseBean;
import cn.ehanghai.route.sys.domain.User;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import net.ryian.commons.BeanUtils;
import net.ryian.commons.GenericsUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Restful Api 基类 遵循 Restful API 的设计规范
 * 规范参考 ：http://novoland.github.io/%E8%AE%BE%E8%AE%A1/2015/08/17/Restful%20API%20%E7%9A%84%E8%AE%BE%E8%AE%A1%E8%A7%84%E8%8C%83.html
 *
 * @author 胡恒博
 */
public class BaseApiAction<K extends BaseEntity, T extends BaseService> {
    public static final String ATTRI_MEMBER = "attr_member";

    @Autowired
    protected T entityService;

    protected Class<K> entityClass;

    public BaseApiAction() {
        //通过反射,获得定义Class时声明的父类的范型参数的类型
        entityClass = GenericsUtils.getSuperClassGenricType(getClass());
    }

    @GetMapping("queryPaged")
    @RequiresAuthentication
    public ResponseBean queryPaged(HttpServletRequest request,
                                   HttpServletResponse response){
        PageInfo<?> page = entityService.queryPaged(getParameterMap(request));
        JSONObject o = (JSONObject) JSONObject.toJSON(page);
        o.put("rows", o.get("list"));
        o.remove("list");
        o.put("totalPageCount", o.get("lastPage"));
        o.put("countPerPage", o.get("pageSize"));
        o.put("currentPage",o.get("pageNum"));
        return new ResponseBean(ResponseCode.SUCCESS, o.toJSONString());
    }

    @GetMapping(value = "{id}")
    @RequiresAuthentication
    public ResponseBean info(@PathVariable("id") Long id) {
        if (id == null) {
            throw new MissingParamException();
        }
        BaseEntity entity = entityService.get(id);
        return new ResponseBean(ResponseCode.SUCCESS, JSONObject.toJSONString(entity));
    }

    /**
     * 保存单条Dictionary记录.
     */
    @PostMapping("save")
    @RequiresAuthentication
    public ResponseBean save(HttpServletRequest request) throws Exception {
        try {
            K o = bindEntity(request, entityClass);
            Long id = entityService.saveOrUpdate(o);
            return new ResponseBean(ResponseCode.SUCCESS, id);
        } catch (Exception e) {
            logger.error("save", e);
            throw e;
        }
    }

    @DeleteMapping(value = "delete/{ids}")
    @RequiresAuthentication
    public ResponseBean delete(HttpServletRequest request,@PathVariable("ids") String ids) {
        for (String id : ids.split(",")) {
            this.beforeDelete(Long.valueOf(id));
            entityService.logicRemove(Long.valueOf(id));
        }
        return new ResponseBean(ResponseCode.SUCCESS, messageSuccessWrap());
    }

    protected <T extends BaseEntity> T bindEntity(HttpServletRequest request, Class<T> clazz)
            throws Exception {
        T entity = clazz.newInstance();
        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String propertyName = (String) enumeration.nextElement();
            String propertyValue = request.getParameter(propertyName).trim();

            propertyValue = propertyValue.replace("\'", "\"");
            BeanUtils.setBeanPropertyByName(entity, propertyName, propertyValue);
        }
        return entity;
    }

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected Map<String, String> getParameterMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String propertyName = enumeration.nextElement();
            String propertyValue = request.getParameter(propertyName)
                    .trim();
            map.put(propertyName, propertyValue);
        }
        return map;
    }

    protected JSONObject messageFailureWrap(String msg) {
        JSONObject result = new JSONObject();
        result.put("success", false);
        result.put("message", msg);
        return result;
    }

    protected JSONObject messageSuccessWrap(String msg) {
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", msg);
        return result;
    }
    protected JSONObject messageSuccessWrap() {
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "成功！");
        return result;
    }

    protected Boolean strToBoolean(String param) {
        Boolean flag = false;
        if (param == null) {
            return false;
        }
        switch (param.trim()) {
            case "1":
                flag = true;
                break;
            case "true":
                flag = true;
                break;
            default:
                break;
        }
        return flag;
    }

    protected User getCurrentUser(HttpServletRequest request) {
        if (request == null) {
            ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            request = ra.getRequest();
        }
        return (User) request.getAttribute(BaseApiAction.ATTRI_MEMBER);
    }

    protected void beforeDelete(Long id) {

    }

}
