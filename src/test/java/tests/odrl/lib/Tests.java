package tests.odrl.lib;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.gson.JsonObject;

import odrl.lib.OdrlLib;
import odrl.lib.Policies;
import odrl.lib.exceptions.OdrlRegistrationException;
import odrl.lib.exceptions.PolicyException;
import odrl.lib.exceptions.UnsupportedOperandException;
import odrl.lib.exceptions.UnsupportedOperatorException;
import odrl.lib.operands.Time;
import odrl.lib.operators.GeoSparqlContains;

public class Tests {


	
	public static OdrlLib odrl = new OdrlLib();
	
	
	
	public static Map<String, List<String>> solvePolicy(String policy) throws UnsupportedOperandException, UnsupportedOperatorException, PolicyException, OdrlRegistrationException {
		JsonObject policyJson = Policies.fromJsonld11String(policy);
		
		odrl.registerPrefix("ops", "http://upm.es/operands#");
		odrl.register("ops", new Time());
		
		return odrl.solve(policyJson);
	}


	public static String readPolicy(String name) {
		StringBuilder contentBuilder = new StringBuilder();

		try (Stream<String> stream = Files.lines(Paths.get(name), StandardCharsets.UTF_8)){

		  stream.forEach(s -> contentBuilder.append(s).append("\n"));
		}catch (IOException e){
		  e.printStackTrace();
		}

		return  contentBuilder.toString();
	}
}
