package tests.odrl.lib;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.gson.JsonObject;

import odrl.lib.model.OdrlLib;
import odrl.lib.model.Policies;
import odrl.lib.model.exceptions.EvaluationException;
import odrl.lib.model.exceptions.OdrlRegistrationException;
import odrl.lib.model.exceptions.OperandException;
import odrl.lib.model.exceptions.OperatorException;
import odrl.lib.model.exceptions.UnsupportedFunctionException;
import odrl.lib.model.functions.Time;

public class Tests {

	private Tests() {
		super();
	}


	public static OdrlLib odrl = new OdrlLib();

	
	
	public static Map<String, List<String>> solvePolicy(String policy) throws UnsupportedFunctionException, OdrlRegistrationException, OperandException, OperatorException, EvaluationException {
		JsonObject policyJson = Policies.fromJsonld11String(policy);

		odrl.registerPrefix("ops", "http://upm.es/operands#");
		odrl.register("ops", new Time());
		odrl.registerNative();
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
