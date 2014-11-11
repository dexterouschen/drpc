package main;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * For convenience, all the helper methods
 * 
 * @author dexterchen
 *
 */
public class NetTool {
	/**
	 * If an address can be used for public access
	 * 
	 * @param inetAddress
	 *            an address
	 * @return public or not
	 */
	private static boolean isPublic(InetAddress inetAddress) {
		if (!inetAddress.isAnyLocalAddress()
				&& !inetAddress.isLinkLocalAddress()
				&& !inetAddress.isLoopbackAddress()
				&& !inetAddress.isMCGlobal() && !inetAddress.isMCLinkLocal()
				&& !inetAddress.isMCNodeLocal() && !inetAddress.isMCOrgLocal()
				&& !inetAddress.isMCSiteLocal()
				&& !inetAddress.isMulticastAddress()) {
			return true;
		} else {
			return false;
		}
	}

	// public static String getPublicAddress() {
	// String res = null;
	// try {
	// String localhost = InetAddress.getLocalHost().getHostAddress();
	// Enumeration<NetworkInterface> e = NetworkInterface
	// .getNetworkInterfaces();
	// while (e.hasMoreElements()) {
	// NetworkInterface ni = (NetworkInterface) e.nextElement();
	// if (ni.isLoopback())
	// continue;
	// if (ni.isPointToPoint())
	// continue;
	// Enumeration<InetAddress> addresses = ni.getInetAddresses();
	// while (addresses.hasMoreElements()) {
	// InetAddress address = (InetAddress) addresses.nextElement();
	// if (address instanceof Inet4Address) {
	// String ip = address.getHostAddress();
	// if (!ip.equals(localhost))
	// res = ip;
	// }
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return res;
	// }

	/**
	 * Get the public address of this machine
	 * 
	 * @return public address
	 */
	public static String getPublicAddress() {
		Enumeration<NetworkInterface> e;
		try {
			e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				Enumeration<InetAddress> addrs = e.nextElement()
						.getInetAddresses();
				while (addrs.hasMoreElements()) {
					InetAddress inetAddress = (InetAddress) addrs.nextElement();
					if (isPublic(inetAddress)) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			//System.out.println(e1.getMessage());
		}

		return null;
	}

	/**
	 * Get the address of the current mapper
	 * 
	 * @return
	 */
	public static String getMapperAddress() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(
					"/afs/cs.pitt.edu/usr0/dextercoder/Public/CS2510_P1P/src/dns.txt"));
			String line = reader.readLine();
			reader.close();
			return line;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			//System.out.println(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			//System.out.println(e.getMessage());
		}
		return null;
	}

	/**
	 * Serialize an object into byte array
	 * 
	 * @param obj
	 *            an object
	 * @return byte array
	 */
	public static byte[] serialize(Object obj) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(out);
			os.writeObject(obj);
			return out.toByteArray();
		} catch (IOException e) {
			// TODO: handle exception
			return null;
		}
	}

	/**
	 * Deserialize a byte array into an Object
	 * 
	 * @param data
	 *            byte array
	 * @return an Object
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object deserialize(byte[] data) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return is.readObject();
	}

	/**
	 * Join 2 byte arrays together
	 * 
	 * @param m1
	 * @param m2
	 * @return
	 */
	public static byte[] join(byte[] m1, byte[] m2) {
		byte[] all = new byte[m1.length + m2.length];

		System.arraycopy(m1, 0, all, 0, m1.length);
		System.arraycopy(m2, 0, all, m1.length, m2.length);

		return all;
	}
}
