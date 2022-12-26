package org.qiunet.function.targets;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.base.Preconditions;
import org.qiunet.flash.handler.common.player.PlayerActor;

import java.util.HashMap;
import java.util.Map;

/***
 * 单个目标的进度管理
 *
 * @author qiunet
 * 2020-11-23 12:58
 */
public class Target {
	@JSONField(serialize = false)
	transient ITargetDef targetDef;

	@JSONField(serialize = false)
	transient Targets targets;
	/**
	 * 用户数据. 可以序列化的对象
	 */
	private Map<String, String> userdata;
	/**
	 * 任务目标的配置定义ID
	 */
	private int tid;
	/**
	 * 进度值
	 */
	private long value;

	public static Target valueOf(Targets targets, ITargetDef targetDef) {
		Target target = new Target();
		target.tid = targetDef.getId();
		target.targetDef = targetDef;
		target.targets = targets;
		return target;
	}
	/**
	 * 获得玩家对象
	 * @return actor
	 */
	public PlayerActor getPlayer() {
		return targets.getPlayer();
	}
	/**
	 * 进度 + 1
	 */
	public void addCount(){
		this.addCount(1);
	}

	/**
	 * 增加进度 并且 尝试完成
	 * @param count 数量
	 */
	public synchronized void addCount(long count){
		if (isFinished()) {
			return;
		}
		Preconditions.checkState(count > 0);
		this.value += count;
		targets.updateCallback(this);
		this.tryFinish();
	}

	/**
	 * 设置进度 并且 尝试完成
	 * @param count 数量
	 */
	public synchronized void alterToCount(int count) {
		// = 0 可能为gm重置任务
		if (isFinished()) {
			return;
		}
		this.value = count;
		targets.updateCallback(this);
		this.tryFinish();
	}

	private void tryFinish() {
		if (isFinished()) {
			// 可能在forEach时候. unwatch了
			getPlayer().addMessage((p0) -> {
				TargetContainer.get(p0).unWatch(this);
			});
		}
	}

	/**
	 * 移除资深
	 */
	public void remove() {
		targets.container.unWatch(this);
		targets.getTargets().remove(this);
	}

	/**
	 * 获得用户数据
	 * @param key
	 * @return
	 */
	public String getUserdata(String key) {
		if (userdata == null) {
			return null;
		}
		return userdata.get(key);
	}

	/**
	 * 清理某个key
	 * @param key
	 */
	public void removeUserdata(String key) {
		if (userdata != null) {
			userdata.remove(key);
		}
	}
	/**
	 * 增加用户数据
	 * @param key
	 * @param data
	 */
	public void addUserdata(String key, String data) {
		if (this.userdata == null) {
			this.userdata = new HashMap<>();
		}
		this.userdata.put(key, data);
	}

	@JSONField(serialize = false)
	public boolean isFinished(){
		return value >= targetDef.getValue();
	}

	@JSONField(serialize = false)
	public ITargetDef getTargetDef() {
		return targetDef;
	}

	public int getTid() {
		return tid;
	}

	public long getValue() {
		return value;
	}
}
