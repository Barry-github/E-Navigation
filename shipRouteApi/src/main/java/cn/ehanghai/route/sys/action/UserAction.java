package cn.ehanghai.route.sys.action;

import cn.ehanghai.route.common.constants.ResponseCode;
import cn.ehanghai.route.common.shiro.JWTToken;
import cn.ehanghai.route.common.utils.JWTUtil;
import cn.ehanghai.route.common.utils.ResponseBean;
import cn.ehanghai.route.sys.domain.Permission;
import cn.ehanghai.route.sys.domain.User;
import cn.ehanghai.route.sys.service.PermissionService;
import cn.ehanghai.route.sys.service.UserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeFilter;
import net.ryian.commons.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class UserAction {

    private static final Logger LOGGER = LogManager.getLogger(UserAction.class);

    @Autowired
    private UserService userService;
    @Autowired
    private PermissionService permissionService;

    @PostMapping("/user/login")
    public ResponseBean login(@RequestParam("username") String username,
                              @RequestParam("password") String password) {
        User user = userService.validatePwd(username, password);
        if (user != null) {
            List<Permission> permissions = permissionService.getPermissionsByUser(user.getId());
            JSONObject result = new JSONObject();
            String token = JWTUtil.sign(username, user.getPassword());
            result.put("token", token);
            result.put("permissions", getJSONArrayByPid(permissions, 0L, null));
            result.put("user", JSON.toJSON(user));
            return new ResponseBean(ResponseCode.SUCCESS, result);
        } else {
            return new ResponseBean(ResponseCode.SUCCESS.getCode(), "用户名或密码错误", null);
        }
    }

    /**
     * 退出接口（未实现）
     *
     * @return
     */
    @GetMapping("/user/logout")
    @RequiresAuthentication
    public ResponseBean logout() {
//        return new ResponseBean(ResponseCode.INVALID_TOKEN, null);
        return new ResponseBean(ResponseCode.INVALID_TOKEN.getCode(),"退出成功", null);
    }


    @GetMapping("/require_permission")
    @RequiresPermissions(logical = Logical.AND, value = {"view", "edit"})
    public ResponseBean requirePermission() {
        return new ResponseBean(200, "You are visiting permission require edit,view", null);
    }

    @RequestMapping(path = "/401")
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseBean unauthorized(HttpServletRequest request) {

        String token = request.getHeader("Authorization");
//        JWTToken token = new JWTToken(authorization);
        if (!StringUtils.isEmpty(token)) {
            // 解密获得username，用于和数据库进行对比
            String username = JWTUtil.getUsername(token);
            if (username == null) {
                return new ResponseBean(ResponseCode.INVALID_TOKEN, null);
            }
            User user = userService.findUserByUserName(username);
            if (user == null) {
                return new ResponseBean(ResponseCode.INVALID_TOKEN, null);
            }
            if (!JWTUtil.verify(token, username, user.getPassword())) {
                return new ResponseBean(ResponseCode.INVALID_TOKEN, null);
            }
        }
        return new ResponseBean(401, "Unauthorized", null);
    }


    private JSONArray getJSONArrayByPid(List<Permission> permissions, Long pid, SerializeFilter filter) {
        JSONArray arr = new JSONArray();
        for (Permission permission : permissions) {
            if (pid.equals(permission.getPid())) {
                JSONObject o = JSON.parseObject(JSON.toJSONString(permission, filter));
                o.put("children", getJSONArrayByPid(permissions, permission.getId(), filter));
                arr.add(o);
            }
        }
        return arr;
    }
}
