package main;

import java.io.Serializable;

/**
 * Server2MapperRefresh entity, represent a refresh from server to mapper
 * 
 * @author dexterchen
 *
 */
public class Server2MapperRefresh implements Serializable {
	private static final long serialVersionUID = 5925446165843179001L;

	/**
	 * Address of the server
	 */
	public Address address;

	/**
	 * Constructor
	 * 
	 * @param address
	 *            Address of the server
	 */
	public Server2MapperRefresh(Address address) {
		super();
		this.address = address;
	}

}
