package main;

import java.io.Serializable;

/**
 * Mapper2Mapper entity, represent a dummy message between main mapper and
 * standby mapper
 * 
 * @author dexterchen
 *
 */
public class Mapper2Mapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8566664925107867379L;
	/**
	 * Dummy message
	 */
	String message;

	/**
	 * Constructor
	 * 
	 * @param m
	 *            dummy message
	 */
	public Mapper2Mapper(String m) {
		// TODO Auto-generated constructor stub
		message = m;
	}
}
