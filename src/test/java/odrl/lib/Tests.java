package odrl.lib;

import com.google.gson.JsonObject;

import odrl.lib.operands.DateTime;
import odrl.lib.operands.Spatial;

public class Tests {

	
	protected static boolean solvePolicy(String policy) {
		JsonObject policyJson = Policies.fromJsonld11String(policy);		
		OdrlLib odrl = new OdrlLib();
		OdrlLib.DEBUG=false;
		odrl.registerOperand(new DateTime());
		odrl.registerOperand(new Spatial());
		return odrl.solve(policyJson);
	}
}
