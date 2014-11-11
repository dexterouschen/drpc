//package x;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.Map;
//import java.util.Queue;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.ConcurrentMap;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import main.Address;
//import main.NetTool;
//import main.Server2ClientResult;
//import main.Server2Mapper;
//import main.Server2MapperRefresh;
//import main.TransactionId;
//
//import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
//import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap.Builder;
//import com.googlecode.concurrentlinkedhashmap.EvictionListener;
//
//public class ServerRequestHandler {
//
//	static Address serverAddress;
//
//	public static String serverClass;
//
//	static int taskCount = 0;
//
//	static long lastInTimestamp = 0l;
//
//	static long sec2lastInTimestamp = 0l;
//
//	static long lastOutTimestamp = 0l;
//
//	static long sec2lastOutTimestamp = 0l;
//
//	static int maxCount = 2000;
//
//	static int loc = 0;
//
//	static EvictionListener<TransactionId, Server2ClientResult> listener = new EvictionListener<TransactionId, Server2ClientResult>() {
//		final ExecutorService executor = Executors.newSingleThreadExecutor();
//
//		@Override
//		public void onEviction(final TransactionId a, final Server2ClientResult b) {
//			executor.submit(new Runnable() {
//				@Override
//				public void run() {
//					System.out.println("Evict cache: " + a + "->" + b.param);
//				}
//			});
//		}
//	};
//
//	static Map<TransactionId, Server2ClientResult> cache = new ConcurrentLinkedHashMap.Builder<TransactionId, Server2ClientResult>()
//			.maximumWeightedCapacity(1000).listener(listener).build();
//
//	public static ServerSocket startServer() {
//		long waitTime = 2000;
//		ServerSocket socket;
//		try {
//			socket = new ServerSocket(0);
//
//			serverAddress = new Address(NetTool.getPublicAddress(),
//					socket.getLocalPort());
//
//			// ConcurrentHashMap<String, ServerList> uid2sl =
//			// constructUid2ServerList(serverAddress);
//			Map<String, Queue<Address>> uid2sl = constructUid2ServerList(serverAddress);
//			Map<Address, Queue<String>> addr2uids = constructAddr2Uids(serverAddress);
//
//			boolean success = false;
//			do {
//				success = registerServer(uid2sl, addr2uids, serverAddress);
//				Thread.sleep(waitTime);
//				waitTime *= 2;
//			} while (!success && waitTime < 9000);
//
//			if (waitTime > 8000) {
//				return null;
//			}
//			System.out.println(serverAddress);
//			socket.setSoTimeout(2000);
//			return socket;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			// e.printStackTrace();
//			System.out.println(e.getMessage());
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			// e.printStackTrace();
//			System.out.println(e.getMessage());
//		}
//		return null;
//	}
//
//	public static void refresh() {
//		// ServerSocket socket;
//		try {
//			// socket = new ServerSocket(0);
//			// Address address = new Address(NetTool.getPublicAddress(),
//			// socket.getLocalPort());
//			// socket.close();
//
//			String[] parts = NetTool.getMapperAddress().split(":");
//			Socket socket2 = new Socket(parts[0], Integer.valueOf(parts[1]));
//			ObjectOutputStream oos = new ObjectOutputStream(
//					socket2.getOutputStream());
//
//			Server2MapperRefresh ref = new Server2MapperRefresh(serverAddress);
//
//			oos.writeObject(ref);
//			oos.flush();
//			oos.close();
//			socket2.close();
//
//		} catch (IOException e) {
//			// TODO: handle exception
//			System.out.println(e.getMessage());
//		}
//	}
//
//	private static boolean registerServer(Map<String, Queue<Address>> uid2sl,
//			Map<Address, Queue<String>> addr2uids, Address address) {
//		try {
//			// BufferedReader reader = new BufferedReader(
//			// new FileReader("dns.txt"));
//			// String line = reader.readLine();
//			String[] parts = NetTool.getMapperAddress().split(":");
//			// reader.close();
//
//			Socket socket = new Socket(parts[0], Integer.valueOf(parts[1]));
//			ObjectOutputStream oos = new ObjectOutputStream(
//					socket.getOutputStream());
//			Server2Mapper srtm = new Server2Mapper(address, addr2uids, uid2sl);
//
//			oos.writeObject(srtm);
//			oos.flush();
//
//			ObjectInputStream ois = new ObjectInputStream(
//					socket.getInputStream());
//			Object result = (String) ois.readObject();
//
//			oos.close();
//			socket.close();
//			if (result.equals("SUCCESS")) {
//				return true;
//			} else {
//				return false;
//			}
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			// e.printStackTrace();
//			System.out.println(e.getMessage());
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			// e.printStackTrace();
//			System.out.println(e.getMessage());
//		}
//		return false;
//
//	}
//
//	private static Map<Address, Queue<String>> constructAddr2Uids(Address addr) {
//		Map<Address, Queue<String>> map = new ConcurrentHashMap<>();
//		Queue<String> list = new ConcurrentLinkedQueue<String>();
//		try {
//			BufferedReader reader = new BufferedReader(new FileReader(
//					"lib/info.txt"));
//			String line = reader.readLine();
//			while (line != null) {
//				list.offer(line);
//				line = reader.readLine();
//			}
//			reader.close();
//			map.putIfAbsent(addr, list);
//
//			return map;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			// e.printStackTrace();
//			System.out.println(e.getMessage());
//		}
//
//		return null;
//	}
//
//	private static Map<String, Queue<Address>> constructUid2ServerList(
//			Address addr) {
//
//		ConcurrentHashMap<String, Queue<Address>> map = new ConcurrentHashMap<>();
//		Queue<Address> sl = new ConcurrentLinkedQueue<Address>();
//		sl.offer(addr);
//		try {
//			BufferedReader reader = new BufferedReader(new FileReader(
//					"lib/info.txt"));
//			String line = reader.readLine();
//			// System.out.println("3.1.");
//			while (line != null) {
//				// System.out.println(line);
//				map.putIfAbsent(line, sl);
//				line = reader.readLine();
//			}
//			reader.close();
//			return map;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			// e.printStackTrace();
//			System.out.println(e.getMessage());
//		}
//
//		return null;
//	}
//
//	// public static void main(String[] args) {
//
//	public static void run() {
//
//		ExecutorService tp = Executors.newCachedThreadPool();
//
//		ServerSocket listener = null;
//
//		listener = startServer();
//
//		if (listener == null) {
//			System.out
//					.println("Registration failed after 3 attempts, shutdown");
//			// Timer timer = new Timer();
//			// timer.schedule(new TimerTask() {
//			//
//			// @Override
//			// public void run() {
//			// // TODO Auto-generated method stub
//			// System.exit(0);
//			// }
//			// }, 5000);
//		} else {
//			System.out.println("Registration succeed.");
//			Timer timer = new Timer();
//			timer.scheduleAtFixedRate(new TimerTask() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					try {
//						// if (lastOutTimestamp - sec2lastOutTimestamp != 0) {
//						// maxCount /= (lastInTimestamp - sec2lastInTimestamp)
//						// / (lastOutTimestamp - sec2lastOutTimestamp);
//						// }
//
//						if (taskCount < 5000) {
//							refresh();
//						}
//
//					} catch (Exception e) {
//						// TODO: handle exception
//						System.out.println(e.getMessage());
//					}
//				}
//			}, 0, 3000);
//
//			Timer timer1 = new Timer();
//			timer1.scheduleAtFixedRate(new TimerTask() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					try {
//						System.out.println("stuck at" + loc);
//
//					} catch (Exception e) {
//						// TODO: handle exception
//						System.out.println(e.getMessage());
//					}
//				}
//			}, 0, 500);
//
//			while (true) {
//				try {
//					loc = 1;
//					Socket socket = listener.accept();
//					loc = 2;
//					sec2lastInTimestamp = lastInTimestamp;
//
//					lastInTimestamp = System.currentTimeMillis();
//
//					tp.execute(new ServerTask(socket));
//
//					// Future<ServerResponseToClient> task = null;
//					// InputStream is = socket.getInputStream();
//					// ObjectInputStream ois = new ObjectInputStream(is);
//					// ClientRequestToServer cr = (ClientRequestToServer)
//					// ois
//					// .readObject();
//					//
//					// ServerResponseToClient cachedResult = null;
//					//
//					// boolean isProc0 = cr.procId.equals("proc0");
//					// // avoid unnecessary lookup
//					// if (!isProc0) {
//					// cachedResult = cache.get(cr.transactionId);
//					// }
//					//
//					// if (cr != null && cachedResult == null && !isProc0) {
//					// System.out.println("Receive: " + cr);
//					// task = tp.submit(new Task(cr.transactionId,
//					// cr.programId, cr.version, cr.procId,
//					// cr.params));
//					// }
//					// ServerResponseToClient result = null;
//					// if (task != null) {
//					// result = task.get();
//					// if (!isProc0) {
//					// cache.putIfAbsent(cr.transactionId, result);
//					// System.out.println("New cache: "
//					// + cr.transactionId + ", Cache size: "
//					// + cache.size());
//					// }
//					// } else if (!isProc0) {
//					// System.out.println("Cache result found for "
//					// + cr.transactionId);
//					// result = cachedResult;
//					// } else {
//					// result = new ServerResponseToClient(
//					// cr.transactionId, new Param("int", 0));
//					// }
//					//
//					// ObjectOutputStream oos = new ObjectOutputStream(
//					// socket.getOutputStream());
//					//
//					// oos.writeObject(result);
//					//
//					// oos.flush();
//					// oos.close();
//					// ois.close();
//					// socket.close();
//
//				} catch (IOException e) {
//					// e.printStackTrace();
//					System.out.println(e.getMessage());
//				}
//				// finally {
//				// try {
//				// listener.close();
//				// } catch (IOException e) {
//				// // TODO Auto-generated catch block
//				// // e.printStackTrace();
//				// System.out.println(e.getMessage());
//				// }
//				// }
//			}
//		}
//	}
//}
