package org.qiunet.function.base;


import org.qiunet.function.utils.ResourceUtil;

/***
 * 资源类型接口
 * @author qiunet
 * 2020-04-25 20:48
 **/
public interface IResourceCfg {
	/**
	 * 获得contentId
	 * @return
	 */
	int getContentId();
	/**
	 * 获得资源的子类型
	 * @return
	 */
	<Type extends Enum<Type> & IResourceType> Type type();
	/**
	 * 获得在背包的ID
	 * @return
	 */
	default String getCfgId() {
		return ResourceUtil.buildItemId(type().value(), getContentId());
	}
}
