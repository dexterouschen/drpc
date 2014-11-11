package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.EvictionListener;

public class ServerRequestHandlerV2 {

	/**
	 * Path to configuration file
	 */
	static String path = "lib/info.txt";

	/**
	 * Server socket to handler all requests
	 */
	static DatagramSocket serverSocket = null;

	/**
	 * Address of this server
	 */
	static Address serverAddress;

	/**
	 * Class name of the server side class which will utilize this handler
	 */
	public static String serverClass;

	/**
	 * Current number of active tasks
	 */
	static int taskCount = 0;

	// static long lastInTimestamp = 0l;

	// static long sec2lastInTimestamp = 0l;

	// static long lastOutTimestamp = 0l;

	// static long sec2lastOutTimestamp = 0l;

	// static int maxCount = 2000;

	// static int loc = 0;

	/**
	 * Last mapper address, used for comparison when refresh, determine if
	 * re-register is needed
	 */
	static String lastMapper;

	/**
	 * Listener for the cache, when an elimination happens, this listener will
	 * respond with a method
	 */
	static EvictionListener<TransactionId, Server2ClientResult> listener = new EvictionListener<TransactionId, Server2ClientResult>() {
		final ExecutorService executor = Executors.newSingleThreadExecutor();

		/**
		 * Method used to respond to elimination
		 */
		@Override
		public void onEviction(final TransactionId a,
				final Server2ClientResult b) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					/**
					 * Simply print out the cache record that get eliminated
					 */
					System.out.println("Evict cache: " + a + "->" + b.param);
				}
			});
		}
	};

	/**
	 * Server cache, has a maximum capacity, eliminate the least recently used
	 * record (LRU-based) when cache is full and a new record is going to be
	 * added
	 */
	static Map<TransactionId, Server2ClientResult> cache = new ConcurrentLinkedHashMap.Builder<TransactionId, Server2ClientResult>()
			.maximumWeightedCapacity(1000).listener(listener).build();

	/**
	 * For reception, <UUID from TransactionId, Queue of UnifiedPacket(s)>, UUID
	 * is constructed from TransactionId, so all corresponding UnifiedPacket(s)
	 * can be correctly classified
	 */
	static Map<UUID, Map<Integer, UnifiedPacket>> packetReceived = new ConcurrentHashMap<>();

	/**
	 * Since all the packets are in the format of customized structure
	 * (UnifiedPacket), we need extra mapping between a collection of
	 * UnifiedPacket(s) and Address of its sender
	 */
	static Map<UUID, Address> destination = new ConcurrentHashMap<>();

	/**
	 * Store the last batch of completed transactions
	 */
	static Map<UUID, Object> completed = new ConcurrentHashMap<>();

	/**
	 * Start the server
	 * 
	 * @param reconnect
	 *            if this happens for re-registration when original mapper
	 *            crushed and standby mapper takes over
	 */
	public static void startServer(boolean reconnect) {
		long waitTime = 2000;
		try {
			/**
			 * If this is for an actual start, then server socket and server
			 * address need to be assigned, but if not, simply skip and register
			 * again to the new mapper
			 */
			if (!reconnect) {
				serverSocket = new DatagramSocket();

				serverAddress = new Address(NetTool.getPublicAddress(),
						serverSocket.getLocalPort());
			}
			/**
			 * Uid - Server list mapping of this server
			 */
			Map<String, Queue<Address>> uid2sl = constructUid2ServerList(serverAddress);
			/**
			 * Address - Uid(s) mapping of this server
			 */
			Map<Address, Queue<String>> addr2uids = constructAddr2Uids(serverAddress);

			/**
			 * Keep trying to register until success or 3 attempts
			 */
			boolean success = false;
			do {
				success = registerServer(uid2sl, addr2uids, serverAddress);
				if (!success) {
					/**
					 * Wait for waitTime, each time fails, double the waitTime
					 */
					Thread.sleep(waitTime);
					waitTime *= 2;
				}
			} while (!success && waitTime < 9000);
			/**
			 * If waitTime is exceeded, it means failure, server socket is set
			 * to null, or not, then means success, keep going and this method
			 * is finished
			 */
			if (waitTime > 8000) {
				serverSocket = null;
			}
			System.out.println(serverAddress);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Refresh, method used to send heart beat to mapper periodically
	 */
	public static void refresh() {
		// ServerSocket socket;
		try {
			// socket = new ServerSocket(0);
			// Address address = new Address(NetTool.getPublicAddress(),
			// socket.getLocalPort());
			// socket.close();

			/**
			 * Get current mapper address
			 */
			String currentMapper = NetTool.getMapperAddress();
			/**
			 * If mapper address changes, means a new mapper takes over, then
			 * re-register to the new mapper
			 */
			if (!currentMapper.equals(lastMapper)) {
				// serverSocket.close();
				startServer(true);
			}
			/**
			 * Connect to mapper and send the refresh message
			 */
			String[] parts = currentMapper.split(":");
			Socket socket2 = new Socket(parts[0], Integer.valueOf(parts[1]));
			ObjectOutputStream oos = new ObjectOutputStream(
					socket2.getOutputStream());

			Server2MapperRefresh ref = new Server2MapperRefresh(serverAddress);
			// System.out
			// .println("Refresh size: " + NetTool.serialize(ref).length);
			oos.writeObject(ref);
			oos.flush();
			oos.close();
			socket2.close();

		} catch (IOException e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Actual method for registration of this server
	 * 
	 * @param uid2sl
	 *            Uid - Server list mapping of this server
	 * @param addr2uids
	 *            Address - Uid(s) mapping of this server
	 * @param address
	 *            Address of this server
	 * @return
	 */
	private static boolean registerServer(Map<String, Queue<Address>> uid2sl,
			Map<Address, Queue<String>> addr2uids, Address address) {
		try {
			// BufferedReader reader = new BufferedReader(
			// new FileReader("dns.txt"));
			// String line = reader.readLine();

			/**
			 * When register, cache the current mapper address
			 */
			lastMapper = NetTool.getMapperAddress();

			/**
			 * Connect to mapper and send the register message
			 */
			String[] parts = lastMapper.split(":");
			// reader.close();

			Socket socket = new Socket(parts[0], Integer.valueOf(parts[1]));
			ObjectOutputStream oos = new ObjectOutputStream(
					socket.getOutputStream());
			Server2Mapper srtm = new Server2Mapper(address, addr2uids, uid2sl);

			// System.out.println("Server2Mapper size:"
			// + NetTool.serialize(srtm).length);

			oos.writeObject(srtm);
			oos.flush();
			/**
			 * Wait for acknowledgement
			 */
			ObjectInputStream ois = new ObjectInputStream(
					socket.getInputStream());
			Object result = (String) ois.readObject();

			oos.close();
			socket.close();
			if (result.equals("SUCCESS")) {
				return true;
			} else {
				return false;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return false;

	}

	/**
	 * Construct Address - Uid(s) mapping of this server
	 * 
	 * @param addr
	 *            Address of this server
	 * @return Address - Uid(s) mapping of this server
	 */
	private static Map<Address, Queue<String>> constructAddr2Uids(Address addr) {
		Map<Address, Queue<String>> map = new ConcurrentHashMap<>();
		Queue<String> list = new ConcurrentLinkedQueue<String>();
		try {
			/**
			 * Read local configuration file
			 */
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line = reader.readLine();
			while (line != null) {
				list.offer(line);
				line = reader.readLine();
			}
			reader.close();
			/**
			 * For each uid, put it into mapping
			 */
			map.putIfAbsent(addr, list);

			return map;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(e.getMessage());
		}

		return null;
	}

	/**
	 * Construct Uid - Server list mapping of this server
	 * 
	 * @param addr
	 *            Address of this server
	 * @return Address - Uid(s) mapping of this server
	 */
	private static Map<String, Queue<Address>> constructUid2ServerList(
			Address addr) {

		ConcurrentHashMap<String, Queue<Address>> map = new ConcurrentHashMap<>();
		Queue<Address> sl = new ConcurrentLinkedQueue<Address>();
		sl.offer(addr);
		try {
			/**
			 * Read local configuration file
			 */
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line = reader.readLine();
			// System.out.println("3.1.");
			while (line != null) {
				// System.out.println(line);
				/**
				 * Associate the Address with all Uid(s) suppored by this server
				 */
				map.putIfAbsent(line, sl);
				line = reader.readLine();
			}
			reader.close();
			return map;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(e.getMessage());
		}

		return null;
	}

	// public static void main(String[] args) {

	/**
	 * Handling of requests of this server
	 */
	public static void run() {

		/**
		 * Thread pool
		 */
		ExecutorService tp = Executors.newCachedThreadPool();

		/**
		 * Start this server
		 */
		startServer(false);

		/**
		 * If a server socket can't be initiated, then shut down this server
		 */
		if (serverSocket == null) {
			System.out
					.println("Registration failed after 3 attempts, shutdown");
			// Timer timer = new Timer();
			// timer.schedule(new TimerTask() {
			//
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			System.exit(0);
			// }
			// }, 5000);
		} else {
			System.out.println("Registration succeed.");
			/**
			 * Timer for refreshing
			 */
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						// if (lastOutTimestamp - sec2lastOutTimestamp != 0) {
						// maxCount /= (lastInTimestamp - sec2lastInTimestamp)
						// / (lastOutTimestamp - sec2lastOutTimestamp);
						// }

						if (taskCount < 5000) {
							refresh();
						}

					} catch (Exception e) {
						// TODO: handle exception
						System.out.println(e.getMessage());
					}
				}
			}, 0, 15000);

			// Timer timer1 = new Timer();
			// timer1.scheduleAtFixedRate(new TimerTask() {
			//
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// try {
			// System.out.println("stuck at" + loc);
			//
			// } catch (Exception e) {
			// // TODO: handle exception
			// System.out.println(e.getMessage());
			// }
			// }
			// }, 0, 500);

			/**
			 * Timer for constructing non-received packet list and sending back
			 * to clients
			 */
			Timer timer2 = new Timer();
			timer2.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					// System.out.println(packetReceived.size());
					try {
						/**
						 * Parallel checking
						 */
						packetReceived
								.entrySet()
								.stream()
								.parallel()
								.unordered()
								.forEach(e -> {
									// System.out.println(e.getValue()
									// .size());
										/**
										 * For each entry, calculate the
										 * received packets' U-indexes and
										 * subtract them from the complete set,
										 * then send back to clients
										 */
										int total = e.getValue().get(1).total;
										List<Integer> cur = new ArrayList<>();
										List<Integer> all = new ArrayList<>();

										e.getValue().forEach((x, y) -> {
											cur.add(x);
										});

										for (Integer i = 1; i <= total; i++) {
											all.add(i);
										}
										all.removeAll(cur);
										if (all.size() == 0) {
											if (completed.containsKey(e
													.getKey())) {
												packetReceived.remove(e
														.getKey());
												completed.remove(e.getKey());
											}
										}
										int[] data = new int[all.size()];
										for (int i = 0; i < data.length; i++) {
											data[i] = all.get(i);
										}
										/**
										 * Construct the message
										 */
										Server2ClientNeed need = new Server2ClientNeed(
												e.getKey(), data);
										byte[] sendData = NetTool
												.serialize(need);
										/**
										 * Locate the destination client
										 */
										Address desAddr = destination.get(e
												.getKey());
										DatagramPacket toSend;
										try {
											if (sendData != null) {
												/**
												 * Send out the packet
												 */
												toSend = new DatagramPacket(
														sendData,
														sendData.length,
														InetAddress
																.getByName(desAddr.ip),
														desAddr.port);
												serverSocket.send(toSend);
											}
										} catch (Exception e2) {
											// TODO Auto-generated catch
											// block
											// e2.printStackTrace();
										}
									});
					} catch (Exception e) {

					}
				}
			}, 60000, 60000);
			/**
			 * Buffer for receiving
			 */
			byte[] receiveData = new byte[40960];
			while (true) {
				try {
					/**
					 * Packet for receiving
					 */
					DatagramPacket receivePacket = new DatagramPacket(
							receiveData, receiveData.length);
					/**
					 * Receive a packet
					 */
					serverSocket.receive(receivePacket);

					/**
					 * Try to convert the received data into an Object
					 */
					byte[] data = receivePacket.getData();
					Object message = NetTool.deserialize(receivePacket
							.getData());

					if (message instanceof UnifiedPacket) {
						/**
						 * For an UnifiedPacket
						 */
						UnifiedPacket p;
						System.out.println("Receive a UPacket"
								+ System.currentTimeMillis());
						p = (UnifiedPacket) NetTool.deserialize(data);
						if (p != null) {
							/**
							 * Get the collection of UnifiedPacket corresponding
							 * to a UUID
							 */
							Map<Integer, UnifiedPacket> map = packetReceived
									.get(p.id);
							if (map != null) {
								if (!completed.containsKey(p.id)) {
									map.putIfAbsent(p.current, p);
								}

								/**
								 * After each insertion, immediately check if
								 * the corresponding list is full, means all the
								 * packets needed for reconstructing the actual
								 * message are received
								 */
								if (p.total == map.keySet().size()) {

									// packetReceived.remove(p.id);
									System.out.println("Receive all UPacket"
											+ System.currentTimeMillis());
									/**
									 * If so, dispatch this entry <UUID,
									 * Queue<UnifiedPacket>> to a worker thread
									 * to perform the task
									 */
									tp.execute(new ServerTaskV2(p.id, map));
								}
							} else {
								/**
								 * If the collection is null, means this is the
								 * first packet (not necessary the packet with 1
								 * as U-index) arrived of the corresponding
								 * collection, then cache its sender address,
								 * initiate the list and put it there
								 */
								destination.put(p.id, new Address(receivePacket
										.getAddress().getHostAddress(),
										receivePacket.getPort()));
								Map<Integer, UnifiedPacket> nl = new ConcurrentHashMap<Integer, UnifiedPacket>();
								nl.putIfAbsent(p.current, p);
								packetReceived.put(p.id, nl);
								/**
								 * After each insertion, immediately check if
								 * the corresponding list is full, means all the
								 * packets needed for reconstructing the actual
								 * message are received
								 */
								Map<Integer, UnifiedPacket> m = packetReceived
										.get(p.id);

								if (p.total == m.keySet().size()) {

									// packetReceived.remove(p.id);
									System.out.println("Receive all UPacket"
											+ System.currentTimeMillis());
									/**
									 * If so, dispatch this entry <UUID,
									 * Queue<UnifiedPacket>> to a worker thread
									 * to perform the task
									 */
									tp.execute(new ServerTaskV2(p.id, m));
								}

							}
						}
					} else if (message instanceof P0) {
						/**
						 * If it's a P0
						 */
						P0 p0 = new P0("SUCCESS");
						byte[] sendP0 = NetTool.serialize(p0);
						/**
						 * Simply write back an acknowledgement with dummy
						 * message
						 */
						DatagramPacket p0p = new DatagramPacket(sendP0,
								sendP0.length, receivePacket.getAddress(),
								receivePacket.getPort());

						serverSocket.send(p0p);
					}
				} catch (IOException | ClassNotFoundException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}
}
