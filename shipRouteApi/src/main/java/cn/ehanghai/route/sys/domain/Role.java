package cn.ehanghai.route.sys.domain;


import cn.ehanghai.route.common.domain.BaseDomain;

import javax.persistence.Table;


/**
 * @author 胡恒博
 */
@Table(name="nav_t_role")
public class Role extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 角色名*
     */
    private String name;

    /**
     * 角色编码*
     */
    private String code;

    /**
     * 备注*
     */
    private String note;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        if (code != null ? !code.equals(role.code) : role.code != null) return false;
        if (name != null ? !name.equals(role.name) : role.name != null) return false;
        if (note != null ? !note.equals(role.note) : role.note != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (note != null ? note.hashCode() : 0);
        return result;
    }
}
