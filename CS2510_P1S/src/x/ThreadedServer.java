package x;
//package main;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.ConcurrentMap;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//
//import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
//import com.googlecode.concurrentlinkedhashmap.EvictionListener;
//
//public class ThreadedServer {
//
//	static Address serverAddress;
//
//	static EvictionListener<String, Server2Client> listener = new EvictionListener<String, Server2Client>() {
//		final ExecutorService executor = Executors.newSingleThreadExecutor();
//
//		@Override
//		public void onEviction(final String a, final Server2Client b) {
//			executor.submit(new Runnable() {
//				@Override
//				public void run() {
//					System.out.println("Evict cache: " + a + "->" + b.param);
//				}
//			});
//		}
//	};
//
//	static ConcurrentMap<String, Server2Client> cache = new ConcurrentLinkedHashMap.Builder<String, Server2Client>()
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
//			Map<String, ServerList> uid2sl = constructUid2ServerList(serverAddress);
//
//			Map<Address, ConcurrentLinkedQueue<String>> addr2uids = constructAddr2Uids(serverAddress);
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
//	private static void refresh() {
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
//	private static boolean registerServer(Map<String, ServerList> uid2sl,
//			Map<Address, ConcurrentLinkedQueue<String>> addr2uids,
//			Address address) {
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
//	private static Map<Address, ConcurrentLinkedQueue<String>> constructAddr2Uids(
//			Address addr) {
//		HashMap<Address, ConcurrentLinkedQueue<String>> map = new HashMap<>();
//		ConcurrentLinkedQueue<String> list = new ConcurrentLinkedQueue<>();
//		try {
//			BufferedReader reader = new BufferedReader(new FileReader(
//					"lib/info.txt"));
//			String line = reader.readLine();
//			while (line != null) {
//				list.add(line);
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
//	private static Map<String, ServerList> constructUid2ServerList(Address addr) {
//		HashMap<String, ServerList> map = new HashMap<>();
//		ConcurrentLinkedQueue<Address> list = new ConcurrentLinkedQueue<>();
//		ServerList sl = new ServerList(list);
//		sl.list.add(addr);
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
//	public static void main(String[] args) {
//
//		ExecutorService tp = Executors.newCachedThreadPool();
//		ServerSocket listener = null;
//
//		// System.out.println("1.");
//		listener = startServer();
//		// System.out.println("2.");
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
//					refresh();
//				}
//			}, 0, 30000);
//
//			while (true) {
//				try {
//					Socket socket = listener.accept();
//					try {
//						Future<Server2Client> task = null;
//						InputStream is = socket.getInputStream();
//						ObjectInputStream ois = new ObjectInputStream(is);
//						Client2Server cr = (Client2Server) ois.readObject();
//
//						Server2Client cachedResult = null;
//
//						if (!cr.procId.equals("proc0")) {
//
//							cachedResult = cache.get(cr.transactionId);
//						}
//
//						if (cr != null && cachedResult == null) {
//							System.out.println("Receive: " + cr);
//							task = tp.submit(new Task(cr.transactionId,
//									cr.programId, cr.version, cr.procId,
//									cr.params));
//						}
//						Server2Client result = null;
//						if (task != null) {
//							result = task.get();
//							if (!cr.procId.equals("proc0")) {
//								cache.putIfAbsent(cr.transactionId, result);
//								System.out.println("Cache: " + cr.transactionId
//										+ " ,Cache size: " + cache.size());
//							}
//						} else {
//							System.out.println("Cache result found for "
//									+ cr.transactionId);
//							result = cachedResult;
//						}
//
//						ObjectOutputStream oos = new ObjectOutputStream(
//								socket.getOutputStream());
//
//						oos.writeObject(result);
//
//						oos.flush();
//						oos.close();
//						ois.close();
//
//					} catch (IOException e) {
//						// e.printStackTrace();
//						System.out.println(e.getMessage());
//					} catch (ClassNotFoundException e) {
//						// TODO Auto-generated catch block
//						// e.printStackTrace();
//						System.out.println(e.getMessage());
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						// e.printStackTrace();
//						System.out.println(e.getMessage());
//					} catch (ExecutionException e) {
//						// TODO Auto-generated catch block
//						// e.printStackTrace();
//						System.out.println(e.getMessage());
//					} finally {
//						socket.close();
//					}
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
