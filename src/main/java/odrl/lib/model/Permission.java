package odrl.lib.model;


import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.Lists;

import com.github.jsonldjava.shaded.com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import odrl.lib.model.exceptions.EvaluationException;

public class Permission {

	private String target;
	private List<Action> actions = Lists.newArrayList();

	public Permission(String target) {
		this.target=target;
	}


	public void addAction(Action action) {
		Optional<Action> oldActionOpt = actions.parallelStream().filter(act -> act.equals(action)).findFirst();
		if(oldActionOpt.isPresent()) {
			Action oldAction = oldActionOpt.get();
			actions.remove(oldAction);
			oldAction.getConstraints().addAll(action.getConstraints());
			actions.add(oldAction);
		}else {
			actions.add(action);
		}
	}

	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}

	public List<Action> getActions() {
		return actions;
	}

	@Override
	public int hashCode() {
		return Objects.hash(target);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		Permission other = (Permission) obj;
		return Objects.equals(target, other.target);
	}


	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("target", this.target);
		JsonArray array = new JsonArray();
		this.actions.stream().forEach(action -> array.add(action.toJson()));
		json.add("actions", array);
		return json;

	}


	public List<String> solve(Map<String, String> prefixes) throws EvaluationException {
		List<String> actionsSolved = Lists.newArrayList();
		for(int index=0; index < this.actions.size(); index++) {
			String result = this.actions.get(0).solve(prefixes);
			if(result!=null) actionsSolved.add(result);
		}
		return actionsSolved;
	}



}
