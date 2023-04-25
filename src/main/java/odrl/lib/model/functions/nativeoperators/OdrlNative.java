package odrl.lib.model.functions.nativeoperators;

import java.io.ByteArrayOutputStream;

import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.apache.jena.sparql.resultset.ResultsFormat;

import odrl.lib.model.exceptions.EvaluationException;
import odrl.lib.model.exceptions.RuntimeEvaluationException;
import odrl.lib.model.functions.IFunction;
import sparql.streamline.core.Sparql;

public abstract class OdrlNative extends FunctionBase2 implements IFunction{

	private static final String QUERY = "SELECT ?bind {  BIND ( #q1# #op# #q2#  AS ?bind ) }";
	private static final String QUERY_REPLACEMENT_1 = "#q1#";
	private static final String QUERY_REPLACEMENT_2 = "#q2#";
	private static final String QUERY_REPLACEMENT_3 = "#op#";
	
	protected Boolean solveOperator(NodeValue v1, NodeValue v2, String opName, String op) throws RuntimeEvaluationException {
		Boolean result = false;
		try {
			String formattedQuery = QUERY.replace(QUERY_REPLACEMENT_1, v1.toString()).replace(QUERY_REPLACEMENT_2, v2.toString()).replace(QUERY_REPLACEMENT_3, op);
			System.out.println(formattedQuery);
			ByteArrayOutputStream out = Sparql.queryModel(formattedQuery, ModelFactory.createDefaultModel(), ResultsFormat.FMT_RS_CSV, null);
			String rawString = new String(out.toByteArray());
			System.out.println(rawString);
			String rawBoolean = rawString.split("\n")[1].trim();
			if(rawBoolean.isEmpty() && v1.getDatatypeURI().equals(v2.getDatatypeURI()) )
				throw new EvaluationException("Provided operands have datatypes incompatible for the operand "+opName);
			result = Boolean.valueOf(rawBoolean);
			out.close();
		}catch(Exception e) {
			throw new RuntimeEvaluationException(e.getMessage());
		}
		return result;
	}
		
	
}
