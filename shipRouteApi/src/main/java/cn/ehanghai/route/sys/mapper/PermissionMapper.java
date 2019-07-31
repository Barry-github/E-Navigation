package cn.ehanghai.route.sys.mapper;

import cn.ehanghai.route.sys.domain.Permission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author 胡恒博
 */
public interface PermissionMapper extends Mapper<Permission> {

    @Select("SELECT * FROM nav_t_permission where valid=1 and id in (select permission_id from nav_r_role_permission where valid=1 and role_id in (select role_id from nav_r_user_role where valid=1 and user_id=#{value}))")
    List<Permission> getPermissionsByUser(Long userId);

    @Select("SELECT * FROM nav_t_permission where valid=1 and type=2 and id in (select permission_id from nav_r_role_permission where valid=1 and role_id in (select role_id from nav_r_user_role where valid=1 and user_id=#{value}))")
    List<Permission> getBtnPermissionsByUser(Long userId);

    @Select("select * from nav_t_permission where valid=1 and id in (select permission_id from nav_r_role_permission where valid=1 and role_id=#{value})")
    List<Permission> getPermissionsByRole(Long roleId);

    @Delete("DELETE FROM nav_r_role_permission WHERE role_id = #{value}")
    void delPermissionsByRole(Long roleId);

    @Insert("insert into nav_r_role_permission(role_id,permission_id,CREATE_DATE,CREATOR)values(#{roleId},#{permissionId},now(),#{creator})")
    void insertPermissionRole(Map map);

}
