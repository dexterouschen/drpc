package main;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map.Entry;
import java.util.Queue;

/**
 * A PortMapper task, responsible for handling of a new request (search,
 * register, refresh, check, etc.)
 * 
 * @author dexterchen
 *
 */
public class PMThread implements Runnable {
	/**
	 * Socket of the new request
	 */
	Socket socket;

	/**
	 * Constructor
	 * 
	 * @param s
	 *            socker of the new request
	 */
	public PMThread(Socket s) {
		// TODO Auto-generated constructor stub
		socket = s;
	}

	/**
	 * Search an available server for the client request
	 * 
	 * @param request
	 *            the client request
	 * @return Address of the server
	 */
	public Address search(Client2Mapper request) {
		/**
		 * Fetch the corresponding server list of an uid
		 */
		Queue<Address> sl = PortMapper.uid2serverlist.get(request.uid);

		if (sl != null) {
			/**
			 * Synchronized reading, round robin, A-B-C | A-B-C-A | pop A |
			 * B-C-A
			 */
			System.out.println(sl);
			synchronized (sl) {
				sl.offer(sl.peek());
				System.out.println(sl);
				return sl.poll();
			}
		} else {
			return null;
		}
	}

	/**
	 * Register a server to mapper
	 * 
	 * @param request
	 *            the server message
	 * @return
	 */
	public Boolean register(Server2Mapper request) {
		/**
		 * Update Address - Time Stamp mapping
		 */
		PortMapper.addr2ts.put(request.address, System.currentTimeMillis());

		/**
		 * Update Address - Uids mapping
		 */
		request.addr2uids.entrySet().stream().parallel().unordered()
				.forEach(x -> {
					PortMapper.addr2uids.put(x.getKey(), x.getValue());
				});

		/**
		 * Update Uid - Server list mapping in parallel
		 */
		request.uid2serverlist
				.entrySet()
				.stream()
				.forEach(
						x -> {
							Queue<Address> curSl = PortMapper.uid2serverlist
									.putIfAbsent(x.getKey(), x.getValue());
							if (curSl != null) {
								// Queue<Address> sl = PortMapper.uid2serverlist
								// .get(x.getKey());
								
								x.getValue().forEach(v -> {
									if (!curSl.contains(v)) {
										curSl.add(v);
									}
								});
								
								PortMapper.uid2serverlist.put(x.getKey(), curSl);
							}
						});

		return true;
	}

	/**
	 * Helper method to print status of all data structure
	 */
	private void printStatus() {
		// TODO Auto-generated method stub

		System.out.println("\nAddr2Timestamp:" + PortMapper.addr2ts.size()
				+ "|" + "Addr2Uids:" + PortMapper.addr2uids.size() + "|"
				+ "Uid2ServerList:" + PortMapper.uid2serverlist.size());

		System.out.println("--------------------------------");

		System.out.println("Address2Timestamp: ");

		for (Entry<Address, Long> e : PortMapper.addr2ts.entrySet()) {
			System.out.println(e.getKey() + " " + e.getValue());
		}

		System.out.println("Address2Uids: ");
		for (Entry<Address, Queue<String>> e : PortMapper.addr2uids.entrySet()) {
			System.out.println(e.getKey() + " " + e.getValue());
		}
		System.out.println("Uid2ServerList: ");
		for (Entry<String, Queue<Address>> e : PortMapper.uid2serverlist
				.entrySet()) {
			System.out.println(e.getKey() + " " + e.getValue());
		}

		System.out.println("--------------------------------");
	}

	/**
	 * Handling procedure of a task
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			/**
			 * Get input and output stream from the socket
			 */
			InputStream is = socket.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			Object request = ois.readObject();
			ObjectOutputStream oos = new ObjectOutputStream(
					socket.getOutputStream());

			/**
			 * if it's a search
			 */
			if (request instanceof Client2Mapper) {
				/**
				 * Get a server based on request, see search for details
				 */
				Address serverAddress = search((Client2Mapper) request);

				if (serverAddress != null) {
					System.out.println("Server available for: " + request);
					oos.writeObject(serverAddress);

				} else {
					System.out.println("No server available for: " + request);
					printStatus();
				}

			} else if (request instanceof Server2Mapper) {
				/**
				 * If it's a register
				 */

				System.out.println("Receive: Register from "
						+ ((Server2Mapper) request).address);
				/**
				 * Register this server, see register for details
				 */
				Boolean result = register((Server2Mapper) request);
				printStatus();

				if (result != null) {
					oos.writeObject("SUCCESS");
				}

			} else if (request instanceof Server2MapperRefresh) {
				/**
				 * If it's a refresh
				 */
				System.out.println("Receive: Refresh "
						+ ((Server2MapperRefresh) request).address);
				/**
				 * Update Address - Time stamp mapping
				 */
				PortMapper.addr2ts.put(
						((Server2MapperRefresh) request).address,
						System.currentTimeMillis());
			} else if (request instanceof Mapper2Mapper) {
				/**
				 * If it's a message from standby mapper, simply write back a
				 * dummy message
				 */
				oos.writeObject(new Mapper2Mapper("1"));
			}
			oos.flush();
			oos.close();
			ois.close();
			socket.close();

		} catch (SocketException e) {
			// TODO: handle exception
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
