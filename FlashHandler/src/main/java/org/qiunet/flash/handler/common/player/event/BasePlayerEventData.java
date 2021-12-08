package org.qiunet.flash.handler.common.player.event;

import org.qiunet.data.db.loader.IPlayerDataLoader;
import org.qiunet.flash.handler.common.player.PlayerActor;
import org.qiunet.flash.handler.common.player.offline.OfflinePlayerActor;

/***
 * 带玩家参数的事件数据.
 *
 * @author qiunet
 * 2020-10-13 20:27
 */
public abstract class BasePlayerEventData extends BaseUserEventData<PlayerActor> implements IPlayerDataLoader {

	private OfflinePlayerActor offlinePlayerActor;

	public OfflinePlayerActor getOfflinePlayerActor() {
		return offlinePlayerActor;
	}

	public void setOfflinePlayerActor(OfflinePlayerActor offlinePlayerActor) {
		this.offlinePlayerActor = offlinePlayerActor;
	}

	@Override
	public IPlayerDataLoader dataLoader() {
		if (getPlayer() != null) {
			return getPlayer().dataLoader();
		}else {
			return offlinePlayerActor.dataLoader();
		}
	}
}
