package odrl.lib;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import org.apache.jena.ext.com.google.common.collect.Maps;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.apache.jena.sparql.resultset.ResultsFormat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import odrl.lib.operands.Operand;
import odrl.lib.operators.Operator;
import sparql.streamline.core.Sparql;

public class OdrlLib {

	private static String NS_OPERATORS = "https://andreacimminoarriaga.github.io/sparql-streamline/functions#";
	private static String NS_OPERANDS = "http://www.w3.org/ns/odrl/2/";
	public static Boolean DEBUG = false;
	private Map<String,String> prefixes = Maps.newHashMap();
	
	public OdrlLib() {
		prefixes.put("fn", NS_OPERATORS);
		prefixes.put("odrl", NS_OPERANDS);
	}
	
	
	public void registerOperand(Operand operand) {
		FunctionRegistry.get().put(NS_OPERANDS+operand.getName(), operand.getClass()) ;
	}
	
	public void registerOperator(Operator operator) {
		FunctionRegistry.get().put(NS_OPERATORS+operator.getName(), operator.getClass()) ;
	}
	
	public boolean solve(JsonObject policyJson) {
		Set<Boolean> allowed = Sets.newHashSet();
		policyJson.get("permission").getAsJsonArray().forEach(policy -> allowed.add(solvePermission(policy.getAsJsonObject())));
		return !allowed.parallelStream().anyMatch(elem -> elem==false);
	}
	
	private boolean solvePermission(JsonObject permission) {
		Set<Boolean> allowed = Sets.newHashSet();
		permission.get("constraint").getAsJsonArray().forEach(constraint -> allowed.add(solveConstraint(constraint.getAsJsonObject())));
		return !allowed.parallelStream().anyMatch(elem -> elem==false);
	}

	private Boolean solveConstraint(JsonObject constraint) {
		JsonElement leftOperand = constraint.get("leftOperand");
		String operator = constraint.get("operator").getAsString();
		JsonElement rightOperand = constraint.get("rightOperand");
		try {
			return solveConstraint(leftOperand, operator, rightOperand);
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
			return false;
		}
	}

	private Boolean solveConstraint(JsonElement leftOperant, String operator, JsonElement rightOperand) throws Exception {
		String left = solveOperant(leftOperant);
		String right = solveOperant(rightOperand);
		String function = solveOperator(operator);
		StringBuilder SPARQL = new StringBuilder();
		this.prefixes.entrySet().parallelStream().forEach(entry -> SPARQL.append("PREFIX "+entry.getKey()+": <"+entry.getValue()+">\n"));
		SPARQL.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n");

		SPARQL.append("SELECT ?bind1 WHERE { \n");
				if(function !=null ) {
					SPARQL.append(" BIND ( "+left+" "+function+" "+right+" AS ?bind1 ) \n");
				}else {
					SPARQL.append(" BIND ( op:"+operator+"("+left+","+right+") AS ?bind1 ) \n");
				}
				SPARQL.append("}\n");
		if(DEBUG)
			System.out.println(SPARQL);
		try {
		String queryResult = new String(Sparql.queryService(SPARQL.toString(), ResultsFormat.FMT_RS_CSV, null).toByteArray());
		if(DEBUG)
			System.out.println(queryResult);
		return queryResult.contains("true");
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * This method replaces any ODRL operator (eq, gt, gteq, hasPart, isA, isAllOf, isAnyOf, isNoneOf, isPartOf, lt, lteq, neq) for a SPARQL native operator
	 * @param operator
	 * @param left
	 * @param right
	 * @return
	 */
	private String solveOperator(String operator) {
		if(operator.equals("eq")) return " = ";
		if(operator.equals("gt")) return " > ";
		if(operator.equals("gteq")) return " >= ";
		if(operator.equals("lt")) return " < ";
		if(operator.equals("lteq")) return " <= ";
		if(operator.equals("neq")) return " != ";
		// hasPart, isA, isAllOf, isAnyOf, isNoneOf, isPartOf, 
		return null;
	}
	
	
	
	private String solveOperant(JsonElement operand) throws Exception {
		if(operand.isJsonPrimitive()) {
			String primitiveOperand = operand.getAsString();	
			// Finds within registered operands (internal or external) 
			Optional<Entry<String, String>> uriOpt = this.prefixes.entrySet().parallelStream().filter(entry -> primitiveOperand.startsWith(entry.getValue())).findFirst();
			if(uriOpt.isPresent()) {
				Entry<String,String> entry = uriOpt.get();
				return operand.getAsString().replace(entry.getValue(), entry.getKey()+":")+"()";
			}else {
				// Constant case
				try {
					new URI(primitiveOperand);
					return  "\""+primitiveOperand+"\"";
				}catch(Exception e) {
					//skip
				}
				return primitiveOperand;
			}
		}else if(operand.isJsonObject() && operand.getAsJsonObject().has("@value")) {
			// Literal case
			String value = operand.getAsJsonObject().get("@value").toString();
			if(operand.getAsJsonObject().has("@type"))
				value = concat(operand.getAsJsonObject().get("@type").getAsString(),"(",value,")");
			return value;
		}
		throw new Exception("");
	}

	public static final String concat(String... args) {
		StringBuilder builder = new StringBuilder();
		for (String arg : args) {
			builder.append(arg);
		}
		return builder.toString();
	}



	
	
}
