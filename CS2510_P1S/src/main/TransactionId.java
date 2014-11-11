package main;

import java.io.Serializable;

/**
 * TransactionId entity, represent a transaction Id, which can uniquely identify
 * a transaction between client to server
 * 
 * @author dexterchen
 *
 */
public class TransactionId implements Serializable {

	private static final long serialVersionUID = -4448133330877048229L;
	/**
	 * Time stamp of this transaction
	 */
	long timestamp;
	/**
	 * uid (program Id+version+procedure Id+TYPELIST)
	 */
	String uid;
	/**
	 * Extra field to enforce uniqueness, which is IP and port in our
	 * implementation
	 */
	String extra = "";

	/**
	 * Constructor
	 * 
	 * @param t
	 *            time stamp
	 * @param u
	 *            uid
	 */
	public TransactionId(long t, String u) {
		uid = u;
		timestamp = t;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	/**
	 * Override to enable uniqueness checking for customized object
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof TransactionId) {
			TransactionId pp = (TransactionId) obj;
			return (pp.uid.equals(this.uid) && pp.timestamp == this.timestamp);
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
		int result = HashCodeUtil.hash(HashCodeUtil.SEED, timestamp);
		result = HashCodeUtil.hash(result, uid);
		result = HashCodeUtil.hash(result, extra);
		return result;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.valueOf(timestamp) + uid + extra;
	}
}
