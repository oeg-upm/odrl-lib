package odrl.lib;

import org.junit.Assert;

public class TestOdrlLib {

	public void test01() {
		String policy = "";
		boolean result = Tests.solvePolicy(policy);
		Assert.assertTrue(result);
	}
	
	
}
