package org.qiunet.data.db.support.info.glboal.base;

import org.qiunet.data.db.datasource.DbSourceType;
import org.qiunet.data.db.support.info.IEntityDbInfo;
import org.qiunet.data.db.util.DbProperties;

/**
 * 全局库的基类dbinfo
 * @author qiunet
 *         Created on 17/2/27 09:42.
 */
public abstract class BaseGlobalDbInfo implements IEntityDbInfo {
	@Override
	public String getDbName() {
		return DbProperties.getInstance().getDbNamePrefix() + "global";
	}

	@Override
	public int getDbIndex() {
		return 0;
	}
	@Override
	public String getDbSourceKey() {
		return DbSourceType.DATASOURCE_GLOBAL.getGlobalDbSourceKey();
	}
}
