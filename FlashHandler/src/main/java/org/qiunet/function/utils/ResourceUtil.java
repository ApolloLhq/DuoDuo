package org.qiunet.function.utils;

import org.qiunet.function.base.IResourceCfg;
import org.qiunet.function.base.IResourceType;
import org.qiunet.function.base.basic.BasicFunctionManager;
import org.qiunet.function.base.basic.IBasicFunction;
import org.qiunet.utils.data.IKeyValueData;
import org.qiunet.utils.exceptions.CustomException;
import org.qiunet.utils.scanner.anno.AutoWired;

/***
 * cfgId 为 type_contentId 组合唯一key.
 * type 和 contentId 是平台方给出.
 * 货币不在平台. type 为 0. id由游戏配置给出.
 *
 * @author qiunet
 * 2022/7/1 21:13
 */
public class ResourceUtil {
	@AutoWired
	private static IBasicFunction basicFunction;

	public static int getContentId(String cfgId) {
		return getResourceCfg(cfgId).getContentId();
	}

	/**
	 * 构造一个 itemId
	 * @param type
	 * @param contentId
	 * @return
	 */
	public static String buildItemId(int type, int contentId) {
		return type + "_" + contentId;
	}
	/**
	 * Resource Type
	 * @param cfgId
	 * @return
	 */
	public static <T extends Enum<T> & IResourceType> T getType(String cfgId) {
		return getResourceCfg(cfgId).type();
	}

	public static IResourceCfg getResourceCfg(String cfgId) {
		return basicFunction.getResById(cfgId);
	}

	/**
	 * 外面不需要调用. 只有RewardConfig ConsumeConfig 调用
	 * @param config
	 */
	public static void handlerResAndCfgId(IKeyValueData<Object, String> config) {
		if ( config.containKey("cfgId")) {
			return;
		}

		if (config.containKey("cid") && config.containKey("type")) {
			config.returnMap().put("cfgId", ResourceUtil.buildItemId(config.getInt("type"), config.getInt("cid")));
		}else if(config.containKey("id")) {
			// 此处id 为resID
			String cfgId = BasicFunctionManager.instance.getCfgIdByResId(config.getString("id"));
			config.returnMap().put("cid", "" + getContentId(cfgId));
			config.returnMap().put("type", ""+ getType(cfgId));
			config.returnMap().put("cfgId", cfgId);
		}else {
			throw new CustomException("reward config get cfgId error!");
		}
	}
}
