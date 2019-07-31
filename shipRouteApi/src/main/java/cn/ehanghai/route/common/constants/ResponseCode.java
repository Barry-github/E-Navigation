package cn.ehanghai.route.common.constants;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 响应码
 * @author 胡恒博
 * @date 2018/05/17 16:48
 **/
public enum ResponseCode {

    SUCCESS(200, "成功!"),
    FAIL(999, "失败!"),
    EXCEPTION(401, "失败"),
    ERROR(2, "参数错误."),
    INVALID_TOKEN(90, "请重新登陆!");

    /**
     * 存放CODE -> Enum的Map
     */
    private static final ConcurrentMap<Integer, ResponseCode> CODE_MAP = new ConcurrentHashMap<Integer, ResponseCode>(
            ResponseCode.values().length);

    /**
     * 填充CODE --> Enum的Map
     */
    static {
        for (ResponseCode codeTable : ResponseCode.values()) {
            CODE_MAP.put(codeTable.code, codeTable);
        }
    }

    /**
     * 响应码
     */
    private final int code;

    /**
     * 响应消息
     */
    private final String message;

    /**
     * 构造函数
     *
     * @param code    响应码
     * @param message 响应消息
     */
    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseCode fromCode(Integer code) {
        return CODE_MAP.get(code);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
