package cn.ehanghai.routecheck.common.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by allenwc on 15/9/11.
 */
public class BaseDomain extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JSONField(serialize = false)
    public Integer getValid() {
        return super.getValid();
    }

    @JSONField(serialize = false)
    public Long getUpdator() {
        return super.getUpdator();
    }

    @JSONField(serialize = false)
    public Date getUpdateDate() {
        return super.getUpdateDate();
    }

    @JSONField(serialize = false)
    public Long getCreator() {
        return super.getCreator();
    }

    @JSONField(serialize = false)
    public Date getCreateDate() {
        return super.getCreateDate();
    }

}
