package odrl.lib;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.utils.Lists;
import org.apache.jena.atlas.logging.Log;
import org.apache.jena.ext.com.google.common.collect.Maps;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import odrl.lib.exceptions.OdrlRegistrationException;
import odrl.lib.exceptions.PolicyException;
import odrl.lib.exceptions.UnsupportedOperandException;
import odrl.lib.exceptions.UnsupportedOperatorException;
import odrl.lib.operands.DateTime;
import odrl.lib.operands.Operand;
import odrl.lib.operands.Spatial;

public class OdrlLib {

	private static final Logger LOG = LoggerFactory.getLogger(OdrlLib.class);
	private static final String PERMISSIONS = "PREFIX odrl: <http://www.w3.org/ns/odrl/2/>\n"
			+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
			+ "SELECT ?action ?target ?left ?right ?op WHERE { \n"
			+ " ?policy odrl:permission ?permission . \n"
			+ " ?permission odrl:action ?action .  \n"
			+ " ?permission odrl:target ?target .\n"
			+ " ?permission odrl:constraint ?constraint .\n"
			+ " ?constraint odrl:leftOperand ?left .\n"
			+ " ?constraint odrl:rightOperand ?right .\n"
			+ " ?constraint odrl:operator ?op .\n"
			+ "} \n"
			+ "\n"
			+ "";


	private Map<String, String> prefixes = Maps.newHashMap();
	private Map<String, String> functions = Maps.newHashMap();
	public OdrlLib() {
		registerPrefix("odrl", "http://www.w3.org/ns/odrl/2/");
		try {
			register("odrl", new DateTime());
			register("odrl", new Spatial());
		}catch(Exception e) {
			//skip
		}
	}

	public void registerPrefix(String prefix, String uri) {
		this.prefixes.put(prefix, uri);
	}

	public Map<String, String> getPrefixes() {
		return prefixes;
	}

	public Map<String, String> getFunctions() {
		return functions;
	}

	public void register(String prefix, Operand operand) throws OdrlRegistrationException {
		if(!prefixes.containsKey(prefix))
			throw new OdrlRegistrationException("Provided prefix ("+prefix+") does not exist, register first a URI with that prefix using addPrefix method.");
		String uri = prefixes.get(prefix);
		FunctionRegistry.get().put(uri + operand.getName(), operand.getClass());
		functions.put(uri + operand.getName(), prefix+":"+operand.getName());
	}


	public Map<String,List<String>> solve(JsonObject policyJson) throws UnsupportedOperandException, UnsupportedOperatorException, PolicyException {
		Map<String,List<String>> allowedTo = Maps.newHashMap();
		List<Permission> permissions = mapToPermissions(policyJson);
		for (Permission permission : permissions) {
			List<String> actions = permission.solve();
			if(!actions.isEmpty())
				allowedTo.put(permission.getTarget(),actions);
		}
		return allowedTo;
	}

	private List<Permission>  mapToPermissions(JsonObject policy) {
		List<Permission> permissions = Lists.newArrayList();
		ResultSet rs = retrievePermissions(policy);
		while(rs.hasNext()) {
			QuerySolution qs = rs.next();
			Permission permission = mapToPermission(qs);
			if(permissions.contains(permission)) {
				int oldIndex = permissions.indexOf(permission);
				Permission permissionOld = permissions.remove(oldIndex);
				permissionOld.getActions().forEach(actionOld -> permission.addAction(actionOld));
			}

			permissions.add(permission);
		}

		return permissions;
	}

	private ResultSet retrievePermissions(JsonObject policy) {
		Model model = ModelFactory.createDefaultModel();
		model.read(new ByteArrayInputStream(policy.toString().getBytes()), null, RDFFormat.JSONLD11.toString());
		return QueryExecutionFactory.create(QueryFactory.create(PERMISSIONS), model).execSelect();
	}

	private Permission mapToPermission(QuerySolution qs) {
		String target = qs.get("?target").toString();
		String actionStr = qs.get("?action").toString();
		RDFNode leftNode = qs.get("?left");
		RDFNode rightNode = qs.get("?right");
		RDFNode opNode = qs.get("?op");
		Permission permission = new Permission(target);
		Action action = new Action(actionStr);
		Constraint constraint = new Constraint(leftNode, opNode, rightNode, functions, prefixes);
		action.addConstraint(constraint);
		permission.addAction(action);
		return permission;
	}









}
