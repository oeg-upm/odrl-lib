package odrl.lib.model.nodes;

public class OperandValue implements IOperand{

	private String value;
	private String type;
	private String lang;
	private boolean isUri = false;

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public OperandValue(String value) {
		super();
		this.value = value;
	}


	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}




	public boolean isUri() {
		return isUri;
	}

	public void setUri(boolean isUri) {
		this.isUri = isUri;
	}

	private static final char TOKEN_STRING = '"';
	private static final char TOKEN_AT = '@';
	private static final String TOKEN_DATATYPE_1 = "^^<";
	private static final String TOKEN_DATATYPE_2 = ">";

	@Override
	public String toSPARQL() {
		StringBuilder sparqlRepresentation = new StringBuilder(value);
		if(!isUri) {
			sparqlRepresentation = new StringBuilder();
			sparqlRepresentation.append(TOKEN_STRING).append(value).append(TOKEN_STRING);
			if(lang!=null) {
				sparqlRepresentation.append(TOKEN_AT).append(lang);
			}else if(type!=null) {
				sparqlRepresentation.append(TOKEN_DATATYPE_1).append(type).append(TOKEN_DATATYPE_2);
			}
		}
		return sparqlRepresentation.toString();
	}


}
