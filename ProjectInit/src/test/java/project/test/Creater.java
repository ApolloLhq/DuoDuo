package project.test;

import org.junit.Test;
import project.init.ProjectInitCreator;

/**
 * Created by qiunet.
 * 17/7/9
 */
public class Creater {
	@Test
	public void testCreate(){
		String basePath = System.getProperty("user.dir");
		ProjectInitCreator.create(basePath);
	}
}
