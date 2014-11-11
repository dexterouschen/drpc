package x;

import java.io.Serializable;
import java.util.UUID;

import main.HashCodeUtil;

/**
 * A entity that can be serialized into a maximum fixed size label, can be used
 * to label a chunk of data of a certain entity
 * 
 * @author dexterchen
 *
 */
public class UnifiedLabel implements Serializable {

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
	 * Constructor
	 * 
	 * @param i
	 *            UUID
	 * @param c
	 *            current U-index
	 * @param t
	 *            total U-index
	 */
	public UnifiedLabel(UUID i, int c, int t) {
		id = i;
		current = c;
		total = t;
	}

	@Override
	public String toString() {
		return id.toString() + "|" + current + "|" + total;
	}

	/**
	 * Override to enable uniqueness checking for customized object
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof UnifiedLabel) {
			UnifiedLabel pp = (UnifiedLabel) obj;
			return (pp.id.equals(this.id) && pp.current == this.current && pp.total == this.total);
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
		return result;
	}
}
