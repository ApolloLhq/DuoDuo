package org.qiunet.cfg.wrapper;

import com.google.common.collect.Lists;
import org.qiunet.cfg.base.INestMapCfg;

import java.util.List;
import java.util.Map;

/***
 *
 *
 * @author qiunet
 * 2020-04-23 11:54
 ***/
public interface INestMapCfgWrapper<ID, SubID, Cfg extends INestMapCfg<ID, SubID>>
	extends ICfgWrapper<Cfg> {
	/**
	 * 得到所有的配置
	 * @return
	 */
	Map<ID, Map<SubID, Cfg>> allCfgs();
	/**
	 * 根据id获得配置对象
	 * @param id
	 * @return
	 */
	default Cfg getCfgById(ID id, SubID subID){
		Map<SubID, Cfg> subIDCfgMap = allCfgs().get(id);
		if (subIDCfgMap == null) {
			return null;
		}
		return subIDCfgMap.get(subID);
	}

	/**
	 * 返回id 对应的map
	 * @param id
	 * @return
	 */
	default Map<SubID, Cfg> getCfgsById(ID id){
		return allCfgs().get(id);
	}

	/**
	 * 是否有该id的配置
	 * @param id
	 * @return
	 */
	default boolean contains(ID id){
		return allCfgs().containsKey(id);
	}

	/**
	 * 是否有该id的配置
	 * @param id
	 * @return
	 */
	default boolean contains(ID id, SubID subID){
		Map<ID, Map<SubID, Cfg>> allCfgs = allCfgs();
		if (! allCfgs.containsKey(id)) {
			return false;
		}
		return allCfgs.get(id).containsKey(subID);
	}

	@Override
	default List<Cfg> list(){
		Map<ID, Map<SubID, Cfg>> allCfgs = allCfgs();
		List<Cfg> cfgList = Lists.newArrayList();

		for (Map<SubID, Cfg> subIDCfgMap : allCfgs.values()) {
			cfgList.addAll(subIDCfgMap.values());
		}
		return cfgList;
	}

	@Override
	default int size(){
		return list().size();
	}
}
