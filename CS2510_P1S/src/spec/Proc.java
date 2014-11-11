package spec;

import java.util.*;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "proc")
@XmlAccessorType(XmlAccessType.FIELD)
public class Proc {
	@XmlElement(name = "param")
	public List<Param> params;
	@XmlElement(name = "type")
	public String type;
	@XmlElement(name = "id")
	public int id;
	public List<Param> getParams() {
		return params;
	}
	public void setParams(List<Param> params) {
		this.params = params;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
