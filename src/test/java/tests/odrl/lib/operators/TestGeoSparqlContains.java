package tests.odrl.lib.operators;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import odrl.lib.OdrlLib;
import odrl.lib.exceptions.OdrlRegistrationException;
import tests.odrl.lib.Tests;

public class TestGeoSparqlContains {

	private StringBuilder dir = new StringBuilder("./src/test/resources/policies/");


	
	@Before
	public void setup() throws OdrlRegistrationException {
		Tests.odrl.registerPrefix("geof", "http://www.opengis.net/def/function/geosparql/");
		Tests.odrl.register("geof", "sfContains");
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
	

}
