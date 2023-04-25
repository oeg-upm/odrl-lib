package tests.odrl.lib;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.jsonldjava.shaded.com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import odrl.lib.model.Action;
import odrl.lib.model.Constraint;
import odrl.lib.model.OdrlLib;
import odrl.lib.model.Permission;
import odrl.lib.model.Policies;
import odrl.lib.model.exceptions.EvaluationException;
import odrl.lib.model.exceptions.OdrlRegistrationException;
import odrl.lib.model.exceptions.OperandException;
import odrl.lib.model.exceptions.OperatorException;
import odrl.lib.model.exceptions.UnsupportedFunctionException;

public class ParsingOperatorsTests {

	private StringBuilder dir = new StringBuilder("./src/test/resources/policies/");

	@Before
	public void setup() throws OdrlRegistrationException {
		Tests.odrl.registerGeof();
		Tests.odrl.registerSpatial();
		OdrlLib.setDebug(true);
	}

	private List<Constraint> fetchConstraints(String file, int numPermissions, int numActions, int numConstraints)
			throws UnsupportedFunctionException, OperandException, OperatorException, EvaluationException {
		String policy = Tests.readPolicy(file);
		JsonObject policyJson = Policies.fromJsonld11String(policy);
		List<Permission> permissions = Tests.odrl.mapToPermissions(policyJson);

		Assert.assertTrue(permissions.size() == 1);
		Permission permission = permissions.get(0);
		Assert.assertTrue(permission.getActions().size() == 1);
		Action action = permission.getActions().iterator().next();
		Assert.assertTrue(action.getConstraints().size() == 1);
		return Lists.newArrayList(action.getConstraints());
	}

	// TEST1: LeftOperand is Constant, i.e., a string value 55
	// TEST2: LeftOperand is Constant, i.e., a nested object with @value & @type

	// TEST3: LeftOperand is 0-ary function (full-uri), i.e., a string with
	// odrl:datetime
	@Test
	public void test03() throws Exception {
		List<Constraint> constraints = fetchConstraints(dir.append("parsing-ex0.json").toString(), 1, 1, 1);
		Constraint constraint = constraints.get(0);
		Assert.assertFalse(constraint.solve(Tests.odrl.getPrefixes()));
	}

	// TEST4: LeftOperand is 0-ary function(full-uri), i.e., a nested object with
	// @id and NO arguments
	@Test
	public void test04() throws Exception {
		List<Constraint> constraints = fetchConstraints(dir.append("parsing-ex1.json").toString(), 1, 1, 1);
		Constraint constraint = constraints.get(0);
		Assert.assertFalse(constraint.solve(Tests.odrl.getPrefixes()));
	}

	// TEST4: LeftOperand is 0-ary function(prefixed-uri), i.e., a nested object with @id
	// and NO arguments
	@Test
	public void test06() throws Exception {
		List<Constraint> constraints = fetchConstraints(dir.append("parsing-ex2.json").toString(), 1, 1, 1);
		Constraint constraint = constraints.get(0);
		Assert.assertFalse(constraint.solve(Tests.odrl.getPrefixes()));
	}

	// TEST4: LeftOperand is 0-ary function(prefixed-uri), i.e., a nested object with @id
	// and SOME arguments this MUST fail because the funcion is not meant to receive arguments
	@Test
	public void test07() throws Exception {
		String expected = "BIND ( odrl:eq(odrl:dateTime(\"33\"^^<http://www.w3.org/2001/XMLSchema#string>),\"2022-11-04T06:00:13.625668Z\"^^<http://www.w3.org/2001/XMLSchema#dateTime>) AS ?bind1 )";
		List<Constraint> constraints = fetchConstraints(dir.append("parsing-ex3.json").toString(), 1, 1, 1);
		Constraint constraint = constraints.get(0);
		Assert.assertTrue(constraint.toSPARQL().contains(expected));
		boolean exception = false;
		try {
			constraint.solve(Tests.odrl.getPrefixes());
		}catch(Exception e) {
			exception = true;
		}
		Assert.assertTrue(exception);
	}

	@Test
	public void test08() throws Exception {
		String expected = " BIND ( odrl:eq(odrl:dateTime(\"33\"^^<http://www.w3.org/2001/XMLSchema#integer>),\"2022-11-04T06:00:13.625668Z\"^^<http://www.w3.org/2001/XMLSchema#dateTime>) AS ?bind1 )";
		List<Constraint> constraints = fetchConstraints(dir.append("parsing-ex4.json").toString(), 1, 1, 1);
		Constraint constraint = constraints.get(0);
		Assert.assertTrue(constraint.toSPARQL().contains(expected));
		boolean exception = false;
		try {
			constraint.solve(Tests.odrl.getPrefixes());
		}catch(Exception e) {
			exception = true;
		}
		Assert.assertTrue(exception);
	}

	@Test
	public void test09() throws Exception {
		String expected = "BIND ( odrl:eq(odrl:dateTime(units:meter),\"2022-11-04T06:00:13.625668Z\"^^<http://www.w3.org/2001/XMLSchema#dateTime>) AS ?bind1 )";
		List<Constraint> constraints = fetchConstraints(dir.append("parsing-ex5.json").toString(), 1, 1, 1);
		Constraint constraint = constraints.get(0);
		Assert.assertTrue(constraint.toSPARQL().contains(expected));
		boolean exception = false;
		try {
			constraint.solve(Tests.odrl.getPrefixes());
		}catch(Exception e) {
			exception = true;
		}
		Assert.assertTrue(exception);
	}


	// Parsing spatialF with wrong parameters
	@Test
	public void test10() throws Exception {
		String expected = " BIND ( odrl:gt(spatialF:distance('POLYGON((-3.840070031583308 40.405675773960866,-3.839853107929229 40.40574011056009,-3.839842379093169 40.40571994155346,-3.839847072958945 40.40571840972993,-3.839840702712535 40.405706155140365,-3.840056620538234 40.40564947763477,-3.840070031583308 40.405675773960866))', 'POINT((-3.839907742614103 40.40570902334372))'),\"50\"^^<http://www.w3.org/2001/XMLSchema#integer>) AS ?bind1 )";
		List<Constraint> constraints = fetchConstraints(dir.append("geosparql-ex3.json").toString(), 1, 1, 1);
		Constraint constraint = constraints.get(0);
		Assert.assertTrue(constraint.toSPARQL().contains(expected));
		boolean exception = false;
		try {
			constraint.solve(Tests.odrl.getPrefixes());
		}catch(Exception e) {
			exception = true;
		}
		Assert.assertTrue(exception);
	}



		@Test
		public void test11() throws Exception {
			boolean exception = false;
			try {
				List<Constraint> constraints = fetchConstraints(dir.append("parsing-ex6.json").toString(), 1, 1, 1);
				Constraint constraint = constraints.get(0);

				constraint.solve(Tests.odrl.getPrefixes());
			}catch(Exception e) {
				e.printStackTrace();
				exception = true;
			}
			Assert.assertTrue(exception);
		}
		@Test
		public void test12() throws Exception {
			boolean exception = false;
			try {
				List<Constraint> constraints = fetchConstraints(dir.append("parsing-ex7.json").toString(), 1, 1, 1);
				Constraint constraint = constraints.get(0);

				constraint.solve(Tests.odrl.getPrefixes());
			}catch(Exception e) {
				e.printStackTrace();
				exception = true;
			}
			Assert.assertTrue(exception);
		}
}
