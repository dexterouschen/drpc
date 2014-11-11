package x;

import java.util.concurrent.Callable;

import main.Server2Mapper;

public class PMRegisterThread implements Callable<Boolean> {

	Server2Mapper request;

	public PMRegisterThread(Server2Mapper r) {
		request = r;
	}

	public Boolean register() {

//		PortMapper.addr2ts.putIfAbsent(request.address,
//				System.currentTimeMillis());
//		PortMapper.addr2uids.putAll(request.addr2uids);
//		PortMapper.uid2serverlist.putAll(request.uid2serverlist);

		// String key = request.programId + request.version + request.procId
		// + request.types.toString();
		//
		// ArrayList<Address> al = new ArrayList<Address>();
		// al.add(new Address(request.ip, request.port));
		// PortMapper.hashMap.putIfAbsent(key, new ServerList(0, al));
		//
		// PortMapper.hashMap.get(request.programId + request.version
		// + request.procId + request.types.toString()).list
		// .add(new Address(request.ip, request.port));

		return true;
	}

	@Override
	public Boolean call() {
		return register();
	}

}