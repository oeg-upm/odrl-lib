package odrl.lib.model;

import java.util.Map;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import odrl.lib.model.exceptions.EvaluationException;
import odrl.lib.model.exceptions.OperatorException;
import odrl.lib.model.nodes.IOperand;
import odrl.lib.model.nodes.OperandFunction;

public class Constraint {

	private static final Logger LOG = LoggerFactory.getLogger(Constraint.class);

	private OperandFunction operatorNode;

	public Constraint(OperandFunction operatorNode) throws OperatorException {
		super();
		validOperator(operatorNode);
		this.operatorNode = operatorNode;
	}

	private void validOperator(OperandFunction operatorNode) throws OperatorException {
		if(operatorNode.getArguments().size()!=2)
			throw new OperatorException("Provided operator "+(this.operatorNode.getFunction())+" ");
	}


	public IOperand getLeftNode() {
		return operatorNode.getArguments().get(0);
	}

	public void setLeftNode(IOperand leftNode) {
		operatorNode.getArguments().set(0, leftNode);
	}

	public IOperand getRightNode() {
		return operatorNode.getArguments().get(1);
	}

	public void setRightNode(IOperand rightNode) {
		operatorNode.getArguments().set(1, rightNode);
	}


	public JsonObject toJson() {
		JsonObject constraint = new JsonObject();
		constraint.addProperty("leftOperand", getLeftNode().toString());
		constraint.addProperty("operator", operatorNode.toString());
		constraint.addProperty("rightOperand", getRightNode().toString());
		return constraint;
	}


	public boolean solve(Map<String, String> prefixes) throws EvaluationException {
		String bind = toSPARQL();
		StringBuilder sparqlQuery = new StringBuilder();
		prefixes.entrySet().forEach(entry -> sparqlQuery.append("PREFIX ").append(entry.getKey()).append(": <").append(entry.getValue()).append("> \n"));
		sparqlQuery.append("SELECT ?bind1 WHERE { \n").append(bind).append("}\n");
		return evaluateQuery(sparqlQuery.toString());
	}

	private boolean evaluateQuery(String query) throws EvaluationException {
		if(OdrlLib.debug)
			LOG.info("Constraint SPARQL: "+query);

		boolean allowed = false;
		QueryExecution qexec = null;
		ResultSet rs = null;
		try {
			Model model = ModelFactory.createDefaultModel();
			qexec = QueryExecutionFactory.create(QueryFactory.create(query), model);
			rs = qexec.execSelect();
			if(rs.hasNext()) {
				RDFNode qs = rs.next().get("bind1");
				if(qs!=null) {
					allowed = qs.asLiteral().getBoolean();
				}else {
					throw new EvaluationException("Policy could not be evaluated due to the fact that either two non-compatible operands in terms of datatypes were provided or the operands lack of datatypes needed by the operand to evaluate them");
				}

			}
			rs.close();
			qexec.close();
		}catch(Exception e) {
			e.printStackTrace();
			throw new EvaluationException(e.getMessage());
		}finally {
			if(qexec!=null) qexec.close();
			if(rs!=null) rs.close();
		}

		if(OdrlLib.debug)
			LOG.info("Constraint SPARQL resolved as: "+allowed);
		return allowed;
	}


	public String toSPARQL() {
		try {
			String operator = this.operatorNode.toSPARQL();
			return concat(" BIND ( "+operator+" AS ?bind1 ) \n");
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}



	private boolean isGeoFunction(String operand) {
		return operand.startsWith(OdrlLib.GEOF) || operand.startsWith(OdrlLib.SPATIAL) || operand.startsWith(OdrlLib.SPATIALF);
	}

	public static final String concat(String... args) {
		StringBuilder builder = new StringBuilder();
		for (String arg : args) {
			builder.append(arg);
		}
		return builder.toString();
	}


}
