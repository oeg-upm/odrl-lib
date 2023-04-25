package odrl.lib.model.nodes;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.Lists;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.resultset.ResultsFormat;

import odrl.lib.model.Sparql;
import odrl.lib.model.exceptions.OperandException;
import odrl.lib.model.exceptions.UnsupportedFunctionException;

public class OperandFactory {

	private OperandFactory() {
		super();
	}

	/**
	 * Creates an operand associated to the provided {@link RDFNode} and checks if the operand is a function that exists in the system.
	 * @param model
	 * @param operand
	 * @param functions
	 * @return
	 * @throws UnsupportedFunctionException is thrown when some function is declared in the mapping but it is not supported in the implementation
	 * @throws OperandException is thrown when an error happens parsing the operands
	 */
	public static IOperand createOperand(Model model, RDFNode operand, List<String> functions) throws UnsupportedFunctionException, OperandException {
		IOperand operandNode = null;
		boolean isStringFunction = !operand.isResource() && isStringFunction(functions, operand.asLiteral().toString());
		if(operand.isLiteral() && !isStringFunction) { // static value (with or without datatype/lang)
			operandNode = createOperandValue(operand.asLiteral());
		}else if(operand.isResource() || isStringFunction){ // function
			operandNode = createOperandFunction(model, operand, functions, isStringFunction);
		}else {
			throw new OperandException("Provided operand ("+operand.toString()+" is unknown, check that the operand is a constant value or a valid URI that corresponds to a function");
		}

		return operandNode;
	}

	private static boolean  isStringFunction(List<String> functions, String function) {
		return functions.parallelStream().anyMatch(function::startsWith);
	}

	// -- OperandFunction methods
	public static OperandFunction createOperandFunction(Model model, RDFNode operand, List<String> functions) throws UnsupportedFunctionException, OperandException {
		return createOperandFunction( model,  operand, functions, false);
	}

	public static OperandFunction createOperandFunction(Model model, RDFNode operand, List<String> functions, boolean isStringFunction) throws UnsupportedFunctionException, OperandException {
		String function = null;
		List<IOperand> arguments = Lists.newArrayList();
		if(!isStringFunction) {
			function = shortenURI( model,  operand.asResource().getURI());
			checkFunctionExists(functions,  function);
			List<String> argumentsRaw = retrieveRawArguments( model,  operand);
			arguments = argumentsRaw.parallelStream().map(elem -> elem.split(",")).map( elem -> buildOperandAgument(model, elem[0].trim(), elem[1].trim(), isStringFunction)).collect(Collectors.toList());

		}else {
			function = operand.asLiteral().toString();
		}


		return  new OperandFunction(function, arguments, isStringFunction);

	}


	private static final String QUERY = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n SELECT ?item ?type WHERE { <###OPERAND###> <http://www.w3.org/ns/odrl-extension#hasArguments> ?list .  ?list rdf:rest*/rdf:first ?item .  BIND (datatype(?item) AS ?type) . }";
	private static final String REPLACE_TOKEN = "###OPERAND###";

	private static List<String> retrieveRawArguments(Model model, RDFNode operand) throws OperandException{
		List<String> argumentsRaw = Lists.newArrayList();
		try {
			String q = QUERY.replace(REPLACE_TOKEN, operand.asResource().getURI());
			String results = new String(Sparql.queryModel(q, model, ResultsFormat.FMT_RS_CSV, null).toByteArray());
			argumentsRaw.addAll(Arrays.asList(results.split("\n")));
			argumentsRaw.remove(0);
		}catch(Exception e) {
			throw new OperandException(e.getMessage());
		}
		return argumentsRaw;
	}

	private static IOperand buildOperandAgument(Model model, String value, String type, boolean isStringFunction) {
		OperandValue operand= new OperandValue(value);
		if(type!=null && !type.isEmpty() && !isStringFunction) {
			operand.setType(type);
		}else {
			operand.setUri(true);
			operand.setValue(formatConstantURI(model, value));
		}
		return operand;
	}

	private static String formatConstantURI(Model model, String uri) {
		StringBuilder result = new StringBuilder();
		try {
			result.append(shortenURI(model, uri));
		} catch (UnsupportedFunctionException e) {
			result.append("<").append(uri).append(">");
		}
		return result.toString();
	}

	// -- Operand Value methods


	private static IOperand createOperandValue(Literal literal) {
		OperandValue operandValue = new OperandValue(literal.getString());
		String lang = literal.getLanguage();
		String type = literal.getDatatypeURI();
		if(lang!=null && !lang.isEmpty()) {
			operandValue.setLang(lang);
		}else if(type!=null && !type.isEmpty()) {
			operandValue.setType(type);
		}
		return operandValue;
	}

	// -- Ancillary methods

	private static void checkFunctionExists(List<String> functions, String function) throws UnsupportedFunctionException {
		if(!functions.contains(function))
			throw new UnsupportedFunctionException("Provided function is not supported (maybe it was not registered), available ones are: "+functions);
	}



	private static String shortenURI(Model model, String node) throws UnsupportedFunctionException {
		String shortenURI = null;
		Optional<Entry<String, String>> prefix = model.getNsPrefixMap().entrySet().parallelStream().filter(entry -> node.startsWith(entry.getValue())).findFirst();
		if(prefix.isPresent()) {
			Entry<String,String> prefixUris = prefix.get();
			shortenURI = node.replace(prefixUris.getValue(), prefixUris.getKey()+":");
		}else {
				throw new UnsupportedFunctionException("Provided uri ("+node+" has no prefix associated, please provide one.");
			}
		return shortenURI;

	}


}
