package org.qiunet.utils.async;

import com.google.common.base.Preconditions;

import java.util.concurrent.atomic.AtomicInteger;

/***
 * 计数到0后, 会触发执行runnable
 * 如果是初始化为0值. 则需要调用{@link CountdownComplete#countUpgrade()} 增加后才有逻辑
 * @author qiunet
 * 2022/11/25 17:35
 */
public class CountdownComplete {
	/**
	 * 计数器
	 */
	private final AtomicInteger counter;
	/**
	 * 到最后执行
	 */
	private final Runnable runnable;

	/**
	 * 使用0来作为counter. 后面需要调用countUpgrade增加计数.
	 * 否则runnable 不会调用
	 * @param runnable
	 */
	public CountdownComplete(Runnable runnable) {
		this.counter = new AtomicInteger();
		this.runnable = runnable;
	}

	public CountdownComplete(int count, Runnable runnable) {
		Preconditions.checkState(count > 0);
		this.counter = new AtomicInteger(count);
		this.runnable = runnable;
	}

	/**
	 * 往上加 n
	 * @param n 一个大于0 的值
	 * @return 当前值
	 */
	public int countUpgrade(int n) {
		Preconditions.checkState(n > 0);
		return this.counter.addAndGet(n);
	}

	/**
	 * 往上加 1
	 * @return 当前值
	 */
	public int countUpgrade() {
		return this.countUpgrade(1);
	}

	/**
	 * 当前数
	 * @return
	 */
	public int currCount() {
		return counter.get();
	}

	/**
	 * 往下减 1
	 */
	public void countdown() {
		if (counter.decrementAndGet() == 0) {
			runnable.run();
		}
	}
}
