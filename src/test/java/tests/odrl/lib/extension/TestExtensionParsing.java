package tests.odrl.lib.extension;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import odrl.lib.model.OdrlLib;
import odrl.lib.model.exceptions.OdrlRegistrationException;
import tests.odrl.lib.Tests;

public class TestExtensionParsing {



	private StringBuilder dir = new StringBuilder("./src/test/resources/extension/");

	@Before
	public void setup() throws OdrlRegistrationException {
		Tests.odrl.registerGeof();
		Tests.odrl.registerSpatial();
		OdrlLib.setDebug(true);
	}



	@Test
	public void test01() throws Exception  {

		String policy = Tests.readPolicy(dir.append("example-operand.json").toString());
		Map<String, List<String>> result = Tests.solvePolicy(policy);
		System.out.println(result);
		Assert.assertTrue(result.isEmpty());
	}
}
