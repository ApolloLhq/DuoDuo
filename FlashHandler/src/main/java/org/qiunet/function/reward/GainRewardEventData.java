package org.qiunet.function.reward;

import org.qiunet.utils.listener.event.IEventData;

/***
 * 获得奖励事件
 * 仅仅获得奖励本身
 *
 * @author qiunet
 * 2021-01-05 20:43
 */
public class GainRewardEventData implements IEventData {
	/**
	 *  奖励上下文
	 */
	private RewardContext context;

	public static GainRewardEventData valueOf(RewardContext context) {
		GainRewardEventData event = new GainRewardEventData();
		event.context = context;
		return event;
	}

	public RewardContext getContext() {
		return context;
	}
}
