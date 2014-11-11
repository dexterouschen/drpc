package main;

import java.io.Serializable;
import java.util.UUID;

/**
 * Server2ClientNeed entity, represents a request from server to client for all
 * lost packets
 * 
 * @author dexterchen
 *
 */
public class Server2ClientNeed implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6265504335691984632L;
	/**
	 * UUID, generated from TransactionId, one to one relationship
	 */
	public UUID id;
	/**
	 * All lost packets U-indexes needed by the server
	 */
	public int[] needed;

	/**
	 * Constructor
	 * 
	 * @param i
	 *            UUID
	 * @param n
	 *            U-indexes of all lost packets
	 */
	public Server2ClientNeed(UUID i, int[] n) {
		// TODO Auto-generated constructor stub
		id = i;
		needed = n;
	}
}
