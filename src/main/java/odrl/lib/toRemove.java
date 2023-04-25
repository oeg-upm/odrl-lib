package odrl.lib;

import org.apache.jena.sparql.resultset.ResultsFormat;

import sparql.streamline.core.Sparql;
import sparql.streamline.exception.SparqlQuerySyntaxException;
import sparql.streamline.exception.SparqlRemoteEndpointException;

public class toRemove {

	private static String query = "SELECT ?s  WHERE {SERVICE <http://localhost:9000/mapas>{ ?s ?p ?o }}";

	public static void main(String[] args) throws SparqlQuerySyntaxException, SparqlRemoteEndpointException {
		String result = new String (Sparql.queryService(query, ResultsFormat.FMT_RS_JSON, null).toByteArray());
		System.out.println(">"+result);
	}
}
