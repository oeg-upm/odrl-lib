package tests.odrl.lib.leftoperands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import tests.odrl.lib.Tests;

public class TestTime {

	private StringBuilder dir = new StringBuilder("./src/test/resources/policies/");
	private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");

	private String toXsdTime() {
		StringBuilder buff = new StringBuilder();
		Date date = new Date();
		buff.append(format.format(date));
		return buff.append('Z').toString();
	}


	@Test
	public void test01() throws Exception {
		String policy = Tests.readPolicy(dir.append("time-ex1.json").toString());
		Tests.odrl.setDebug(true);
		Map<String, List<String>> result = Tests.solvePolicy(policy);
		System.out.println(result);
		Assert.assertTrue(result.containsKey("http://example.com/asset:9898.movie"));
	}

	@Test
	public void test02() throws Exception {
		String policy = Tests.readPolicy(dir.append("time-ex2.json").toString());
		Map<String, List<String>> result = Tests.solvePolicy(policy);
		Assert.assertTrue(result.containsKey("http://example.com/asset:9898.movie"));
	}

	@Test
	public void test03() throws Exception {
		String policy = Tests.readPolicy(dir.append("time-ex3.json").toString());
		boolean exception = false;
		try {
			Tests.solvePolicy(policy);

		} catch (Exception e) {
			exception = true;
		}
		Assert.assertTrue(exception);
	}

}
