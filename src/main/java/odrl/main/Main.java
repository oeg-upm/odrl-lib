package odrl.main;

import com.google.gson.JsonObject;

import odrl.lib.OdrlLib;
import odrl.lib.Policies;
import odrl.lib.operands.DateTime;
import odrl.lib.operands.Spatial;

public class Main {

	public static String policy = "{\n"
			+ " \"@context\": \"http://www.w3.org/ns/odrl.jsonld\",\n"
			+ " \"@type\": \"Set\",\n"
			+ " \"uid\": \"http://example.com/policy:1010\",\n"
			+ " \"permission\": [{\n"
			+ " 	\"target\": \"http://example.com/asset:9898.movie\",\n"
			+ "	\"action\": \"display\",\n"
			+ "	\"constraint\": [{\n"
			+ "           \"leftOperand\":  \"http://www.w3.org/ns/odrl/2/dateTime\",\n"
			+ "           \"operator\": \"gt\",\n"
			+ "           \"rightOperand\":  { \"@value\": \"2022-11-04T15:09:13.625668Z\", \"@type\": \"xsd:dateTime\" }\n"
			+ "       },{\n"
			+ "           \"leftOperand\": \"http://www.w3.org/ns/odrl/2/dateTime\",\n"
			+ "           \"operator\": \"lt\",\n"
			+ "           \"rightOperand\":  { \"@value\": \"2022-11-04T20:24:13.625668Z\", \"@type\": \"xsd:dateTime\" }\n"
			+ "       }"
			+ "       ]\n"
			+ " }]\n"
			+ "}";
	

	
	public static String policy2 = "{\n"
			+ " \"@context\": \"http://www.w3.org/ns/odrl.jsonld\",\n"
			+ " \"@type\": \"Set\",\n"
			+ " \"uid\": \"http://example.com/policy:1010\",\n"
			+ " \"permission\": [{\n"
			+ " 	\"target\": \"http://example.com/asset:9898.movie\",\n"
			+ "	\"action\": \"display\",\n"
			+ "	\"constraint\": [{\n"
			+ "           \"leftOperand\": \"http://www.w3.org/ns/odrl/2/spatial\",\n"
			+ "           \"operator\": \"eq\",\n"
			+ "           \"rightOperand\":  \"https://www.wikidata.org/wiki/Q183\",\n"
			+ "	   \"comment\": \"i.e Germany\"\n"
			+ "       }]\n"
			+ " }]\n"
			+ "}";
	
	public static void main(String[] args) {
		JsonObject policyJson = Policies.fromJsonld11String(policy);		
		OdrlLib odrl = new OdrlLib();
		OdrlLib.DEBUG=true;
		odrl.registerOperand(new DateTime());
		odrl.registerOperand(new Spatial());
		System.out.println("Access:"+odrl.solve(policyJson));

	}

}
