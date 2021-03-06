package ttworkbench.play.parameters.settings.data;

import java.util.Map;

import ttworkbench.play.parameters.settings.Data;

public class ValidatorImpl implements Data.Validator {

	private Class<?> type;
	private Map<String, String> attrs;

	public ValidatorImpl(Class<?> type, Map<String, String> attrs) {
		this.type = type;
		this.attrs = attrs;
	}

	public Class<?> getType() {
		return type;
	}

	public Map<String, String> getAttributes() {
		return attrs;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attrs == null) ? 0 : attrs.hashCode());
		result = prime * result
				+ ((type == null) ? 0 : type.toString().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValidatorImpl other = (ValidatorImpl) obj;
		if (attrs == null) {
			if (other.attrs != null)
				return false;
		} else if (!attrs.equals(other.attrs))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.toString().equals(other.type.toString()))
			return false;
		return true;
	}

	
	
}
