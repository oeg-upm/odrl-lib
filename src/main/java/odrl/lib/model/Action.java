package odrl.lib.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.compress.utils.Lists;

import com.github.jsonldjava.shaded.com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import odrl.lib.model.exceptions.EvaluationException;

public class Action {

	private String action;
	private List<Constraint> constraints = Lists.newArrayList();

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
	public List<Constraint> getConstraints() {
		return constraints;
	}
	public void setConstraints(List<Constraint> constraints) {
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


	public String solve(Map<String, String> prefixes) throws EvaluationException {
		boolean fulfilled = false;
		for(int index=0; index < this.constraints.size(); index++) {
			fulfilled = this.constraints.get(index).solve(prefixes);
			if(!fulfilled) break;
		}
		if(fulfilled)
			return action;
		return null;
	}
}
