package odrl.lib.model;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFFormat;

@Deprecated
public class Policy {

	private Model policy = ModelFactory.createDefaultModel();
	public Policy() {
		super();
	}
	
	public Model getPolicy() {
		return policy;
	}

	public void setPolicy(Model policy) {
		this.policy = policy;
	}

	public String toTurtle() {
		try(Writer writer = new StringWriter()){
		policy.write(writer, "TURTLE", null);
		return writer.toString();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Policy fromRDF(String rdf, RDFFormat format) {
		Policy newPolicy = new Policy();
		newPolicy.getPolicy().read(new ByteArrayInputStream(rdf.getBytes()), null, format.toString());
		return newPolicy;
	}
	
	public static Policy fromJSONLD11(String json) {
		return  fromRDF(json, RDFFormat.JSONLD11);
	}
	
	public static Policy fromTurtle(String json) {
		return  fromRDF(json, RDFFormat.TURTLE);
	}
	
	
	
}
