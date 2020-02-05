package org.qiunet.cfg.base;

/**
 * 游戏设定数据处理类实现接口
 * @author qiunet
 *         Created on 17/2/9 12:18.
 */
public interface ICfgManager {
	/**
	 * 设定加载
	 * @return
	 */
	void loadCfg() throws Exception;

	/**
	 * 得到加载的文件名
	 * @return
	 */
	String getLoadFileName();

	/**
	 * 得到该类加载的cfg 类class
	 * @return
	 */
	Class<? extends ICfg> getCfgClass();
}
