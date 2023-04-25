package tests.odrl.lib.operators;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import odrl.lib.model.OdrlLib;
import odrl.lib.model.exceptions.OdrlRegistrationException;
import tests.odrl.lib.Tests;

public class TestGeoSparqlContains {

	private StringBuilder dir = new StringBuilder("./src/test/resources/policies/");



	@Before
	public void setup() throws OdrlRegistrationException {
		Tests.odrl.registerGeof();
		Tests.odrl.registerSpatial();
		OdrlLib.setDebug(true);
	}



	@Test
	public void test01() throws Exception  {

		String policy = Tests.readPolicy(dir.append("geosparql-ex1.json").toString());
		Map<String, List<String>> result = Tests.solvePolicy(policy);
		System.out.println(result);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void test02() throws Exception  {
		String policy = Tests.readPolicy(dir.append("geosparql-ex2.json").toString());
		Map<String, List<String>> result = Tests.solvePolicy(policy);
		System.out.println(result);
		Assert.assertTrue(result.containsKey("http://example.com/asset:9898.movie"));
	}

	@Test
	public void test03() throws Exception  {
		String policy = Tests.readPolicy(dir.append("geosparql-ex3.json").toString());
		
		boolean exception = false;
		try {
			Tests.solvePolicy(policy);
		}catch(Exception e) {
			exception = true;
		}
		Assert.assertTrue(exception);
	}

	
	@Test
	public void test04() throws Exception  {
		String policy = Tests.readPolicy(dir.append("geosparql-ex4.json").toString());
		boolean exception = false;
		try {
			Tests.solvePolicy(policy);
		}catch(Exception e) {
			exception = true;
			e.printStackTrace();
		}
		Assert.assertTrue(exception);
	}
	
	@Test
	public void test05() throws Exception  {
		String policy = Tests.readPolicy(dir.append("geosparql-ex5.json").toString());
		Map<String, List<String>> result = Tests.solvePolicy(policy);
		System.out.println(result);
		Assert.assertTrue(result.isEmpty());
	}
}
