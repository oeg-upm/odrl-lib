package odrl.lib;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Set;

import org.apache.jena.ext.com.google.common.collect.Maps;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.apache.jena.sparql.resultset.ResultsFormat;

import com.github.jsonldjava.shaded.com.google.common.collect.Sets;

import odrl.lib.model.Constraint;
import odrl.lib.model.Permission;
import odrl.lib.model.Policy;
import odrl.lib.operands.DateTime;
import odrl.lib.operands.Operand;
import sparql.streamline.core.Sparql;
import sparql.streamline.exception.SparqlQuerySyntaxException;
import sparql.streamline.exception.SparqlRemoteEndpointException;

@Deprecated
public class PolicySolver {

	private static String NS_OPERANDS = "http://www.w3.org/ns/odrl/2/";
	private Map<String,String> prefixes = Maps.newHashMap();

	public PolicySolver() {
		prefixes.put("odrl", NS_OPERANDS);
		DateTime dt = new DateTime();
		FunctionRegistry.get().put(NS_OPERANDS+dt.getName(), dt.getClass()) ;

	}
	
	public void registerOperand(Operand operand) {
		FunctionRegistry.get().put(NS_OPERANDS+operand.getName(), operand.getClass()) ;
	}

	public static void solvePolicy(Policy policy) {
		String rawPermissions = retrievePermissions(policy.getPolicy());
		System.out.println(rawPermissions);
		Set<Permission> permissions = buildPermissions(rawPermissions);
		permissions.parallelStream().forEach(permission -> System.out.println(permission.toSPARQL()));
		permissions.stream().forEach(elem -> {
			try {
				System.out.println(solvePermission(elem.toSPARQL()));
			} catch (SparqlQuerySyntaxException e) {
				e.printStackTrace();
			} catch (SparqlRemoteEndpointException e) {
				e.printStackTrace();
			}
		});
	}

	// run permission sparql
	
	public static String solvePermission(String queryStr) throws SparqlQuerySyntaxException, SparqlRemoteEndpointException {
		return new String(Sparql.queryService(queryStr, ResultsFormat.FMT_RS_CSV, null).toByteArray());
	}
	
	// string to ORM

	private static Set<Permission> buildPermissions(String rawPermissions) {
		Set<Permission> permissions = Sets.newHashSet();
		String[] rawLines = rawPermissions.split("\n");
		String id = null;
		Permission permission = new Permission();
		for (int index = 0; index < rawLines.length; index++) {
			String[] rawFields = rawLines[index].split(",");
			if (id == null || (id != null && !clean(rawFields[0]).equals(id))) {
				permission = new Permission();
				permission.setPolicy(clean(rawFields[0]));
				if (rawFields.length == 6)
					permission.setTarget(clean(rawFields[5]));
				id = clean(rawFields[0]);
			}
			Constraint constraint = new Constraint();
			constraint.setConstraint(clean(rawFields[1]));
			constraint.setLeft(clean(rawFields[2]));
			constraint.setOperation(clean(rawFields[3]));
			constraint.setRight(clean(rawFields[4]));
			permission.getConstraints().add(constraint);
			permissions.add(permission);
		}

		return permissions;
	}

	private static String clean(String dirty) {
		dirty = dirty.trim();
		if (dirty.endsWith(","))
			dirty = dirty.substring(0, dirty.length() - 2);
		return dirty;
	}

	// Transform RDF to ORM
	private static final Query QUERY_4_CONSTRAINTS = QueryFactory.create("PREFIX odrl: <http://www.w3.org/ns/odrl/2/>\n"
			+ "\n" + "SELECT ?permission ?constraint ?left ?op ?right ?target ?lType { \n"
			+ "    ?policy odrl:permission ?permission . \n" 
			+ "    ?permission odrl:constraint ?constraint .\n"
			+ "    ?constraint odrl:leftOperand ?left .\n"
			+ "		BIND( datatype(?left) AS ?lType) ." 
			+ "    ?constraint odrl:operator ?op .\n"
			+ "    ?constraint odrl:rightOperand ?right .\n" 
			+ "    OPTIONAL {?permission odrl:target ?target } .\n"
			+ "} ");

	private static String retrievePermissions(Model policy) {
		try (QueryExecution qe = QueryExecutionFactory.create(QUERY_4_CONSTRAINTS, policy)) {
			try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
				ResultSet results = qe.execSelect();
				ResultSetFormatter.outputAsCSV(stream, results);
				qe.close();
				String resultsCsv = new String(stream.toByteArray());
				System.out.println(resultsCsv);
				System.out.println("----");
				return resultsCsv.substring(44);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
