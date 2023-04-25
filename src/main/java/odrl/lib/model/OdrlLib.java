package odrl.lib.model;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.utils.Lists;
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

import com.google.gson.JsonObject;

import odrl.lib.model.exceptions.EvaluationException;
import odrl.lib.model.exceptions.OdrlRegistrationException;
import odrl.lib.model.exceptions.OperandException;
import odrl.lib.model.exceptions.OperatorException;
import odrl.lib.model.exceptions.UnsupportedFunctionException;
import odrl.lib.model.functions.DateTime;
import odrl.lib.model.functions.IFunction;
import odrl.lib.model.functions.Spatial;
import odrl.lib.model.functions.nativeoperators.OdrlEq;
import odrl.lib.model.functions.nativeoperators.OdrlGt;
import odrl.lib.model.functions.nativeoperators.OdrlGteq;
import odrl.lib.model.functions.nativeoperators.OdrlLt;
import odrl.lib.model.functions.nativeoperators.OdrlLteq;
import odrl.lib.model.functions.nativeoperators.OdrlNeq;
import odrl.lib.model.nodes.IOperand;
import odrl.lib.model.nodes.OperandFactory;
import odrl.lib.model.nodes.OperandFunction;

public class OdrlLib {

	// private static final Logger LOG = LoggerFactory.getLogger(OdrlLib.class);
	private static final String PERMISSIONS = "PREFIX odrl: <http://www.w3.org/ns/odrl/2/>\n"
			+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" + "SELECT ?action ?target ?left ?right ?op WHERE { \n"
			+ " ?policy odrl:permission ?permission . \n" 
			+ " ?permission odrl:action ?action .  \n"
			+ " ?permission odrl:target ?target .\n" 
			+ " ?permission odrl:constraint ?constraint .\n"
			+ " ?constraint odrl:leftOperand ?left .\n" 
			+ " ?constraint odrl:rightOperand ?right .\n"
			+ " ?constraint odrl:operator ?op .\n" 
			+ "} \n";

	protected static boolean debug = false;

	private Map<String, String> prefixes = Maps.newHashMap();
	private List<String> functions = Lists.newArrayList();

	public OdrlLib() {
		registerNative();
	}

	public void registerPrefix(String prefix, String uri) {
		this.prefixes.put(prefix, uri);
	}

	public Map<String, String> getPrefixes() {
		return prefixes;
	}

	public List<String> getFunctions() {
		return functions;
	}

	public void register(String prefix, IFunction function) throws OdrlRegistrationException {
		if (!prefixes.containsKey(prefix))
			throw new OdrlRegistrationException("Provided prefix (" + prefix
					+ ") does not exist, register first a URI with that prefix using addPrefix method.");
		String uri = prefixes.get(prefix);
		FunctionRegistry.get().put(uri + function.getName(), function.getClass());
		functions.add(prefix + ":" + function.getName());
	}

	public void register(String sparqlFunction) throws OdrlRegistrationException {
		if(!sparqlFunction.contains(":"))
			throw new OdrlRegistrationException("Provided function must be prefixed, provide a valid function name that follows the convention [prefix]:[name]");
		String[] splitted = sparqlFunction.split(":");
		if (!prefixes.containsKey(splitted[0]))
			throw new OdrlRegistrationException("Provided prefix (" + splitted[0]+ ") does not exist, register first a URI with that prefix using addPrefix method.");

		functions.add(sparqlFunction);
	}

	public Map<String, List<String>> solve(JsonObject policyJson)
			throws UnsupportedFunctionException, OperandException, OperatorException, EvaluationException {
		Map<String, List<String>> allowedTo = Maps.newHashMap();
		List<Permission> permissions = mapToPermissions(policyJson);
		for (Permission permission : permissions) {
			List<String> actions = permission.solve(this.prefixes);
			if (!actions.isEmpty())
				allowedTo.put(permission.getTarget(), actions);
		}
		return allowedTo;
	}


	public List<Permission> mapToPermissions(JsonObject policy) throws UnsupportedFunctionException, OperandException, OperatorException, EvaluationException {
		List<Permission> permissions = Lists.newArrayList();
		Model model = toRDFModel( policy);
		model.write(System.out,"turtle");
		ResultSet rs = QueryExecutionFactory.create(QueryFactory.create(PERMISSIONS), model).execSelect();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			Permission permission = mapToPermission(model, qs);
			if (permissions.contains(permission)) {
				int oldIndex = permissions.indexOf(permission);
				Permission permissionOld = permissions.remove(oldIndex);
				permissionOld.getActions().forEach(actionOld -> permission.addAction(actionOld));
			}

			permissions.add(permission);
		}
		if(permissions.isEmpty())
			throw new EvaluationException("Provided policy seems to be malformed since it lacks of correly expressed permissions");
		return permissions;
	}


