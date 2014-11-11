package main;

import java.io.Serializable;
import java.util.UUID;

/**
 * A entity that can be serialized into a maximum fixed size label, can be used
 * to label a chunk of data of a certain entity
 * 
 * @author dexterchen
 *
 */
public class UnifiedPacket implements Serializable {

	private static final long serialVersionUID = 9195566068079014534L;
	/**
	 * Fixed size universally unique identifier
	 */
	UUID id;// generated from transactionId
	/**
	 * Fixed size current U-index
	 */
	int current;
	/**
	 * Fixed size total U-index
	 */
	int total;

	/**
	 * Partial data of an entity
	 */
	byte[] partial = new byte[308]; // max size: 308

	/**
	 * Constructor
	 * 
	 * @param i
	 *            UUID
	 * @param c
	 *            current U-index
	 * @param t
	 *            total U-index
	 */
	public UnifiedPacket(UUID i, int c, int t, byte[] p) {
		id = i;
		current = c;
		total = t;
		partial = p;
	}

	public String arrToString() {
		String string = "";
		if (partial != null) {
			for (int i = 0; i < partial.length; i++) {
				string += " " + partial[i];
				if (i % 8 == 0) {
					string += "\n";
				}
			}
		}
		return string;
	}

	@Override
	public String toString() {
		return id.toString() + "|" + current + "|" + total + "\n"
				+ arrToString();
	}

	/**
	 * Override to enable uniqueness checking for customized object
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof UnifiedPacket) {
			UnifiedPacket pp = (UnifiedPacket) obj;
			return (pp.id.equals(this.id) && pp.current == this.current
					&& pp.total == this.total && pp.partial
						.equals(this.partial));
		} else {
			return false;
		}
	}

	/**
	 * Override to enable uniqueness checking for customized object
	 */
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		int result = HashCodeUtil.hash(HashCodeUtil.SEED, id);
		result = HashCodeUtil.hash(result, current);
		result = HashCodeUtil.hash(result, total);
		result = HashCodeUtil.hash(result, partial);
		return result;
	}
}
