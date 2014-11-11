//package x;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.ObjectOutputStream;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.Inet4Address;
//import java.net.InetAddress;
//import java.net.SocketException;
//import java.net.UnknownHostException;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.TimerTask;
//import java.util.Timer;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//import main.Client2Server;
//import main.PacketGenerator;
//import main.UnifiedPacket;
//import spec.Param;
//
//public class UDPClient {
//
//	static PacketGenerator prepare() {
//		List<Param> params = new ArrayList<Param>();
//
//		String string = "";
//
//		for (int i = 0; i < 30; i++) {
//			string += String.valueOf(Math.random());
//		}
//
//		params.add(new Param("java.lang.String", string));
//		params.add(new Param("int", 10));
//
//		Client2Server message = new Client2Server("A", "1", "Some", params);
//		PacketGenerator generator = new PacketGenerator(message);
//		return generator;
//	}
//
//	static byte[] serialize(Object obj) {
//		try {
//			ByteArrayOutputStream out = new ByteArrayOutputStream();
//			ObjectOutputStream os = new ObjectOutputStream(out);
//			os.writeObject(obj);
//			return out.toByteArray();
//		} catch (IOException e) {
//			// TODO: handle exception
//			return null;
//		}
//	}
//
//	static DatagramSocket clientSocket;
//
//	@SuppressWarnings("serial")
//	public static void main(String args[]) {
//
//		// try {
//		// clientSocket = new DatagramSocket();
//		// } catch (SocketException e1) {
//		// // TODO Auto-generated catch block
//		// e1.printStackTrace();
//		// }
//		//
//		// Timer timer = new Timer();
//		// timer.scheduleAtFixedRate(new TimerTask() {
//		//
//		// @Override
//		// public void run() {
//		// // TODO Auto-generated method stub
//		// System.out.println(clientSocket.isBound() + "|"
//		// + clientSocket.isClosed() + "|"
//		// + clientSocket.isConnected());
//		// }
//		// }, 0, 500);
//
//		ThreadPoolExecutor sa = new ThreadPoolExecutor(1, 1, 0,
//				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
//
//		System.out.println(sa.getActiveCount());
//		sa.execute(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				while (true) {
//
//				}
//			}
//		});
//		System.out.println(sa.getActiveCount());
//
//		PacketGenerator generator = prepare();
//
//		byte[] sendData = null;
//
//		byte[] receiveData = new byte[1024];
//		while (generator.hasMoreElements()) {
//			UnifiedPacket client2ServerPacket = (UnifiedPacket) generator
//					.nextElement();
//			sendData = serialize(client2ServerPacket);
//
//			InetAddress ip = null;
//			try {
//				ip = Inet4Address.getByName("localhost");
//			} catch (UnknownHostException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//
//			DatagramPacket sendPacket = new DatagramPacket(sendData,
//					sendData.length, ip, 9999);
//			DatagramPacket sendPacket1 = new DatagramPacket(sendData,
//					sendData.length, ip, 9998);
//			try {
//				Thread.sleep(1000);
//				System.out.println(sendData.length);
//				// clientSocket.send(sendPacket);
//				// clientSocket.send(sendPacket1);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//		System.out.println("Finish sending");
//		for (int i = 1; i <= generator.size(); i++) {
//			System.out.println(generator.get(i).id);
//		}
//
//		ArrayList<Byte> all = new ArrayList<Byte>();
//		List<Byte> cur = new ArrayList<>();
//		for (Byte i = 1; i <= 10; i++) {
//			all.add(i);
//			cur.add(i);
//		}
//
//		all.removeAll(cur);
//		byte[] data = new byte[all.size()];
//		for (int i = 0; i < data.length; i++) {
//			data[i] = all.get(i);
//		}
//
//		System.out.println(new int[0]);
//
//		// for (int i = 0; i < list.size(); i++) {
//		//
//		// }
//
//		// DatagramPacket receivePacket = new DatagramPacket(receiveData,
//		// receiveData.length);
//		//
//		// clientSocket.receive(receivePacket);
//		//
//		// String modifiedSentence = new String(receivePacket.getData(), 0,
//		// receivePacket.getLength());
//		//
//		// System.out.println("FROM SERVER: " + modifiedSentence);
//		//
//		// clientSocket.close();
//	}
//}
