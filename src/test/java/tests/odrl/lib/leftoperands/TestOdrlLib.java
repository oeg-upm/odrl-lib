package tests.odrl.lib.leftoperands;

import org.junit.Assert;
import org.junit.Test;

import odrl.lib.model.OdrlLib;
import odrl.lib.model.exceptions.OdrlRegistrationException;
import odrl.lib.model.functions.Time;

public class TestOdrlLib {

	private StringBuilder dir = new StringBuilder("./src/test/resources/policies/");

	// Tests

	/**
	 * This test ensures that before registering any operand or operator a prefix
	 * must be registered
	 */
	@Test
	public void test01() {
		boolean exceptionThrown = false;
		try {
			OdrlLib odrl = new OdrlLib();
			odrl.register("op", new Time());
		} catch (OdrlRegistrationException e) {
			exceptionThrown = true;
		}
		Assert.assertTrue(exceptionThrown);
	}

	/**
	 * This test checks that after registering a prefix an operand or operator can
	 * be registered
	 */
	@Test
	public void test02() {
		boolean exceptionThrown = true;
		try {
			OdrlLib odrl = new OdrlLib();
			odrl.registerPrefix("op", "http://test.es/operands#");
			odrl.register("op", new Time());
		} catch (OdrlRegistrationException e) {
			exceptionThrown = false;
		}
		Assert.assertTrue(exceptionThrown);
	}

	/**
	 * This test checks that prefixes are correctly added
	 */
	@Test
	public void test04() {
		OdrlLib odrl = new OdrlLib();
		odrl.registerPrefix("op", "http://test.es/operands#");
		Assert.assertTrue(odrl.getPrefixes().containsKey("op"));
	}

	/**
	 * This test checks that operands or operators are correctly added
	 * be registered
	 */
	@Test
	public void test05() {
		boolean correctlyAdded = false;
		try {
			OdrlLib odrl = new OdrlLib();
			odrl.registerPrefix("op", "http://test.es/operands#");
			odrl.register("op", new Time());
			correctlyAdded = odrl.getFunctions().contains("op:time");
		} catch (OdrlRegistrationException e) {

			correctlyAdded = false;
		}
		Assert.assertTrue(correctlyAdded);
	}




}
