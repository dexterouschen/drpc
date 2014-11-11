package spec;
import javax.xml.bind.annotation.*;

@XmlRootElement (name = "prog_name")
public class Spec {
	@XmlElement(name ="p1")
	String p1;
	@XmlElement(name = "p2")
	int p2;
	
	public String getP1(){
		return p1;
	}
	
	public void setP1(String p1){
		this.p1 = p1;
	}
	
	public int getP2(){
		return p2;
	}
	
	public void setP2(int p2){
		this.p2 = p2;
	}
}
