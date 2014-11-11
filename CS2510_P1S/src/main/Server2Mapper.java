package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Server2Mapper entity, represent a registration message from server to mapper
 * 
 * @author dexterchen
 *
 */
public class Server2Mapper implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 347214735564740569L;

	/**
	 * Address of the server
	 */
	public Address address;

	/**
	 * Address - Uid(s) mapping, Address - List of uids
	 */
	public Map<Address, Queue<String>> addr2uids;

	// public ConcurrentHashMap<String, ServerList> uid2serverlist;
	/**
	 * Uid - Server list mapping, (program Id+version+procedure Id+TYPELIST) -
	 * List of server address
	 */
	public Map<String, Queue<Address>> uid2serverlist;

	/**
	 * Constructor
	 * 
	 * @param address
	 *            Address of the server
	 * @param addr2uids
	 *            Address - Uid(s) mapping, Address - List of uids
	 * @param uid2serverlist
	 *            Uid - Server list mapping, (program Id+version+procedure
	 *            Id+TYPELIST) - List of server address
	 */
	public Server2Mapper(Address address,
			Map<Address, Queue<String>> addr2uids,
			Map<String, Queue<Address>> uid2serverlist) {
		super();
		this.address = address;
		this.addr2uids = addr2uids;
		this.uid2serverlist = uid2serverlist;
	}

}
