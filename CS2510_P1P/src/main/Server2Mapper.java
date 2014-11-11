package main;

import java.io.Serializable;
import java.util.Map;
import java.util.Queue;

/**
 * Server2Mapper entity, represent a registration message from server to mapper
 * @author dexterchen
 *
 */
public class Server2Mapper implements Serializable {
	private static final long serialVersionUID = 347214735564740569L;
	// public ConcurrentHashMap<String, ServerList> uid2serverlist;
	/**
	 * Uid - Server list mapping, (program Id+version+procedure Id+TYPELIST) - List of server address
	 */
	public Map<String, Queue<Address>> uid2serverlist;
	/**
	 * Address - Uid(s) mapping, Address - List of uids
	 */
	public Map<Address, Queue<String>> addr2uids;
	/**
	 * Address of the server
	 */
	public Address address;
}
