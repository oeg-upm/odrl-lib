package odrl.lib.operands;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase0;

public class Time extends FunctionBase0 implements Operand {

	private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

	@Override
	public NodeValue exec() {

		try {
			//NodeValue v = NodeValue.makeDateTime(toXsdDateTime());
			NodeValue v = NodeValue.makeNode(toXsdDateTime(), null,"http://www.w3.org/2001/XMLSchema#time");

			System.out.println(">"+v);
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
