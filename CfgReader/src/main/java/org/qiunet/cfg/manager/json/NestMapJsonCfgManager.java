package org.qiunet.cfg.manager.json;

import org.qiunet.cfg.base.INestMapConfig;
import org.qiunet.cfg.base.InitCfg;
import org.qiunet.utils.collection.safe.SafeMap;

import java.util.List;
import java.util.Map;

/**
 * @author zhengj
 * Date: 2019/6/6.
 * Time: 16:10.
 * To change this template use File | Settings | File Templates.
 */
public abstract class NestMapJsonCfgManager<ID, SubId, Cfg extends INestMapConfig<ID, SubId>> extends BaseJsonCfgManager<Cfg> {
	private Map<ID, Map<SubId, Cfg>> cfgs;

	protected NestMapJsonCfgManager(String fileName) {
		super(fileName);
	}

	@Override
	void init() throws Exception {
		this.cfgs = getNestMapCfg();
		this.initCfgSelf();
	}
	/***
	 * 如果cfg 对象是实现了 initCfg接口,
	 * 就调用init方法实现cfg的二次init.
	 */
	private void initCfgSelf() {
		if (! InitCfg.class.isAssignableFrom(getCfgClass())) {
			return;
		}

		this.cfgs.values().stream().flatMap(val -> val.values().stream())
				.map(cfg -> (InitCfg)cfg)
				.forEach(InitCfg::init);
	}
	public boolean contains(ID id, SubId subId) {
		if (! cfgs.containsKey(id)) {
			return false;
		}
		return cfgs.get(id).containsKey(subId);
	}

	public boolean contains(ID id) {
		return cfgs.containsKey(id);
	}
	/***
	 * 根据id 和 subId 得到一条cfg数据
	 * @param id
	 * @param subId
	 * @return
	 */
	public Cfg getCfgByIdAndSubId(ID id, SubId subId) {
		Map<SubId, Cfg> subIdCfgMap = cfgs.get(id);
		if (subIdCfgMap == null) {
			return null;
		}
		return subIdCfgMap.get(subId);
	}

	/***
	 * 得到一个一定格式的嵌套map
	 * 格式: key 对应 Map<subKey, cfg>
	 * @return
	 * @throws Exception
	 */
	protected Map<ID, Map<SubId, Cfg>> getNestMapCfg() throws Exception {
		SafeMap<ID, Map<SubId, Cfg>> cfgMap = new SafeMap<>();
		List<Cfg> cfgs = getSimpleListCfg();
		for (Cfg cfg : cfgs) {
			Map<SubId, Cfg> subMap = cfgMap.computeIfAbsent(cfg.getId(), key -> new SafeMap<>());
			subMap.put(cfg.getSubId(), cfg);
		}
		for (Map<SubId, Cfg> subKeyCfgMap : cfgMap.values()) {
			((SafeMap) subKeyCfgMap).loggerIfAbsent();
			((SafeMap) subKeyCfgMap).convertToUnmodifiable();
		}
		cfgMap.loggerIfAbsent();
		cfgMap.convertToUnmodifiable();
		return cfgMap;
	}


	public Map<ID, Map<SubId, Cfg>> getCfgs() {
		return cfgs;
	}
}
