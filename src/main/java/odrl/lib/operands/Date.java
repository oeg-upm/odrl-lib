package odrl.lib.operands;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase0;

public class Date extends FunctionBase0 implements Operand {

	@Override
	public String getName() {
		return "date";
	}

	@Override
	public NodeValue exec() {
		// TODO Auto-generated method stub
		return null;
	}

}
