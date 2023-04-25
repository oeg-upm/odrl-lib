package odrl.lib;

import org.apache.jena.sparql.resultset.ResultsFormat;

import odrl.lib.model.Sparql;
import odrl.lib.model.exceptions.SparqlException;

public class toRemove {

	private static String query = "SELECT ?s  WHERE {SERVICE <http://localhost:9000/mapas>{ ?s ?p ?o }}";

	public static void main(String[] args) throws SparqlException {
		String result = new String (Sparql.queryService(query, ResultsFormat.FMT_RS_JSON, null).toByteArray());
		System.out.println(">"+result);
	}
}
