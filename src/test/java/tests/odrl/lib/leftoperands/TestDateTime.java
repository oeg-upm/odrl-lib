package tests.odrl.lib.leftoperands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import odrl.lib.model.exceptions.UnsupportedFunctionException;
import tests.odrl.lib.Tests;

public class TestDateTime {

	private StringBuilder dir = new StringBuilder("./src/test/resources/policies/");
	private SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss.SSS");

	private String toXsdDate() {
		StringBuilder buff = new StringBuilder();
		Date date = new Date();
		buff.append(format1.format(date));
		return buff.toString();
	}

	private String toXsdTime() {
		StringBuilder buff = new StringBuilder();
		Date date = new Date();
		buff.append(format2.format(date));
		return buff.append('Z').toString();
	}

	private static final String TARGET = "http://example.com/asset:9898.movie";
	private static final String ACTION = "http://www.w3.org/ns/odrl/2/display";


	// Tests

	/**
	 * This test checks that the current system's date time is greater than 2022-11-04T06:00:13.625668Z and lower than 2022-11-04T23:59:13.625668Z.
	 * This test returns always an empty map of targets and actions
	 * @throws PolicyException
	 * @throws UnsupportedOperatorException
	 * @throws UnsupportedFunctionException
	 */
	@Test
	public void test01() throws Exception {
		String policy = Tests.readPolicy(dir.append("dateTime-ex1.json").toString());
		Map<String, List<String>> result = Tests.solvePolicy(policy);
		Assert.assertTrue(result.isEmpty());
	}

	/**
	 * This test checks that current system's date time is within the temporal space 06:00AM and 23:59PM.
	 * @throws PolicyException
	 * @throws UnsupportedOperatorException
	 * @throws UnsupportedFunctionException
	 */
	@Test
	public void test02() throws Exception {
		String policy = Tests.readPolicy(dir.append("dateTime-ex1.json").toString());
		policy = policy.replace("2022-11-04", toXsdDate());
		policy = policy.replace("2022-11-04", toXsdDate());
		Map<String, List<String>> result = Tests.solvePolicy(policy);
		Assert.assertTrue(result.containsKey(TARGET));
		Assert.assertTrue(result.get(TARGET).get(0).equals(ACTION));
	}

	/**
	 * This test ensures that if the dateTime value in the policy only specifies the date an exception is thrown.
	 * This test always throws a {@link PolicyException}
	 * @throws PolicyException
	 * @throws UnsupportedOperatorException
	 * @throws UnsupportedFunctionException
	 */
	@Test
	public void test03() throws Exception {
		String policy = Tests.readPolicy(dir.append("dateTime-err1.json").toString());
		boolean throwsException = false;
		try {
			Tests.solvePolicy(policy);
		}catch(Exception e) {
			throwsException= true;
		}
		Assert.assertTrue(throwsException);
	}

	/**
	 * This test ensures that if the dateTime value in the policy only specifies the time an exception is thrown.
	 * This test always throws a {@link PolicyException}.
	 * @throws PolicyException
	 * @throws UnsupportedOperatorException
	 * @throws UnsupportedFunctionException
	 */
	@Test
	public void test04() throws Exception {
		String policy = Tests.readPolicy(dir.append("dateTime-err2.json").toString());
		boolean throwsException = false;
		try {
			Tests.solvePolicy(policy);
		}catch(Exception e) {
			throwsException= true;
		}
		Assert.assertTrue(throwsException);
	}

	/**
	 * This test checks that the policy defines the operand correctly, by either putting the full URI ("http://www.w3.org/ns/odrl/2/dateTime") or the string ("dateTime").
	 * This test always throws a {@link PolicyException}.
	 * @throws PolicyException
	 * @throws UnsupportedOperatorException
	 * @throws UnsupportedFunctionException
	 */
	@Test
	public void test05() throws Exception {
		String policy = Tests.readPolicy(dir.append("dateTime-ex2.json").toString());
		policy = policy.replace("2022-11-04", toXsdDate());
		policy = policy.replace("2022-11-04", toXsdDate());
		Map<String, List<String>> allowedTo = Tests.solvePolicy(policy);
		Assert.assertTrue(allowedTo.containsKey(TARGET));
		Assert.assertTrue(allowedTo.get(TARGET).get(0).equals(ACTION));
	}


}
