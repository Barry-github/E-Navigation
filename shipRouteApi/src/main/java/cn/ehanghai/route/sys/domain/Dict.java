package cn.ehanghai.route.sys.domain;

import cn.ehanghai.route.common.domain.BaseDomain;

import javax.persistence.Column;
import javax.persistence.Table;


/**
 * @description:
 * @author: autoCode
 * @history:
 */
@Table(name="sys_dict")
public class Dict extends BaseDomain {

	

	private static final long serialVersionUID = 7720287929595490942L;

	/** 数据键 **/
	@Column(name="key_name")
	private String keyName;

	/** 数据值 **/
	
	private String value;

	/** 内容 **/
	private String content;

	/** 排序 **/
	@Column(name="order_by")
	private Integer orderBy;

	/** 备注 **/
	private String memo;

	/**
	 * 数据键
	 * 
	 */
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	/**
	 * 数据键
	 * 
	 */
	public String getKeyName() {
		return keyName;
	}

	/**
	 * 数据值
	 * 
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 数据值
	 * 
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 内容
	 * 
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 内容
	 * 
	 */
	public String getContent() {
		return content;
	}

	/**
	 * 排序
	 * 
	 */
	public void setOrderBy(Integer orderBy) {
		this.orderBy = orderBy;
	}

	/**
	 * 排序
	 * 
	 */
	public Integer getOrderBy() {
		return orderBy;
	}

	/**
	 * 备注
	 * 
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * 备注
	 * 
	 */
	public String getMemo() {
		return memo;
	}

}
