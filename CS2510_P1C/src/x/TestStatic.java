package x;

import java.io.IOException;
import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import main.NetTool;

class DummyObj implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2363827320136706830L;
	String aString;
	int bi;
	int ci;

	public DummyObj(String a, int b, int c) {
		// TODO Auto-generated constructor stub
		aString = a;
		bi = b;
		ci = c;
	}

	public String toString() {
		return aString + "|" + bi + "|" + ci;
	}
}

public class TestStatic {
	static int a = 0;

	public static String getRandomString() {
		String string = "";

		for (int i = 0; i < 10; i++) {
			string += ((int) Math.random() * 10);
		}

		return string.substring(0, 10);
	}

	public static void main(String[] args) {

		UnifiedLabel[] labels = new UnifiedLabel[5];

		byte[] data = NetTool.serialize(new DummyObj("21231234", 545, 4677));

		for (int i = 0; i < labels.length; i++) {
			labels[i] = new UnifiedLabel(
					UUID.nameUUIDFromBytes(getRandomString().getBytes()), i,
					labels.length);

			System.out.println(NetTool.serialize(labels[i]).length);
		}

		if (labels[0] == null) {
			System.out.println(1);
		}

		if (data == null) {
			System.out.println(2);
		}

		byte[] merged = NetTool.join(NetTool.serialize(labels[0]), data);

		System.out.println(merged.length);

		byte[] realdata = new byte[91];

		System.arraycopy(merged, 161, realdata, 0, 91);

		try {
			Object originalObject = NetTool.deserialize(realdata);

			System.out.println(originalObject);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// packets[1] = new
		// UnifiedLabel(UUID.nameUUIDFromBytes("adfjidfjaiod".getBytes()), 6, 7,
		// null);
		//
		// for (int i = 0; i < packets.length; i++) {
		// if (packets[i]!=null) {
		// System.out.println(packets[i]);
		// }else {
		// System.out.println("NULL");
		// }
		//
		// }

		// Timer timer = new Timer();
		// timer.scheduleAtFixedRate(new TimerTask() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// a++;
		// System.out.println(a);
		// }
		// }, 0, 500);
	}
}
