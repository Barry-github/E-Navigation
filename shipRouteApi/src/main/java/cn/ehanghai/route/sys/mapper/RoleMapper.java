package cn.ehanghai.route.sys.mapper;

import cn.ehanghai.route.sys.domain.Role;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author 胡恒博
 */
public interface RoleMapper extends Mapper<Role> {

    @Select("select * from sys_role where valid='1' and id in (select role_id from sys_r_user_role where valid='1' and user_id=#{value})")
    @Results(value = {
            @Result(property = "id", column = "ID"),
            @Result(property = "name", column = "NAME"),
            @Result(property = "code", column = "code"),
            @Result(property = "note", column = "note")
    })
    List<Role> getRolesByUser(Long userId);


    @Select("select * from sys_role where valid='1' and pid = #{value}")
    @Results(value = {
            @Result(property = "id", column = "ID"),
            @Result(property = "name", column = "NAME"),
            @Result(property = "code", column = "code"),
            @Result(property = "note", column = "note")
    })
    List<Role> getRolesByPid(Long pid);

    @Delete("DELETE FROM sys_r_user_role where user_id=#{userId}")
    void deleteByUser(Long userId);

    @Insert("INSERT INTO sys_r_user_role(user_id,role_id,CREATE_DATE,CREATOR) VALUES (#{userId},#{roleId},NOW(),#{creator})")
    void insertRoleUser(Map map);


}
