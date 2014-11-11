package main;

import java.io.Serializable;
/**
 * Client2Mapper entity, represents a request from client to mapper 
 * @author dexterchen
 *
 */
public class Client2Mapper implements Serializable {

	private static final long serialVersionUID = 7380595810492509817L;
	/**
	 * uid (programId+version+procId+TYPELIST)
	 */
	public String uid;

	// public String programId;
	// public Integer version;
	// public String procId;
	// public ArrayList<Class> types;

	/**
	 * Print info of this object
	 */
	public String toString() {
		return uid;
		// return programId + version + procId + types.toString();
	}
}
