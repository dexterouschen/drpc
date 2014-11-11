package spec;

import java.io.Serializable;

public class Param implements Serializable {
	
	private static final long serialVersionUID = 3794975156461505658L;
	public String type;
	public Object value;

	public Param(String t, Object v) {
		type = t;
		value = v;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String toString() {
		return "<" + type + "," + value + ">";
	}
}
