package odrl.lib.model.nodes;

import java.util.List;

import org.apache.commons.compress.utils.Lists;

public class OperandFunction implements IOperand {

	private String function;
	private List<IOperand> arguments;
	private Boolean isStringFunction = false;

	public OperandFunction(String function)  {
		super();
		this.function = function;
		this.arguments = Lists.newArrayList();
	}

	public OperandFunction(String function, List<IOperand> arguments, boolean isStringFunction) {
		super();
		this.function = function;
		this.arguments = arguments;
		this.isStringFunction = isStringFunction;
	}



	public List<IOperand> getArguments() {
		return arguments;
	}

	public void setArguments(List<IOperand> arguments) {
		this.arguments = arguments;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function)  {
		this.function = function;
	}

	private static final char TOKEN_PAR_1 = '(';
	private static final char TOKEN_PAR_2 = ')';
	private static final char TOKEN_COMMA = ',';

	@Override
	public String toSPARQL() {
		StringBuilder sparqlRepresentation = new StringBuilder();
		sparqlRepresentation.append(function);
		if(!isStringFunction) {
			sparqlRepresentation.append(TOKEN_PAR_1);
		for(int index=0; index < arguments.size();index++) {
				 sparqlRepresentation.append(arguments.get(index).toSPARQL());
				if(index+1<arguments.size())
					sparqlRepresentation.append(TOKEN_COMMA);
			}
		sparqlRepresentation.append(TOKEN_PAR_2);
		}
		return sparqlRepresentation.toString();
	}


}
