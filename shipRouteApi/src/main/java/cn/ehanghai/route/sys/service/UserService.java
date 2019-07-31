package cn.ehanghai.route.sys.service;

import cn.ehanghai.route.common.service.BaseService;
import cn.ehanghai.route.sys.domain.User;
import cn.ehanghai.route.sys.mapper.UserMapper;
import net.ryian.commons.DigestUtils;
import net.ryian.commons.EncodeUtils;
import net.ryian.commons.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * @author 胡恒博
 */
@Component
public class UserService extends BaseService<User, UserMapper> {

    /**
     * 根据条件查询分页
     *
     * @param paramMap
     * @return
     */
    public List<User> query(Map<String, String> paramMap) {
        Assert.notNull(paramMap);
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        String userName = paramMap.get("userName");
        String creator = paramMap.get("creator");
        if (!StringUtils.isEmpty(userName)) {
            criteria.andLike("userName", "%" + userName + "%");
        }
        if (!StringUtils.isEmpty(creator)) {
            criteria.andEqualTo("creator", Long.valueOf(creator));
        }
        return mapper.selectByExample(example);
    }

    /**
     * 根据用户名查找
     *
     * @param userName 用户名
     * @return
     */
    public User findUserByUserName(String userName) {
        User user = new User();
        user.setValid(1);
        user.setUserName(userName);
        return mapper.selectOne(user);
    }

    public User validatePwd(String userName, String pwd) {
        User user = findUserByUserName(userName);
        if (user == null)
            return null;
        byte[] hashPassword = DigestUtils.sha1(pwd.getBytes(), EncodeUtils.decodeHex(user.getSalt()), User.HASH_INTERATIONS);
        if (EncodeUtils.encodeHex(hashPassword).equals(user.getPassword())) {
            return user;
        } else {
            return null;
        }
    }

}
