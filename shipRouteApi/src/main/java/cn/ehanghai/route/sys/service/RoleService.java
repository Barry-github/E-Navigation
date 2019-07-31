package cn.ehanghai.route.sys.service;

import cn.ehanghai.route.common.service.BaseService;
import cn.ehanghai.route.sys.domain.Role;
import cn.ehanghai.route.sys.mapper.RoleMapper;
import net.ryian.commons.StringUtils;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: autoCode
 * @history:
 */
@Component
public class  RoleService extends BaseService<Role,RoleMapper> {

	/**
	 * 根据条件查询分页
	 * @param paramMap
	 * @return
	 */
	@Override
	public List<Role> query(Map<String,String> paramMap) {
		Example example = new Example(Role.class);
		Example.Criteria criteria = example.createCriteria().andEqualTo("valid",1);
		String name = paramMap.get("name");
		if (!StringUtils.isEmpty(name)) {
			criteria.andLike("name",name);
		}
		return mapper.selectByExample(example);
	}

	public List<Role> getRolesByUser(Long userId) {
		return mapper.getRolesByUser(userId);
	}

	public List<Role> getRolesByPid(Long pid) {
		return mapper.getRolesByPid(pid);
	}

	public void saveUserRoles(Long userId,String roles,Long currentUserId) {
		mapper.deleteByUser(userId);
		if(!StringUtils.isEmpty(roles)) {
			Map map = new HashMap();
			map.put("userId",Long.valueOf(userId));
			map.put("creator",currentUserId);
			for(String roleId : roles.split(",")) {
				map.put("roleId", Long.valueOf(roleId));
				mapper.insertRoleUser(map);
			}
		}
	}

}
