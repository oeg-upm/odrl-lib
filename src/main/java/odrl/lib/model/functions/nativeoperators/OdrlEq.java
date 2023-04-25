package odrl.lib.model.functions.nativeoperators;

import org.apache.jena.sparql.expr.NodeValue;

public class OdrlEq  extends OdrlNative{

	@Override
	public String getName() {
		return "eq";
	}

		
	@Override
	public NodeValue exec(NodeValue v1, NodeValue v2) {
		Boolean result = solveOperator(v1, v2, getName(), " = ");
		return NodeValue.makeNodeBoolean(result);
	}

	
}
