package odrl.lib;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * This class provides methods to parse an ODRL policy in different RDF formats.
 * @author <a href="mailto:andreajesus.cimmino@upm.es">Andrea Cimmino</a>
 *
 */
public class Policies {

	private final static Gson GSON = new Gson();

	/**
	 * This method parses an ODRL policy
	 * @param policy a {@link String} representation of the policy
	 * @return a {@link JsonObject} representation of the policy
	 */
	public static JsonObject fromJsonld11String(String policy) {
		return 	GSON.fromJson(policy, JsonObject.class);
	}

	/**
	 * This method parses an ODRL policy
	 * @param policy a {@link String} representation of the policy
	 * @return a {@link JsonObject} representation of the policy
	 */
	public static JsonObject fromTurtle(String policy) {
		//TODO
		throw new IllegalArgumentException("Not implemneted yet");
	}





}
