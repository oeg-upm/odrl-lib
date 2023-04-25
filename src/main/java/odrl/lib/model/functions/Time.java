package odrl.lib.model.functions;

import java.text.SimpleDateFormat;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase0;
import java.util.Date;
public class Time extends FunctionBase0 implements IFunction {

	private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

	@Override
	public NodeValue exec() {

		try {
			//NodeValue v = NodeValue.makeDateTime(toXsdDateTime());
			NodeValue v = NodeValue.makeNode(toXsdDateTime(), null,"http://www.w3.org/2001/XMLSchema#time");


			return v;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String toXsdDateTime() {
		StringBuilder buff = new StringBuilder();
		Date date = new Date();
		buff.append(format.format(date));
		return buff.toString();
	}


	@Override
	public String getName() {
		return "time";
	}

}
