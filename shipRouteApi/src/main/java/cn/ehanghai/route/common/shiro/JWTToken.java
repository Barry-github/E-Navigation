package cn.ehanghai.route.common.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author 胡恒博
 */
public class JWTToken implements AuthenticationToken {

    // 密钥
    private String token;

    public JWTToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
