package odrl.lib;

import org.apache.jena.sparql.function.Function;

/**
 * This interface must be implemented by classes providing the functionalities for Operands or Operators
 * @author Andrea Cimmino
 *
 */
public interface RegistrableElement extends Function {

	String getName();


}
