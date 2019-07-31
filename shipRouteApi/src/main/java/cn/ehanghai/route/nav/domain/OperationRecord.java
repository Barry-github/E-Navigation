package cn.ehanghai.route.nav.domain;

import cn.ehanghai.route.common.domain.BaseDomain;

import javax.persistence.Table;
import java.util.Date;

@Table(name = "nav_t_operation_record")
public class OperationRecord  extends BaseDomain {

private     Long id;
    private    Long userId;
    private    String action;
    private    String describe;
    private   Date time;

    public OperationRecord() {
    }

    public OperationRecord(Long id, Long userId, String action, String describe, Date time) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.describe = describe;
        this.time = time;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
