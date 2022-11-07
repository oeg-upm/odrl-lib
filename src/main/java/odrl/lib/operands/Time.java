package odrl.lib.operands;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase0;

public class Time extends FunctionBase0 implements Operand {

	private SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss");

	@Override
	public NodeValue exec() {
		
		try {
			//NodeValue v = NodeValue.makeDateTime(toXsdDateTime());
			NodeValue v = NodeValue.makeString("xsd:dateTime(\""+toXsdDateTime()+".0Z\")");
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
		buff.append(format1.format(date));
		buff.append('T').append(format2.format(date));
		return buff.toString();
	}

	@Override
	public String getName() {
		return "dateTime";
	}

}
