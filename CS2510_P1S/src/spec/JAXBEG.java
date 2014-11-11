package spec;
import javax.xml.bind.*;

public class JAXBEG {
	public static void main(String []args){
		Spec o = new Spec();
		o.setP1("PPP111");
		o.setP2(222);
		
		try{
			JAXBContext jc = JAXBContext.newInstance(Spec.class);
			Marshaller jm = jc.createMarshaller();
			
			jm.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jm.marshal(o, System.out);
		} catch(JAXBException e){
			e.printStackTrace();
		}
		
	}
}
