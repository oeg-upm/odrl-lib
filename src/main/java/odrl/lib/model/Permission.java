package odrl.lib.model;

import java.util.List;
import java.util.Objects;

import org.apache.commons.compress.utils.Lists;

@Deprecated
public class Permission {

	private String policy;
	private String target;
	private List<Constraint> constraints = Lists.newArrayList();
	
	public String getPolicy() {
		return policy;
	}
	public void setPolicy(String policy) {
		this.policy = policy;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public List<Constraint> getConstraints() {
		return constraints;
	}
	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
	}
	
	@Override
	public String toString() {
		return "Permission [policy=" + policy + ", target=" + target + ", constraints=" + constraints + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(policy);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Permission other = (Permission) obj;
		return Objects.equals(policy, other.policy);
	}
	
	public String toSPARQL() {
		StringBuilder SPARQL = new StringBuilder();
		SPARQL.append("PREFIX odrl: <http://www.w3.org/ns/odrl/2/>\n");

		SPARQL.append("SELECT ");
		this.constraints.stream().forEach(elem -> SPARQL.append(elem.getSPARQLVar()).append(" "));
		SPARQL.append(" WHERE { \n");
		this.constraints.stream().map(elem -> elem.toSPARQL()).forEach(elem -> SPARQL.append(elem));
		SPARQL.append("}\n");
		
		return SPARQL.toString();
	}
	
}
