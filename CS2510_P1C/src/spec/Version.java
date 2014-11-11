package spec;
import java.util.*;
import javax.xml.bind.annotation.*;

@XmlRootElement(name="version")
@XmlAccessorType (XmlAccessType.FIELD)
public class Version {
	@XmlElement(name = "proc")
	List<Proc> procs;
	
	public void setProcs(List<Proc> procs){
		this.procs = procs;
	}
	
	public List<Proc> getProcs(){
		return procs;
	}
}
