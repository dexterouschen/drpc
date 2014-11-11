//package x;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.ServerSocket;
//import java.sql.Time;
//import java.util.ArrayList;
//import java.util.Map;
//import java.util.Queue;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.function.BiConsumer;
//
//import main.Client2Server;
//import main.UnifiedPacket;
//
//public class UDPServer {
//
//	static Map<UUID, Queue<UnifiedPacket>> packetBag = new ConcurrentHashMap<>();
//
//	static Queue<Client2Server> requests = new ConcurrentLinkedQueue<>();
//
//	public static Object deserialize(byte[] data) throws IOException,
//			ClassNotFoundException {
//		ByteArrayInputStream in = new ByteArrayInputStream(data);
//		ObjectInputStream is = new ObjectInputStream(in);
//		return is.readObject();
//	}
//
//	private static byte[] join(byte[] m1, byte[] m2) {
//		byte[] all = new byte[m1.length + m2.length];
//
//		System.arraycopy(m1, 0, all, 0, m1.length);
//		System.arraycopy(m2, 0, all, m1.length, m2.length);
//
//		return all;
//	}
//
//	static UnifiedPacket find(byte index, Queue<UnifiedPacket> list) {
//		for (UnifiedPacket packet : list) {
//			// System.out.println(packet.current + "/" + index);
//			if (packet.current == index) {
//				return packet;
//			}
//		}
//
//		return null;
//	}
//
//	static void check() { // instead of using this tedious function, we can
//							// check every time when a packet fall into a
//							// "bucket"
//							// if that bucket isFull then start assembling
//		((ConcurrentHashMap<UUID, Queue<UnifiedPacket>>) packetBag)
//				.forEach(10,
//						new BiConsumer<UUID, Queue<UnifiedPacket>>() {
//
//							@Override
//							public void accept(UUID t,
//									Queue<UnifiedPacket> u) {
//								
//								
//								// TODO Auto-generated method stub
//								byte total = u.element().total;
//								int packetNum = u.size();
//								System.out.println(packetNum + "/" + total);
//								if (total == packetNum) {
//									byte[] finalData = find((byte) 1, u).partial;
//
//									for (byte i = 2; i <= total; i++) {
//										UnifiedPacket packet = find(i, u);
//										if (packet != null) {
//											finalData = join(finalData,
//													packet.partial);
//										} else {
//											System.out.println("NULLLLLLL");
//										}
//
//									}
//									try {
//										Client2Server message = (Client2Server) deserialize(finalData);
//										requests.offer(message);
//									} catch (ClassNotFoundException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									} catch (IOException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//
//								}
//							}
//							
//							
//						});
//	}
//
//	public static void main(String args[]) throws Exception {
//
//		DatagramSocket serverSocket = new DatagramSocket(9999);
//
//		byte[] receiveData = new byte[1024];
//		byte[] sendData;
//
//		Timer timer = new Timer();
//		timer.scheduleAtFixedRate(new TimerTask() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				try {
//					check();
//				} catch (Exception e) {
//					// TODO: handle exception
//					// System.out.println("Eliminate: " + e.getMessage());
//					e.printStackTrace();
//				}
//
//			}
//		}, 0, 2000);
//
//		Timer timer2 = new Timer();
//		timer.scheduleAtFixedRate(new TimerTask() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				try {
//					if (requests.size() > 0) {
//						System.out.println(requests.poll());
//					}
//				} catch (Exception e) {
//					// TODO: handle exception
//					// System.out.println("Eliminate: " + e.getMessage());
//					e.printStackTrace();
//				}
//
//			}
//		}, 0, 2000);
//
//		while (true) {
//			DatagramPacket receivePacket = new DatagramPacket(receiveData,
//					receiveData.length);
//
//			serverSocket.receive(receivePacket);
//
//			UnifiedPacket p;
//			byte[] data = receivePacket.getData();
//			int length = receivePacket.getLength();
//
//			p = (UnifiedPacket) deserialize(data);
//
//			if (p != null) {
//				Queue<UnifiedPacket> list = packetBag.get(p.id);
//				if (list != null) {
//					list.add(p);
//				} else {
//					Queue<UnifiedPacket> nl = new ConcurrentLinkedQueue<>();
//					nl.offer(p);
//					packetBag.put(p.id, nl);
//				}
//			}
//
//			// if ((byte) packets.size() == p.total) {
//			// System.out.println("all packets received");
//			// }
//
//			// new String(receivePacket.getData(), 0,
//			// receivePacket.getLength());
//
//			// InetAddress IPAddress = receivePacket.getAddress();
//			//
//			// int port = receivePacket.getPort();
//			//
//			// String capitalizedSentence = sentence.toUpperCase();
//			//
//			// sendData = capitalizedSentence.getBytes();
//			//
//			// DatagramPacket sendPacket = new DatagramPacket(sendData,
//			// sendData.length, IPAddress, port);
//			//
//			// serverSocket.send(sendPacket);
//		}
//	}
//}
