package main;

import java.io.Serializable;

import spec.Param;

/**
 * Server2ClientResult entity, represents a response from server to client,
 * containing the result desired by the client
 * 
 * @author dexterchen
 *
 */
public class Server2ClientResult implements Serializable {

	private static final long serialVersionUID = -9040527098411173235L;

	/**
	 * See TransactionId for details
	 */
	public TransactionId transactionId;
	/**
	 * Result as a Param entity, see Param for details
	 */
	public Param param;

	public Server2ClientResult(TransactionId tr, Param p) {
		transactionId = tr;
		param = p;
	}

	public TransactionId getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(TransactionId transactionId) {
		this.transactionId = transactionId;
	}

	public Param getParam() {
		return param;
	}

	public void setParam(Param param) {
		this.param = param;
	}

	public String toString() {
		return param.toString();

	}

}
