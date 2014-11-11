//package x;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.util.ArrayList;
//
//import main.Client2Server;
//import main.PacketGenerator;
//import main.UnifiedPacket;
//import spec.Param;
//
//public class ClientTest {
//
//	public static String arrToString(byte[] partial) {
//		String string = "";
//		for (int i = 0; i < partial.length; i++) {
//			string += " " + partial[i];
//			if (i % 8 == 0) {
//				string += "\n";
//			}
//		}
//		return string;
//	}
//
//	public static void main(String[] args) {
//
//		ArrayList<Param> params = new ArrayList<Param>();
//
//		String string = "";
//
//		for (int i = 0; i < 5; i++) {
//			string += String.valueOf(Math.random());
//		}
//
//		params.add(new Param("java.lang.String", string));
//		params.add(new Param("int", 10));
//
//		Client2Server message = new Client2Server("A", "1", "Some", params);
//
//		System.out.println("Original: \n" + arrToString(serialize(message)));
//
//		PacketGenerator generator = new PacketGenerator(message);
//
//		byte[] finalData;
//
//		finalData = generator.nextElement().partial;
//
//		while (generator.hasMoreElements()) {
//			UnifiedPacket client2ServerPacket = (UnifiedPacket) generator
//					.nextElement();
//			System.out.println(serialize(client2ServerPacket).length);
//
//			// for (byte b : client2ServerPacket.partial) {
//			//
//			// }
//			finalData = join(finalData, client2ServerPacket.partial);
//		}
//
//		if (finalData == null) {
//			System.out.println("ayayay");
//		} else {
//			System.out.println("Final: \n" + arrToString(finalData));
//		}
//		try {
//			Client2Server me = ((Client2Server) deserialize(finalData));
//
//			System.out.println(me.toString());
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
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
//	static Object deserialize(byte[] data) throws IOException,
//			ClassNotFoundException {
//		ByteArrayInputStream in = new ByteArrayInputStream(data);
//		ObjectInputStream is = new ObjectInputStream(in);
//		return is.readObject();
//	}
//
//}
