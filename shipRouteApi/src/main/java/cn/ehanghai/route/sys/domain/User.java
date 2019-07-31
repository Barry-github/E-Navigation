package cn.ehanghai.route.sys.domain;

import cn.ehanghai.route.common.domain.BaseDomain;
import com.alibaba.fastjson.annotation.JSONField;
import net.ryian.commons.DigestUtils;
import net.ryian.commons.EncodeUtils;

import javax.persistence.Column;
import javax.persistence.Table;


/**
 * @author 胡恒博
 */
@Table(name = "nav_t_user")
public class User extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 使用的加密算法
     */
    public static final String HASH_ALGORITHM = "SHA-1";
    /**
     * 加密迭代次数
     */
    public static final int HASH_INTERATIONS = 1024;
    /**
     * 加密盐的大小
     */
    private static final int SALT_SIZE = 8;


    /**
     * 用户名
     **/
    @Column(name="user_name")
    private String userName;

    /**
     * 姓名
     **/
    private String name;

    /**
     * 密码
     **/
    @JSONField(serialize = false)
    private String password;

    /**
     * 盐值
     **/
    @JSONField(serialize = false)
    private String salt;

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void encryptUserPassword(String password) {
        byte[] salt = DigestUtils.generateSalt(SALT_SIZE);
        this.setSalt(EncodeUtils.encodeHex(salt));

        byte[] hashPassword = DigestUtils.sha1(password.getBytes(), salt, HASH_INTERATIONS);
        this.setPassword(EncodeUtils.encodeHex(hashPassword));
    }

}
