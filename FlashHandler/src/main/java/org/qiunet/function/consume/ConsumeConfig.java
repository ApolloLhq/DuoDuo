package org.qiunet.function.consume;

import org.qiunet.function.base.IResourceType;
import org.qiunet.function.utils.ResourceUtil;
import org.qiunet.utils.data.IKeyValueData;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/***
 * 消耗配置的json类
 *
 * @author qiunet
 * 2020-12-28 11:50
 */
public class ConsumeConfig  extends HashMap<Object, String> implements IKeyValueData<Object, String> {

	public ConsumeConfig() {}

	public ConsumeConfig(String cfgId, long value) {
		this(cfgId, value, false);
	}

	public ConsumeConfig(String cfgId, long value, boolean banReplace) {
		this.put("type", ""+ResourceUtil.getType(cfgId).value());
		this.put("cid", ""+ ResourceUtil.getContentId(cfgId));
		this.put("banReplace", String.valueOf(banReplace));
		this.put("value", String.valueOf(value));
		this.put("cfgId", cfgId);
	}

	/**
	 * 转 Consume
	 * @param typeGetter cfgId → type
	 * @return Consume
	 */
	public BaseConsume convertToConsume(Function<String, IResourceType> typeGetter) {
		return typeGetter.apply(getCfgId()).createConsume(this);
	}

	public String getContentId() {
		return get("cid");
	}

	public int getType() {
		return getInt("type");
	}

	public String getCfgId() {
		ResourceUtil.handlerResAndCfgId(this);
		return getString("cfgId");
	}
	/**
	 * 数量
	 * @return
	 */
	public long getValue() {
		return getLong("value");
	}

	/**
	 * 是否禁止替换
	 * @return
	 */
	public boolean isBanReplace() {
		return getBoolean("banReplace");
	}

	@Override
	public Map<Object, String> returnMap() {
		return this;
	}
}
