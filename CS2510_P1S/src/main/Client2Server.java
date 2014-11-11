package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import spec.Param;

/**
 * Client2Server entity, represents a request from client to server
 * 
 * @author dexterchen
 *
 */
public class Client2Server implements Serializable {

	private static final long serialVersionUID = 5280874168654332016L;

	/**
	 * See TransactionId for details
	 */
	public TransactionId transactionId;
	/**
	 * program Id, version, procedure Id, parameter list
	 */
	public String programId;
	public String version;
	public String procId;
	public List<Param> params;

	public TransactionId getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(TransactionId transactionId) {
		this.transactionId = transactionId;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getProcId() {
		return procId;
	}

	public void setProcId(String procId) {
		this.procId = procId;
	}

	public List<Param> getParams() {
		return params;
	}

	public void setParams(ArrayList<Param> params) {
		this.params = params;
	}

	public String toString() {
		return this.transactionId + ":\n\t" + this.programId + this.version
				+ " -> " + this.procId + this.params + "\n";
	}

	/**
	 * Override to enable uniqueness checking for customized object
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof Client2Server) {
			TransactionId pp = ((Client2Server) obj).transactionId;
			return (pp.uid.equals(this.transactionId.uid) && pp.timestamp == this.transactionId.timestamp);
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
		int result = HashCodeUtil.hash(HashCodeUtil.SEED,
				this.transactionId.uid);
		result = HashCodeUtil.hash(result, this.transactionId.timestamp);
		return result;
	}
}
