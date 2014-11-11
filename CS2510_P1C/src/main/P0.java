package main;

import java.io.Serializable;

/**
 * P0 entity, represents a procedure 0 message from client to server
 * 
 * @author dexterchen
 *
 */
public class P0 implements Serializable {

	private static final long serialVersionUID = 1606937712929001876L;
	/**
	 * A dummy message
	 */
	String message;

	/**
	 * Constructor
	 * 
	 * @param m
	 *            a dummy message
	 */
	public P0(String m) {
		// TODO Auto-generated constructor stub
		message = m;
	}
}
