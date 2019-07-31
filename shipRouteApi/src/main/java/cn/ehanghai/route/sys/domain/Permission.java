package cn.ehanghai.route.sys.domain;

import cn.ehanghai.route.common.domain.BaseDomain;
import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Column;
import javax.persistence.Table;


/**
 * @author 胡恒博
 */
@Table(name = "nav_t_permission")
public class Permission extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 名称*
     */
    @JSONField(name = "text")
    private String name;

    /**
     * 编码*
     */
    private String code;

    /**
     * 类型*
     */

    private String type;

    /**
     * 链接*
     */
    private String url;

    /**
     * 图标*
     */
    private String icon;

    /**
     * 父id*
     */
    @JSONField(name = "parentId")
    private Long pid;

    /**
     * 树路径*
     */
    @Column(name = "tree_path")
    private String treePath;

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

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Long getPid() {
        return pid;
    }

    public void setTreePath(String treePath) {
        this.treePath = treePath;
    }

    public String getTreePath() {
        return treePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Permission that = (Permission) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (pid != null ? !pid.equals(that.pid) : that.pid != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (pid != null ? pid.hashCode() : 0);
        return result;
    }
}
