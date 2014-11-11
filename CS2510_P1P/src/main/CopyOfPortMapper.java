package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Responsible for checking upon main mapper and taking over its job when it
 * crushes, standby mapper has the identical functionality as the main mapper
 * once it takes over
 * 
 * @author dexterchen
 *
 */
public class CopyOfPortMapper {
	/**
	 * Address - Uid(s) mapping
	 */
	static Map<Address, Queue<String>> addr2uids = new ConcurrentHashMap<>(16,
			0.9f, 1);
	/**
	 * Address - Time stamp mapping
	 */
	static Map<Address, Long> addr2ts = new ConcurrentHashMap<>(16, 0.9f, 1);

	/**
	 * Uid - Server list mapping
	 */
	static Map<String, Queue<Address>> uid2serverlist = new ConcurrentHashMap<>(
			16, 0.9f, 1);

	/**
	 * Update mapper address in public DNS and construct a server socket
	 * 
	 * @return new server socket
	 */
	private static ServerSocket updatePublicEntry() {
		/**
		 * Get public address of this mapper
		 */
		String ip = NetTool.getPublicAddress();
		/**
		 * ServerSocket to get random port
		 */
		ServerSocket socket;
		try {
			socket = new ServerSocket(0);
			int port = socket.getLocalPort();
			socket.close();
			/**
			 * Write to DNS
			 */
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					"/afs/cs.pitt.edu/usr0/dextercoder/Public/CS2510_P1P/src/dns.txt", false));
			writer.write(ip + ":" + port);
			writer.newLine();
			writer.flush();
			writer.close();
			System.out.println("Public directory updated: <" + ip + ":" + port
					+ ">");
			/**
			 * Construct ServerSocket with designated port and return
			 */
			return new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return null;
	}

	/**
	 * Helper method to print status of all data structure
	 */
	static void printStatus() {
		// TODO Auto-generated method stub
		System.out.println("\nAddr2Timestamp:"
				+ CopyOfPortMapper.addr2ts.size() + "|" + "Addr2Uids:"
				+ CopyOfPortMapper.addr2uids.size() + "|" + "Uid2ServerList:"
				+ CopyOfPortMapper.uid2serverlist.size());

		System.out.println("--------------------------------");

		System.out.println("Address2Timestamp: ");

		for (Entry<Address, Long> e : CopyOfPortMapper.addr2ts.entrySet()) {
			System.out.println(e.getKey() + " " + e.getValue());
		}

		System.out.println("Address2Uids: ");
		for (Entry<Address, Queue<String>> e : CopyOfPortMapper.addr2uids
				.entrySet()) {
			System.out.println(e.getKey() + " " + e.getValue());
		}
		System.out.println("Uid2ServerList: ");
		for (Entry<String, Queue<Address>> e : CopyOfPortMapper.uid2serverlist
				.entrySet()) {
			System.out.println(e.getKey() + " " + e.getValue());
		}

		System.out.println("--------------------------------");
	}

	/**
	 * Eliminate dead servers from data structures
	 */
	static void eliminate() {
		System.out.println("Start elimination.");

		/**
		 * Parallel elimination
		 */
		addr2ts.entrySet().stream().parallel().unordered().forEach(x -> {
			/**
			 * No response more than 9000 milliseconds
			 */
			if (System.currentTimeMillis() - x.getValue() > 9000) {
				Address address = x.getKey();
				/**
				 * Take it out from Address - Time stamp mapping
				 */
				addr2ts.remove(address);
				/**
				 * Which Uid(s) this server supports should all get eliminated
				 */
				Queue<String> uids = addr2uids.get(address);
				if (uids != null) {
					/**
					 * Parallel elimination
					 */
					uids.stream().parallel().unordered().forEach(y -> {
						/**
						 * Eliminate server address from all server lists
						 */
						Queue<Address> sl = uid2serverlist.get(y);
						if (sl != null) {
							sl.remove(address);
						}
					});
					/**
					 * Take it out from Address - Uid(s) mapping
					 */
					addr2uids.remove(address);
				}
			}
		});

		System.out.println("End elimination.");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/**
		 * Thread pool
		 */
		ExecutorService executorService = Executors.newCachedThreadPool();

		/**
		 * Check upon main mapper
		 */
		while (true) {
			try {
				/**
				 * Keep sending and receiving dummy message to main mapper
				 */
				String[] parts = NetTool.getMapperAddress().split(":");
				Socket socket = new Socket(parts[0], Integer.valueOf(parts[1]));
				ObjectOutputStream o = new ObjectOutputStream(
						socket.getOutputStream());
				o.writeObject(new Mapper2Mapper("1"));
				o.flush();
				ObjectInputStream i = new ObjectInputStream(
						socket.getInputStream());
				Object r = i.readObject();
				if (r instanceof Mapper2Mapper) {
					System.out.println("Main Mapper is still alive");
				}
				i.close();
				o.close();
				socket.close();
			} catch (Exception e) {
				// TODO: handle exception
				// e.printStackTrace();
				/**
				 * Once any exception throws, break out, takes over and act as
				 * the new main mapper
				 */
				System.out.println("Main Mapper crushed, Standby takes over");
				break;
			}
		}

		/**
		 * listener socket for all requests
		 */
		ServerSocket listener = updatePublicEntry();

		// Timer timer = new Timer();
		// timer.scheduleAtFixedRate(new TimerTask() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// try {
		// eliminate();
		// } catch (Exception e) {
		// // TODO: handle exception
		// // System.out.println("Eliminate: " + e.getMessage());
		// e.printStackTrace();
		// }
		//
		// }
		// }, 9000, 9000);

		while (true) {

			try {
				/**
				 * Accept a socket
				 */
				Socket socket = listener.accept();
				/**
				 * Dispatch it to a thread
				 */
				executorService.execute(new PMThread(socket));

				// socket.setSoTimeout(10000);
				//
				// InputStream is = socket.getInputStream();
				// ObjectInputStream ois = new ObjectInputStream(is);
				// Object request = ois.readObject();
				// ObjectOutputStream oos = new ObjectOutputStream(
				// socket.getOutputStream());
				//
				// if (request instanceof ClientRequestToMapper) {
				// System.out.println("Receive: Search request of " + request);
				//
				// // Iterator it = uid2serverlist.entrySet().iterator();
				// // while (it.hasNext()) {
				// // Map.Entry pairs = (Map.Entry) it.next();
				// // System.out.println(pairs.getKey() + " = "
				// // + pairs.getValue());
				// // it.remove(); // avoids a ConcurrentModificationException
				// // }
				// Address serverAddress = executorService
				// .submit(new PMSearchThread(
				// (ClientRequestToMapper) request)).get();
				// if (serverAddress != null) {
				// System.out.println("Server available for: " + request);
				// oos.writeObject(serverAddress);
				//
				// } else {
				// System.out.println("No server available for: "
				// + request);
				// }
				//
				// } else if (request instanceof ServerRequestToMapper) {
				// System.out.println("Receive: Register from "
				// + ((ServerRequestToMapper) request).address);
				// Boolean result = executorService.submit(
				// new PMRegisterThread(
				// (ServerRequestToMapper) request)).get();
				// System.out.println(addr2ts.size() + "|" + addr2uids.size()
				// + "|" + uid2serverlist.size());
				//
				// // Iterator it = addr2ts.entrySet().iterator();
				// // while (it.hasNext()) {
				// // Map.Entry pairs = (Map.Entry) it.next();
				// // System.out.println(pairs.getKey() + " = "
				// // + pairs.getValue());
				// // it.remove(); // avoids a ConcurrentModificationException
				// // }
				//
				// if (result != null) {
				// oos.writeObject("SUCCESS");
				// }
				// } else if (request instanceof ServerRefreshToMapper) {
				// System.out.println("Receive: Refresh "
				// + ((ServerRefreshToMapper) request).address);
				// addr2ts.put(((ServerRefreshToMapper) request).address,
				// System.currentTimeMillis());
				// }
				// oos.flush();
				// oos.close();
				// ois.close();
				// socket.close();

			} catch (SocketException e) {
				// e.printStackTrace();
				System.out.println(e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}

	}
}
