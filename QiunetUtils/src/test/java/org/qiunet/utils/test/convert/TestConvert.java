package org.qiunet.utils.test.convert;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qiunet.utils.convert.ConvertManager;
import org.qiunet.utils.scanner.ClassScanner;

import java.lang.reflect.Field;

/***
 * 测试字段值 字符串转换
 *
 * @author qiunet
 * 2021/12/13 13:36
 */
public class TestConvert {

	private TestType type;

	private int num;

	@BeforeClass
	public static void init(){
		ClassScanner.getInstance().scanner();
	}

	@Test
	public void convertEnum() throws NoSuchFieldException {
		Field field = TestConvert.class.getDeclaredField("type");

		Object convert = ConvertManager.instance.convert(field, "2");
		Assert.assertEquals(convert, TestType.TYPE1);

		convert = ConvertManager.instance.convert(field, "NONE");
		Assert.assertEquals(convert, TestType.NONE);
	}

	@Test
	public void convertInteger() throws NoSuchFieldException {
		int val = 10;

		Field field = TestConvert.class.getDeclaredField("num");
		ConvertManager.instance.covertAndSet(this, field, String.valueOf(val));
		Assert.assertEquals(val, this.num);
	}
}

