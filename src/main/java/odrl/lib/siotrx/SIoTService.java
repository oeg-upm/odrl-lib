package odrl.lib.siotrx;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import helio.blueprints.TranslationUnit;
import helio.blueprints.UnitBuilder;
import helio.blueprints.components.ComponentType;
import helio.blueprints.components.Components;
import helio.blueprints.exceptions.ExtensionNotFoundException;
import helio.blueprints.exceptions.IncompatibleMappingException;
import helio.blueprints.exceptions.IncorrectMappingException;
import helio.blueprints.exceptions.TranslationUnitExecutionException;

public class SIoTService {
	private static ExecutorService service = Executors.newCachedThreadPool();
	private static final String MAPPING_PROCESSOR = "SIoTRxBuilder";
	private final static Gson GSON = new Gson();
	private final static String defaultComponentsFile = "./components.json";
	private  static UnitBuilder builder;
	static {
		System.out.println("loading components..");
		loadDefaultComponents();
		System.out.println("loading builder..");
		try {
			builder = Components.newBuilderInstance(MAPPING_PROCESSOR);
		} catch (ExtensionNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static final String solveSIoTPolicy(String policy, Map<String, Object> params)
			throws IncompatibleMappingException, TranslationUnitExecutionException, IncorrectMappingException,
			ExtensionNotFoundException {
		System.out.println("compiling mappings..");
		return builder.parseMapping(policy).parallelStream()
				.map(uniT -> runUnit(uniT, params)).collect(Collectors.joining());

	}

	public static void close() {
		service.shutdown();
	}

	public static String runUnit(TranslationUnit unit, Map<String, Object> args)  {
		String result = "";
		try {
			Future<String> f = service.submit(unit.getTask(args));
			result = f.get();
			f.cancel(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	protected static void loadDefaultComponents() {
		JsonArray list = readDefaultComponents();
		list.forEach(component -> {
				try {
					JsonObject cmp = component.getAsJsonObject();
					Components.registerAndLoad(cmp.get("source").getAsString(), cmp.get("clazz").getAsString(), ComponentType.valueOf(cmp.get("type").getAsString()));
				} catch (ExtensionNotFoundException e) {
					e.printStackTrace();
					System.exit(-1);
				}
			});

	}

	private static JsonArray readDefaultComponents() {
		try {
			String content = Files.readString(Paths.get(defaultComponentsFile));
			return GSON.fromJson(content, JsonArray.class);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return null;

	}
}
