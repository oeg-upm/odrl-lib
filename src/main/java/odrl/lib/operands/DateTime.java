package odrl.lib.operands;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase0;

/**
 * This class implements the dateTime LeftOperand from the ODRL specification. It allows to compare xsd:dateTime values, however it does not work with only time or date for which the {@link Time} and {@link Date} operands are recommended. {@link Time} and {@link Date} operands do nto belong to the ODRL standard.
 * @see <a href="https://www.w3.org/TR/odrl-vocab/#term-dateTime">ODRL LeftOperand dateTime</a>
 * @author <a href="mailto:andreajesus.cimmino@upm.es">Andrea Cimmino</a>
 */
public class DateTime extends FunctionBase0 implements Operand {

	private SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss.SSS");


	@Override
	public NodeValue exec() {
		NodeValue v = NodeValue.makeDateTime(toXsdDateTime());
		System.out.println("DateTime.class > "+ v);
		return v;

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
