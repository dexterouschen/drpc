package main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Takes in any entity, convert to a collection of fixed maximum size
 * UnifiedPacket(s)
 * 
 * @author dexterchen
 *
 */
public class PacketGenerator implements Enumeration<UnifiedPacket> {
	/**
	 * Local cache of all the generated UnifiedPacket(s)
	 */
	Map<Integer, UnifiedPacket> packets = new HashMap<>();
	/**
	 * Current iteration index
	 */
	int current = 1;

	int chunkSize = 20480;

	/**
	 * Constructor
	 * 
	 * @param m
	 *            Client2Server entity
	 */
	public PacketGenerator(Client2Server m) {
		// TODO Auto-generated constructor stub
		/**
		 * Serialize the entity into byte array
		 */
		byte[] data = serialize(m);
		/**
		 * Divide them into fixed size chunks
		 */
		byte[][] chunks = chunkArray(data);
		/**
		 * Generate a packet for each chunk and put them into local cache, see
		 * UnifiedPacket for design details
		 */
		for (int i = 0; i < chunks.length; i++) {
			packets.putIfAbsent(
					i + 1,
					new UnifiedPacket(UUID
							.nameUUIDFromBytes(serialize(m.transactionId)),
							i + 1, chunks.length, chunks[i]));
		}
	}

	/**
	 * Constructor
	 * 
	 * @param m
	 *            Server2ClientResult entity, similar to above
	 */
	public PacketGenerator(Server2ClientResult m) {
		byte[] data = serialize(m);
		byte[][] chunks = chunkArray(data);
		for (int i = 0; i < chunks.length; i++) {
			packets.putIfAbsent(
					i + 1,
					new UnifiedPacket(UUID
							.nameUUIDFromBytes(serialize(m.transactionId)),
							i + 1, chunks.length, chunks[i]));
		}
	}

	/**
	 * Constructor
	 * 
	 * @param data
	 *            byte array, treat the byte array as an Object
	 */
	public PacketGenerator(byte[] data) {
		byte[][] chunks = chunkArray(data);
		for (int i = 0; i < chunks.length; i++) {
			packets.putIfAbsent(i + 1,
					new UnifiedPacket(UUID.nameUUIDFromBytes(data), i + 1,
							chunks.length, chunks[i]));
		}
	}

	/**
	 * Divide an byte array into fixed size chunks
	 * 
	 * @param array
	 *            an byte array
	 * @return
	 */
	byte[][] chunkArray(byte[] array) {
		int numOfChunks = (int) Math.ceil((double) array.length / chunkSize);
		byte[][] output = new byte[numOfChunks][];

		for (int i = 0; i < numOfChunks; ++i) {
			int start = i * chunkSize;
			int length = Math.min(array.length - start, chunkSize);

			byte[] temp = new byte[length];
			System.arraycopy(array, start, temp, 0, length);
			output[i] = temp;
		}

		return output;
	}

	static byte[] serialize(Object obj) {
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
	 * Size of local cache
	 * 
	 * @return size of local cache
	 */
	public int size() {
		return packets.size();
	}

	/**
	 * Get a UnifiedPacket with certain U-index
	 * 
	 * @param index
	 * @return
	 */
	public UnifiedPacket get(int index) {
		return packets.get((Integer) index);
	}

	/**
	 * Override to enable iteration
	 */
	@Override
	public boolean hasMoreElements() {
		// TODO Auto-generated method stub
		if (current < packets.size() + 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Override to enable iteration
	 */
	@Override
	public UnifiedPacket nextElement() {
		// TODO Auto-generated method stub

		return packets.get((Integer) current++ % (packets.size() + 1));
	}
}
