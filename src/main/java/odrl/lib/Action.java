package odrl.lib;

import java.util.Objects;
import java.util.Set;

import com.github.jsonldjava.shaded.com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

class Action {

	private String action;
	private Set<Constraint> constraints = Sets.newHashSet();

	public Action(String action) {
		this.action = action;
	}

	public void addConstraint(Constraint constraint) {
		constraints.add(constraint);
	}

	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Set<Constraint> getConstraints() {
		return constraints;
	}
	public void setConstraints(Set<Constraint> constraints) {
		this.constraints = constraints;
	}

	@Override
	public int hashCode() {
		return Objects.hash(action);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		Action other = (Action) obj;
		return Objects.equals(action, other.action);
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("action", this.action);
		JsonArray array = new JsonArray();
		this.constraints.stream().forEach(constraintElem -> array.add(constraintElem.toJson()));
		json.add("constraints", array);
		return json;

	}

	public String solve() {
		boolean fulfilled = this.constraints.parallelStream().map(constraint -> constraint.solve()).anyMatch(solution -> !solution);
		if(!fulfilled)
			return action;
		return null;
	}
}
