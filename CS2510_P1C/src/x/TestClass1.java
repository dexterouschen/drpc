package x;
//package lib;
//
//import java.util.ArrayList;
//
//import main.ClientRequestToMapper;
//import main.ClientRequestToServer;
//import spec.Param;
//
//public class TestClass1 extends ClientRequestHandler {
//
//	public String func(String s1, String s2) {
//
//		ClientRequestToServer clientRequestToServer = new ClientRequestToServer();
//		clientRequestToServer.programId = "TestClass";
//		clientRequestToServer.version = "1";
//		clientRequestToServer.procId = "func";
//
//		ArrayList<Param> paralist = new ArrayList<>();
//		paralist.add(new Param(String.class.getName(), s1));
//		paralist.add(new Param(String.class.getName(), s2));
//		clientRequestToServer.params = paralist;
//
//		clientRequestToServer.transactionId = String.valueOf(System
//				.currentTimeMillis()) + clientRequestToServer.toString();
//
//		ClientRequestToMapper clientRequestToMapper = new ClientRequestToMapper();
//		ArrayList<String> typelist = new ArrayList<>();
//		typelist.add(String.class.getName());
//		typelist.add(String.class.getName());
//
//		clientRequestToMapper.uid = "TestClass1func" + typelist.toString();
//
//		return (String) super.getResult(clientRequestToMapper,
//				clientRequestToServer);
//	}
//
//	public int func2(int i1, float f1) {
//
//		ClientRequestToServer clientRequestToServer = new ClientRequestToServer();
//		clientRequestToServer.programId = "TestClass";
//		clientRequestToServer.version = "1";
//		clientRequestToServer.procId = "func2";
//
//		ArrayList<Param> paralist = new ArrayList<Param>();
//		paralist.add(new Param(int.class.getName(), i1));
//		paralist.add(new Param(float.class.getName(), f1));
//		clientRequestToServer.params = paralist;
//
//		clientRequestToServer.transactionId = String.valueOf(System
//				.currentTimeMillis()) + clientRequestToServer.toString();
//
//		ClientRequestToMapper clientRequestToMapper = new ClientRequestToMapper();
//		ArrayList<String> typelist = new ArrayList<>();
//		typelist.add(int.class.getName());
//		typelist.add(float.class.getName());
//
//		clientRequestToMapper.uid = "TestClass1func2" + typelist.toString();
//
//		return (int) super.getResult(clientRequestToMapper,
//				clientRequestToServer);
//	}
// }
