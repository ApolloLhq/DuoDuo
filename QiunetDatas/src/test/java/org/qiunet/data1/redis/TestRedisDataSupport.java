package org.qiunet.data1.redis;

import org.junit.Assert;
import org.junit.Test;
import org.qiunet.data1.support.RedisDataSupport;

public class TestRedisDataSupport {
	private static RedisDataSupport<Long, VipDo, VipBo> dataSupport = new RedisDataSupport<>(RedisDataUtil.getInstance(), VipDo.class, VipBo::new);

	private long uid = 10000;
	@Test
	public void testEntity(){
		VipDo vipDo = new VipDo();
		vipDo.setUid(uid);
		vipDo.setLevel(10);
		vipDo.setExp(1000);

		vipDo.insert();
		dataSupport.syncToDatabase();
		dataSupport.expire(uid);

		VipBo bo = dataSupport.getBo(uid);
		Assert.assertEquals(bo.getDo().getUid(), uid);
		Assert.assertEquals(bo.getDo().getLevel(), 10);
		Assert.assertEquals(bo.getDo().getExp(), 1000);

		bo.getDo().setExp(100);
		bo.update();
		dataSupport.syncToDatabase();

		dataSupport.expire(uid);
		bo = dataSupport.getBo(uid);
		Assert.assertEquals(bo.getDo().getExp(), 100);

		bo.delete();
		dataSupport.syncToDatabase();
		bo = dataSupport.getBo(uid);
		Assert.assertNull(bo);
	}
}
