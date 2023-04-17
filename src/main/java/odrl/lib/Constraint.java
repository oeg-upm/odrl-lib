package odrl.lib;

import java.util.Map;
import java.util.Optional;

import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import odrl.lib.exceptions.UnsupportedOperandException;
import odrl.lib.exceptions.UnsupportedOperatorException;

class Constraint {

	private static final Logger LOG = LoggerFactory.getLogger(Constraint.class);

	
	
	private RDFNode leftNode;
	private RDFNode operatorNode;
	private RDFNode rightNode;

	private Map<String, String> functions;
	private Map<String, String> prefixes;

	public Constraint(RDFNode leftNode, RDFNode operatorNode, RDFNode rightNode, Map<String, String> functions, Map<String, String> prefixes) {
		super();
	
		this.leftNode = leftNode;
		this.operatorNode = operatorNode;
		this.rightNode = rightNode;
		this.functions = functions;
		this.prefixes = prefixes;
	}
	public RDFNode getLeftNode() {
		return leftNode;
	}
	public void setLeftNode(RDFNode leftNode) {
		this.leftNode = leftNode;
	}
	public RDFNode getOperatorNode() {
		return operatorNode;
	}
	public void setOperatorNode(RDFNode operatorNode) {
		this.operatorNode = operatorNode;
	}
	public RDFNode getRightNode() {
		return rightNode;
	}
	public void setRightNode(RDFNode rightNode) {
		this.rightNode = rightNode;
	}

	public JsonObject toJson() {
		JsonObject constraint = new JsonObject();
		constraint.addProperty("leftOperand", leftNode.toString());
		constraint.addProperty("operator", operatorNode.toString());
		constraint.addProperty("rightOperand", rightNode.toString());
		return constraint;
	}

	public boolean solve() {
		String bind = toSPARQL();
		
		StringBuilder SPARQL = new StringBuilder();
		prefixes.entrySet().forEach(entry -> SPARQL.append("PREFIX ").append(entry.getKey()).append(": <").append(entry.getValue()).append("> \n"));
		SPARQL.append("SELECT ?bind1 WHERE { \n").append(bind).append("}\n");
		
		return evaluateQuery(SPARQL.toString());
	}

	private boolean evaluateQuery(String query) {
		if(OdrlLib.debug)
			LOG.info("Constraint SPARQL: "+query);
		Model model = ModelFactory.createDefaultModel();
		ResultSet rs = QueryExecutionFactory.create(QueryFactory.create(query), model).execSelect();
		int solutions = 0;
		boolean allowed = false;
		while(rs.hasNext()) {
			if(solutions==1)
				break;
			allowed = rs.next().get("bind1").asLiteral().getBoolean();
			solutions++;
		}
		if(OdrlLib.debug)
			LOG.info("Constraint SPARQL resolved as: "+allowed);
		// TODO; if solutions is 0 exception
		return allowed;
	}

	public String toSPARQL() {
		try {
			String left = solveOperant(this.leftNode);
			String right = solveOperant(this.rightNode);
			String operator = solveOperator(this.operatorNode);
			if(functions.containsKey(operator)){
				return concat(" BIND ( "+shorcutOperator(operator)+"("+left+","+right+") AS ?bind1 ) \n");
			}else {
				return concat(" BIND ( "+left+" "+operator+" "+right+" AS ?bind1 ) \n");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method replaces the operator URI for its associated prefix
	 * @param operator
	 * @return
	 */
	private String shorcutOperator(String operator) {
		String result = operator;
		Optional<String> value = prefixes.entrySet().parallelStream()
				.filter(entry -> operator.startsWith(entry.getValue()))
				.map(entry -> operator.replace(entry.getValue(), entry.getKey()+":"))
				.findFirst();
			if(value.isPresent())
				result = value.get();
		return result;
	}
	
	/**
	 * This method replaces any ODRL operator (eq, gt, gteq, hasPart, isA, isAllOf,
	 * isAnyOf, isNoneOf, isPartOf, lt, lteq, neq) for a SPARQL native operator
	 *
	 * @param operatorNode
	 * @param left
	 * @param right
	 * @return
	 * @throws UnsupportedOperatorException
	 */
	private String solveOperator(RDFNode operatorNode) throws UnsupportedOperatorException {
		if(operatorNode.isResource()) {
			String operator = operatorNode.toString();
			if (operator.equals("http://www.w3.org/ns/odrl/2/eq"))
				return " = ";
			if (operator.equals("http://www.w3.org/ns/odrl/2/gt"))
				return " > ";
			if (operator.equals("http://www.w3.org/ns/odrl/2/gteq"))
				return " >= ";
			if (operator.equals("http://www.w3.org/ns/odrl/2/lt"))
				return " < ";
			if (operator.equals("http://www.w3.org/ns/odrl/2/lteq"))
				return " <= ";
			if (operator.equals("http://www.w3.org/ns/odrl/2/neq"))
				return " != ";
			if(functions.containsKey(operator)) {
				return operator;
			}
				

		}
		// hasPart, isA, isAllOf, isAnyOf, isNoneOf, isPartOf,
		throw new UnsupportedOperatorException(concat("Specified operator (" , operatorNode.toString() , ") not supported."));
	}

	private String solveOperant(RDFNode operand) throws UnsupportedOperandException {
		if (operand.isLiteral()) {
			Literal literal = operand.asLiteral();
			if(!literal.getLanguage().isEmpty()) {
				return concat("\"",literal.getString(),"\"@",literal.getLanguage());
			}
			return concat("\"",literal.getString(),"\"^^<",literal.getDatatypeURI()+">");


		} else if (operand.isResource()) {
			String operandStr=operand.toString();
			if(functions.containsKey(operandStr)) {
				return concat(functions.get(operandStr),"()");
			}else {
				return concat("<",operandStr,">");
			}
		}else {
			throw new UnsupportedOperandException(concat("Specified operand (" , operand.toString() , ") not supported."));
		}
	}

	public static final String concat(String... args) {
		StringBuilder builder = new StringBuilder();
		for (String arg : args) {
			builder.append(arg);
		}
		return builder.toString();
	}


}
