package cn.ehanghai.routecalc.nav.domain;


import cn.ehanghai.routecalc.common.domain.BaseDomain;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = "nav_t_harbour_type")
public class HarbourType extends BaseDomain {


    @Column(name = "name")
    private  String name;

    @Column(name = "type")
    private  Integer type;

    public HarbourType() {
    }

    public HarbourType(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
