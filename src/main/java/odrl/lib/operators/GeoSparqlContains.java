package odrl.lib.operators;

import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase0;
import org.apache.jena.sparql.function.FunctionBase2;
import org.apache.jena.sparql.function.FunctionEnv;

public class GeoSparqlContains implements Operator {

	// Operator from https://jena.apache.org/documentation/geosparql/index.html
	private final String NAME = "sfContains"; 
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void build(String uri, ExprList args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NodeValue exec(Binding binding, ExprList args, String uri, FunctionEnv env) {
		// TODO Auto-generated method stub
		return null;
	}


	
	
}
