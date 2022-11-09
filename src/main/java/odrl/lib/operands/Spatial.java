package odrl.lib.operands;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase0;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Spatial extends FunctionBase0 implements Operand {
	private static File dir = new File("./operands");
	private static File file = new File("./operands/spatial.json");
	private String spatial = null;
	private final static Gson GSON = new Gson();

	public Spatial() {
		try {

			FileWriter fw;
			if (!dir.exists()) {
				dir.mkdirs();
				if (!file.exists())
					file.createNewFile();
				fw = new FileWriter(file);
				fw.write("{ \"spatial\" : \"https://www.wikidata.org/wiki/Q183\"}");
				fw.flush();
				fw.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public String getName() {
		return "spatial";
	}

	@Override
	public NodeValue exec() {
		String value = "";
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(file.getPath()));
			String jValue = new String(encoded, Charset.defaultCharset());
			JsonObject g = GSON.fromJson(jValue, JsonObject.class);
			value = g.get("spatial").getAsString();
			System.out.println(">" + value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return NodeValue.makeString(value);
	}

}
