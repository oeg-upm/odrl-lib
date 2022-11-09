package odrl.lib;


import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.jsonldjava.shaded.com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

class Permission {

	private String target;
	private Set<Action> actions = Sets.newHashSet();

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

	public Set<Action> getActions() {
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


	public List<String> solve() {
		return this.actions.parallelStream().map(action -> action.solve()).filter(action -> action!=null).collect(Collectors.toList());
	}



}
