package org.qiunet.utils.test.async;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.qiunet.utils.async.CountdownComplete;

import java.util.concurrent.atomic.AtomicBoolean;

/***
 *
 * @author qiunet
 * 2022/11/25 17:47
 */
public class TestCountdownComplete {

	@Test
	public void test() {
		int i = 10;
		AtomicBoolean a = new AtomicBoolean();
		CountdownComplete countdown = new CountdownComplete(i, () -> {
			a.compareAndSet(false, true);
		});
		for (int i1 = 0; i1 < i; i1++) {
			countdown.countdown();
		}
		Assertions.assertTrue(a.get());
	}
}
