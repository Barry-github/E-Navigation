package cn.ehanghai.route.sys.domain;


import cn.ehanghai.route.common.domain.BaseDomain;

import javax.persistence.Table;

/**
 * Created by allenwc on 16/3/10.
 */
@Table(name = "sys_param")
public class Param extends BaseDomain {

    private String name;
    private String value;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
