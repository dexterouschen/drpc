//package x;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.Socket;
//import java.net.SocketException;
//import java.util.LinkedList;
//import java.util.Map;
//import java.util.Queue;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.UUID;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//import main.Client2Mapper;
//import main.Client2Server;
//import main.NetTool;
//import main.Server2ClientNeed;
//import main.Server2ClientResult;
//import main.TransactionId;
//import main.UnifiedPacket;
//import spec.Param;
//
//public class ClientRequestHandlerV2 {
//
//	/**
//	 * Universal socket for all the requests
//	 */
//	static DatagramSocket socketToServer;
//
//	/**
//	 * Universal thread pool for all the requests
//	 */
//	static ExecutorService service = Executors.newCachedThreadPool();
//	/**
//	 * Thread pool that runs only one task (keep sending packets if any)
//	 */
//	static ThreadPoolExecutor single = new ThreadPoolExecutor(2, 2, 0,
//			TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
//
//	/**
//	 * For sending, shared by threads, dump generated DatagramPacket(s) here
//	 */
//	static Queue<DatagramPacket> packets2Send = new ConcurrentLinkedQueue<>();
//	/**
//	 * For resent, <UUID from TransactionId, needed packets>, checked frequently
//	 * by threads
//	 */
//	static Map<UUID, byte[]> neededPackets = new ConcurrentHashMap<>(16, 0.9f,
//			1);
//
//	/**
//	 * For reception, <UUID from TransactionId, Queue of UnifiedPacket(s)>
//	 */
//	static Map<UUID, Queue<UnifiedPacket>> packetReceived = new ConcurrentHashMap<>();
//	/**
//	 * For reception, <transactionId, returned result>, checked frequently by
//	 * threads
//	 */
//	static Map<TransactionId, Param> results = new ConcurrentHashMap<>(16,
//			0.9f, 1);
//
//	Object lock = new Object();
//
//	Timer debugTimer = new Timer();
//
//	static int loc = 0;
//
//	public ClientRequestHandlerV2() {
//		synchronized (lock) {
//
//			if (single.getActiveCount() == 0) {
//				try {
//					loc = 1;
//					socketToServer = new DatagramSocket();
//					loc = 2;
//					debugTimer.scheduleAtFixedRate(new TimerTask() {
//
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							System.out.println("stuck at " + loc);
//						}
//					}, 0, 500);
//				} catch (SocketException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				single.execute(new Runnable() {
//
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						while (true) {
//							while (!packets2Send.isEmpty()) {
//								try {
//									System.out.println("Send a UPacket");
//									socketToServer.send(packets2Send.poll());
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//							}
//						}
//					}
//				});
//
//				single.execute(new Runnable() {
//
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						while (true) {
//							byte[] receiveData = new byte[1024];
//							DatagramPacket receivePacket = new DatagramPacket(
//									receiveData, receiveData.length);
//							try {
//								loc = 3;
//								socketToServer.receive(receivePacket);
//								loc = 4;
//								Object message = NetTool
//										.deserialize(receivePacket.getData());
//								if (message instanceof UnifiedPacket) {
//									System.out
//											.println("Receive a UnifiedPacket");
//									UnifiedPacket p = (UnifiedPacket) message;
//									if (p != null) {
//										loc = 5;
//										Queue<UnifiedPacket> list = packetReceived
//												.get(p.id);
//										loc = 6;
//										if (list != null) {
//											list.add(p);
//											if ((int) p.total == list.size()) {
//												System.out
//														.println("Receive all UPacket");
//												byte total = p.total;
//												// int packetNum = list.size();
//												byte[] finalData = null;
//												UnifiedPacket first = NetTool
//														.find((byte) 1, list);
//												loc = 7;
//												if (first != null) {
//													finalData = first.partial;
//
//													for (byte i = 2; i <= total; i++) {
//														UnifiedPacket packet = NetTool
//																.find(i, list);
//														loc = 8;
//														if (packet != null) {
//															finalData = NetTool
//																	.join(finalData,
//																			packet.partial);
//														} else {
//															finalData = null;
//															loc = 9;
//															packetReceived
//																	.put(p.id,
//																			new LinkedList<UnifiedPacket>());
//															break;
//														}
//
//													}
//												}
//												try {
//													if (finalData != null
//															&& finalData.length > 0) {
//														loc = 10;
//														Server2ClientResult result = (Server2ClientResult) NetTool
//																.deserialize(finalData);
//														results.put(
//																result.transactionId,
//																result.param);
//													}
//												} catch (ClassNotFoundException e) {
//													// TODO Auto-generated catch
//													// block
//													e.printStackTrace();
//												} catch (IOException e) {
//													// TODO Auto-generated catch
//													// block
//													e.printStackTrace();
//												}
//												loc = 11;
//											}
//										} else {
//											loc = 12;
//											Queue<UnifiedPacket> nl = new LinkedList<>();
//											nl.offer(p);
//											packetReceived.put(p.id, nl);
//										}
//									}
//
//								} else if (message instanceof Server2ClientNeed) {
//									loc = 13;
//									System.out
//											.println("Receive a Server2ClientNeed");
//									Server2ClientNeed need = (Server2ClientNeed) message;
//									neededPackets.put(need.id, need.needed);
//								}
//
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} catch (ClassNotFoundException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//					}
//				});
//			}
//		}
//	}
//
//	public static Object getResult(Client2Mapper crtm, Client2Server crts) {
//		// System.out.println("getResult");
//		loc = 14;
//		Future<Param> result = service.submit(new ClientTask(crtm, crts));
//		loc = 15;
//		try {
//
//			return ((Param) result.get()).value;
//		} catch (InterruptedException | ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}
//}
