package cn.ehanghai.routecheck.common.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 胡恒博
 * @date 2018/05/22 09:57
 * @description
 **/
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 2734951563954109759L;

    private Date createDate;// 创建时间
    private Long creator;// 创建人
    private Date updateDate;// 更新时间
    private Long updator;// 更新人
    private Integer valid=1;// 有效性

    public abstract Long getId();

    public abstract void setId(Long id);

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Long getUpdator() {
        return updator;
    }

    public void setUpdator(Long updator) {
        this.updator = updator;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }
}
