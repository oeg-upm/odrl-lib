package odrl.lib.model;

import java.util.Objects;

@Deprecated
public class Constraint {

	String constraint;
	String operation;
	String left;
	String right;
	
	public Constraint() {
	
	}

	@Override
	public String toString() {
		return "Constraint [constraint=" + constraint + ", operation=" + operation + ", left=" + left + ", right="
				+ right + "]";
	}

	public String getConstraint() {
		return constraint;
	}

	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getLeft() {
		return left;
	}

	public void setLeft(String left) {
		this.left = left;
	}

	public String getRight() {
		return right;
	}

	public void setRight(String right) {
		this.right = right;
	}


	@Override
	public int hashCode() {
		return Objects.hash(left, operation, right);
	}

	public String getSPARQLVar() {
		return "?"+String.valueOf(constraint.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Constraint other = (Constraint) obj;
		return Objects.equals(left, other.left) && Objects.equals(operation, other.operation)
				&& Objects.equals(right, other.right);
	}
	
	public String toSPARQL() {
		String operator = solveOperator(this.operation);
		String leftOp = solveOperand(this.left);
		String rightOp = solveOperand(this.right);
		StringBuilder SPARQL = new StringBuilder();

		if(operator!=null) {
			SPARQL.append(" BIND ( "+leftOp+"() "+operator+" \"").append(rightOp).append("\" AS ").append(getSPARQLVar()).append(" ) \n");
		}else {
			if(operation.startsWith("http://www.w3.org/ns/odrl/2/")) {
				SPARQL.append(" BIND ( op:").append(operator).append("(").append(leftOp).append(",").append(rightOp).append(") AS ?").append(getSPARQLVar()).append(" ) \n");
			}else {
				throw new IllegalArgumentException("Constraint [toSPARQL] provided operator ("+operation+") is not supported");
			}
		}
		return SPARQL.toString();
	}
	
	

	private String solveOperand(String operand) {
		if(operand.startsWith("http://www.w3.org/ns/odrl/2/"))
			return operand.replace("http://www.w3.org/ns/odrl/2/", "odrl:");
		return operand;
	}

	/**
	 * This method replaces any ODRL operator (eq, gt, gteq, hasPart, isA, isAllOf, isAnyOf, isNoneOf, isPartOf, lt, lteq, neq) for a SPARQL native operator
	 * @param operator
	 * @param left
	 * @param right
	 * @return
	 */
	private String solveOperator(String operator) {
		if(operator.equals("http://www.w3.org/ns/odrl/2/eq")) return " = ";
		if(operator.equals("http://www.w3.org/ns/odrl/2/gt")) return " > ";
		if(operator.equals("http://www.w3.org/ns/odrl/2/gteq")) return " >= ";
		if(operator.equals("http://www.w3.org/ns/odrl/2/lt")) return " < ";
		if(operator.equals("http://www.w3.org/ns/odrl/2/lteq")) return " <= ";
		if(operator.equals("http://www.w3.org/ns/odrl/2/neq")) return " != ";
		// hasPart, isA, isAllOf, isAnyOf, isNoneOf, isPartOf, 
		return null;
	}
	
}
