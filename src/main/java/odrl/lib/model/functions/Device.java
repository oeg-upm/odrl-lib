package odrl.lib.model.functions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase0;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Device extends FunctionBase0 implements IFunction {
	private static File dir = new File("./operands");
	private static File file = new File("./operands/device.json");
	private final static Gson GSON = new Gson();

	public Device() {
		try {

			FileWriter fw;
			if (!dir.exists()) {
				dir.mkdirs();
				if (!file.exists())
					file.createNewFile();
				fw = new FileWriter(file);
				fw.write("{ \"device\" : \""+UUID.randomUUID().toString()+"\"}");
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
		return "device";
	}

	@Override
	public NodeValue exec() {
		String value = "";
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(file.getPath()));
			String jValue = new String(encoded, Charset.defaultCharset());
			JsonObject g = GSON.fromJson(jValue, JsonObject.class);
			value = g.get("device").getAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return NodeValue.makeString(value);
	}

}
