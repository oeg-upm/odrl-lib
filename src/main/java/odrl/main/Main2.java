package odrl.main;

import com.google.gson.JsonObject;

import helio.blueprints.exceptions.ExtensionNotFoundException;
import helio.blueprints.exceptions.IncompatibleMappingException;
import helio.blueprints.exceptions.IncorrectMappingException;
import helio.blueprints.exceptions.TranslationUnitExecutionException;
import odrl.lib.OdrlLib;
import odrl.lib.Policies;
import odrl.lib.SIoTService;
import odrl.lib.operands.DateTime;
import odrl.lib.operands.Spatial;

public class Main2 {

	public static String policy1 = "<#assign weatherConf = \"{\\\"url\\\" : \\\"https://api.open-meteo.com/v1/forecast?latitude=40.4050099&longitude=-3.839519&hourly=temperature_2m\\\"}\">\n"
			+ "<#assign weather=providers(\"URLProvider\", weatherConf)?eval>\n"
			+ "<#assign currentTime = .now?time?keep_before(\":\")>\n"
			+ "<#assign tmp=\"ERROR\">\n"
			+ "<#list weather.hourly.time as elem>\n"
			+ "<#if elem?contains(\"T\"+currentTime)> <#assign tmp=weather.hourly.temperature_2m[elem?index]> <#break></#if>\n"
			+ "</#list>\n"
			+ "\n"
			+ "{\n"
			+ " \"@context\": \"http://www.w3.org/ns/odrl.jsonld\",\n"
			+ " \"@type\": \"Set\",\n"
			+ " \"uid\": \"http://example.com/policy:1010\",\n"
			+ " \"permission\": [{\n"
			+ " 	\"target\": \"http://example.com/asset:9898.movie\",\n"
			+ "	\"action\": \"display\",\n"
			+ "	\"constraint\": [{\n"
			+ "           \"leftOperand\":  \"[=tmp]\",\n"
			+ "           \"operator\": \"gt\",\n"
			+ "           \"rightOperand\":  { \"@value\": \"35\", \"@type\": \"xsd:dateTime\" }\n"
			+ "       } ]\n"
			+ " }]\n"
			+ "}";
		
	public static void main(String[] args) throws IncompatibleMappingException, TranslationUnitExecutionException, IncorrectMappingException, ExtensionNotFoundException {
		
		String cleanPolicy = SIoTService.solveSIoTPolicy(policy1, null);
		System.out.println(cleanPolicy);
		JsonObject policyJson = Policies.fromJsonld11String(cleanPolicy);		
		OdrlLib odrl = new OdrlLib();
		OdrlLib.DEBUG=false;
		odrl.registerOperand(new DateTime());
		odrl.registerOperand(new Spatial());
		System.out.println("Access:"+odrl.solve(policyJson));
		SIoTService.close();
	}

}