	private Model toRDFModel(JsonObject policy) {
		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefixes(prefixes);
		model.read(new ByteArrayInputStream(policy.toString().getBytes()), null, RDFFormat.JSONLD11.toString());
		return model;
	}

	private Permission mapToPermission(Model model, QuerySolution qs) throws OperandException, UnsupportedFunctionException, OperatorException {
		String target = qs.get("?target").toString();
		String actionStr = qs.get("?action").toString();
		RDFNode leftNode = qs.get("?left");
		RDFNode rightNode = qs.get("?right");
		RDFNode opNode = qs.get("?op");

		Permission permission = new Permission(target);
		Action action = new Action(actionStr);
		IOperand left = OperandFactory.createOperand(model, leftNode, functions);
		IOperand right = OperandFactory.createOperand(model, rightNode, functions);
		OperandFunction operator = OperandFactory.createOperandFunction(model, opNode, functions);
		operator.getArguments().add(left);
		operator.getArguments().add(right);
		Constraint constraint = new Constraint(operator);	
		action.addConstraint(constraint);
		permission.addAction(action);
		return permission;
	}
	
	

	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(boolean debug) {
		OdrlLib.debug = debug;
	}
	
	public void registerNative() {
		registerPrefix("odrl", "http://www.w3.org/ns/odrl/2/");
		try {
			// Operators
			register("odrl", new OdrlEq());
			register("odrl", new OdrlNeq());
			register("odrl", new OdrlGt());
			register("odrl", new OdrlGteq());
			register("odrl", new OdrlLt());
			register("odrl", new OdrlLteq());
			// Operands
			register("odrl", new DateTime());
			register("odrl", new Spatial());
		} catch (Exception e) {
			// skip
		}
	}

	public void registerGeof() {
		registerPrefix("geof", OdrlLib.GEOF);
		try {
			register("geof:sfContains");
			register("geof:boundary");
			register("geof:buffer");
			register("geof:ehContains");
			register("geof:sfContains");
			register("geof:convexHull");
			register("geof:ehCoveredBy");
			register("geof:ehCovers");
			register("geof:sfCrosses");
			register("geof:difference");
			register("geof:rcc8dc");
			register("geof:ehDisjoint");
			register("geof:sfDisjoint");
			register("geof:distance");
			register("geof:envelope");
			register("geof:ehEquals");
			register("geof:rcc8eq");
			register("geof:sfEquals");
			register("geof:rcc8ec");
			register("geof:getSRID");
			register("geof:ehInside");
			register("geof:intersection");
			register("geof:sfIntersects");
			register("geof:ehMeet");
			register("geof:rcc8ntpp");
			register("geof:rcc8ntppi");
			register("geof:ehOverlap");
			register("geof:sfOverlaps");
			register("geof:rcc8po");
			register("geof:relate");
			register("geof:symDifference");
			register("geof:rcc8tpp");
			register("geof:rcc8tppi");
			register("geof:sfTouches");
			register("geof:union");
			register("geof:sfWithin");
			register("geof:asGeoJSON");
		} catch (Exception e) {

		}
	}

	protected static final String GEOF = "http://www.opengis.net/def/function/geosparql/";
	protected static final String SPATIALF = "http://jena.apache.org/function/spatial#";
	protected static final String SPATIAL = "http://jena.apache.org/spatial#";
	protected static final String UNITS = "http://www.opengis.net/def/uom/OGC/1.0/";

	public void registerSpatial() {
		registerPrefix("spatialF", SPATIALF);
		registerPrefix("spatial", SPATIAL);
		registerPrefix("units", UNITS);
		registerPrefix("geosp", "http://www.opengis.net/ont/geosparql#");
		try {
			register("spatialF:convertLatLon");
			register("spatialF:convertLatLonBox");
			register("spatialF:equals");
			register("spatialF:nearby");
			register("spatialF:withinCircle");
			register("spatialF:angle");
			register("spatialF:angleDeg");
			register("spatialF:distance");
			register("spatialF:azimuth");
			register("spatialF:azimuthDeg");
			register("spatialF:greatCircle");
			register("spatialF:greatCircleGeom");
			register("spatialF:transform");
			register("spatialF:transformDatatype");
			register("spatialF:transformSRS");

			register("spatial:intersectBox");
			register("spatial:intersectBoxGeom");
			register("spatial:withinBox");
			register("spatial:withinBoxGeom");
			register("spatial:nearby");
			register("spatial:nearbyGeom");
			register("spatial:withinCircle");
			register("spatial:withinCircleGeom");
			register("spatial:north");
			register("spatial:northGeom");
			register("spatial:south");
			register("spatial:southGeom");
			register("spatial:east");
			register("spatial:eastGeom");
			register("spatial:west");
			register("spatial:westGeom");

		} catch (Exception e) {

		}
	}

}
