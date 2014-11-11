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
	TransactionId transactionId;
	/**
	 * program Id, version, procedure Id, parameter list
	 */
	public String programId;
	public String version;
	public String procId;
	public List<Param> params;

	/**
	 * Constructor
	 * 
	 * @param programId
	 *            program Id
	 * @param version
	 *            version
	 * @param procId
	 *            procedure Id
	 * @param params
	 *            parameter list
	 */
	public Client2Server(String programId, String version, String procId,
			List<Param> params) {
		super();

		this.programId = programId;
		this.version = version;
		this.procId = procId;
		this.params = params;

		// transactionId = timestamp + uid
		this.transactionId = new TransactionId(System.currentTimeMillis(),
				toString());
	}

	public TransactionId getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(TransactionId transactionId) {
		this.transactionId = transactionId;
	}

	public void setTransactionIdUid(String uid) {
		this.transactionId.uid = uid;
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

	/**
	 * Extract TYPELIST from parameter list
	 * 
	 * @return TYPELIST
	 */
	public List<String> getTypeList() {
		List<String> typeList = new ArrayList<>();

		for (Param param : params) {
			typeList.add(param.type);
		}
		return typeList;
	}

	/**
	 * Print info of this object
	 */
	public String toString() {

		return this.programId + this.version + this.procId
				+ getTypeList().toString();
	}

	/**
	 * Make a copy of this object
	 * 
	 * @return a copy
	 */
	public Client2Server cloneObj() {
		Client2Server crts = new Client2Server(programId, version, procId,
				params);
		crts.setTransactionId(transactionId);
		return crts;
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
