package org.qiunet.function.reward;

import org.qiunet.function.base.IResourceCfg;
import org.qiunet.function.base.IResourceType;
import org.qiunet.function.base.basic.BasicFunctionManager;
import org.qiunet.function.utils.ResourceUtil;

/***
 * 真实奖励. 方便客户端展示
 * 普通奖励就是BaseReward本身.
 * 装备奖励是装备实例
 *
 * @author qiunet
 * 2021-01-07 15:42
 */
public interface IRealReward {
	/**
	 * 有唯一id的给唯一id.
	 * 没有为 0
	 * @return
	 */
	String getUid();
	/**
	 * 配置id
	 * @return
	 */
	String getCfgId();

	default String getContentId() {
		return ResourceUtil.getContentId(getCfgId());
	}

	default <T extends Enum<T> & IResourceType> T getType() {
		return ResourceUtil.getType(getCfgId());
	}

	/**
	 * 获得数量信息
	 * @return
	 */
	long getValue();

	/**
	 * 获得资源配置
	 * @param <T>
	 * @return
	 */
	default <T extends IResourceCfg> T getResourceCfg() {
		return BasicFunctionManager.instance.getResById(getCfgId());
	}
}
