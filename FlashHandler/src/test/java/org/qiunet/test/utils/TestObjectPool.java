package org.qiunet.test.utils;

import org.junit.jupiter.api.Test;
import org.qiunet.flash.handler.common.CommMessageHandler;
import org.qiunet.flash.handler.common.IMessage;
import org.qiunet.utils.exceptions.CustomException;
import org.qiunet.utils.pool.ObjectPool;
import org.qiunet.utils.string.StringUtil;
import org.qiunet.utils.system.OSUtil;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/***
 *
 * @author qiunet
 * 2023/2/17 10:53
 */
public class TestObjectPool {
	private static final ExecutorService threadPool = new ThreadPoolExecutor(OSUtil.availableProcessors(), OSUtil.availableProcessors() * 2, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<>());

	private static final List<CommMessageHandler> list = IntStream.range(0, OSUtil.availableProcessors() * 2)
			.mapToObj(i -> new CommMessageHandler()).collect(Collectors.toList());

	private static final CountDownLatch countdown = new CountDownLatch(1000000);

	@Test
	public void test() throws InterruptedException {
		long count = countdown.getCount();
		for (long i = 0; i < count; i++) {
			long finalI = i;
			threadPool.execute(() -> {
				TestObj testObj = TestObj.valueOf(String.valueOf(finalI));
				list.get((int) (finalI % list.size())).addMessage(testObj);
			});
		}
		countdown.await();
	}

	public static class TestObj implements IMessage<CommMessageHandler> {

		private static final ObjectPool<TestObj> RECYCLER = new ObjectPool<TestObj>() {
			@Override
			public TestObj newObject(Handle<TestObj> handler) {
				return new TestObj(handler);
			}
		};

		private final ObjectPool.Handle<TestObj> recyclerHandle;

		public TestObj(ObjectPool.Handle<TestObj> recyclerHandle) {
			this.recyclerHandle = recyclerHandle;
		}

		private Object requestData;
		public static TestObj valueOf(Object requestData) {
			TestObj testObj = RECYCLER.get();
			testObj.requestData = requestData;
			return testObj;
		}

		private void recycle() {
			this.requestData = null;
			this.recyclerHandle.recycle();
		}

		@Override
		public void execute(CommMessageHandler actor) {
			try {
				if (this.requestData == null) {
					System.out.println("----------------------");
				}
			}finally {
				countdown.countDown();
				this.recycle();
			}
		}
	}
}
