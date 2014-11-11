package main;

import java.io.Serializable;

/**
 * Address entity, represents an address
 * 
 * @author dexterchen
 *
 */
public class Address implements Serializable {
	private static final long serialVersionUID = 3128206828722791912L;
	/**
	 * IP and port of this address
	 */
	public String ip;
	public Integer port;

	/**
	 * Constructor
	 * 
	 * @param ip
	 *            IP of this address
	 * @param port
	 *            port of this address
	 */
	public Address(String ip, Integer port) {
		super();
		this.ip = ip;
		this.port = port;
	}

	/**
	 * Print info of this object
	 */
	public String toString() {
		return "(" + ip + ":" + port + ")";
	}

	/**
	 * Override to enable uniqueness checking for customized object
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof Address) {
			Address pp = (Address) obj;
			return (pp.ip.equals(this.ip) && pp.port.equals(this.port));
		} else {
			return false;
		}
	}

	/**
	 * Override to enable uniqueness checking for customized object
	 */
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		int result = HashCodeUtil.hash(HashCodeUtil.SEED, ip);
		result = HashCodeUtil.hash(result, port);
		return result;
	}

	/**
	 * Make a copy of this object
	 * 
	 * @return a copy
	 */
	public Address cloneObj() {
		return new Address(ip, port);
	}

}
