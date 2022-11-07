package odrl.lib.operands;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase0;

public class DateTime extends FunctionBase0 implements Operand {

	private SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss.SSS");

	@Override
	public NodeValue exec() {
		
		try {
			NodeValue v = NodeValue.makeDateTime(toXsdDateTime());
			//NodeValue v = NodeValue.makeDateTime("2022-11-04T03:09:13.625668Z");
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
		return buff.append('Z').toString();
	}

	@Override
	public String getName() {
		return "dateTime";
	}

}
