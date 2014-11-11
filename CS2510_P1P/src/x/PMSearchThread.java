package x;
//package main;
//
//import java.util.concurrent.Callable;
//
//public class PMSearchThread implements Callable<Address> {
//
//	Client2Mapper request;
//
//	public PMSearchThread(Client2Mapper r) {
//		request = r;
//	}
//
//	public Address search() {
//		// System.out.println("1");
//		ServerList sl = PortMapper.uid2serverlist.get(request.uid);
//		// System.out.println("1");
//		if (sl != null)
//			return sl.nextService();
//		else
//			return null;
//	}
//
//	@Override
//	public Address call() throws Exception {
//		// TODO Auto-generated method stub
//		return search();
//	}
//
//}
