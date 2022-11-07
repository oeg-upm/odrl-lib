package odrl.lib;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Policies {

	private final static Gson GSON = new Gson();
	
	public static JsonObject fromJsonld11String(String policy) {
		return 	GSON.fromJson(policy, JsonObject.class);

	}
}
