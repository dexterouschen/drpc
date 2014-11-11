//package x;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.lang.reflect.Array;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.net.Socket;
//import java.net.SocketException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import main.Client2Server;
//import main.Server2ClientResult;
//import main.TransactionId;
//import spec.Param;
//
//public class ServerTask implements Runnable {
//
//	Socket socket;
//
//	@SuppressWarnings("serial")
//	Map<String, Class<?>> p2w = new ConcurrentHashMap<String, Class<?>>() {
//		{
//			put("byte", byte.class);
//			put("short", short.class);
//			put("int", int.class);
//			put("long", long.class);
//			put("float", float.class);
//			put("double", double.class);
//			put("char", char.class);
//			put("boolean", boolean.class);
//		}
//	};
//
//	public ServerTask(Socket s) {
//		// TODO Auto-generated constructor stub
//		socket = s;
//		ServerRequestHandler.taskCount++;
//	}
//
//	Server2ClientResult doTask(TransactionId transactionId, String programId,
//			String version, String procId, List<Param> params) {
//		try {
//			Class<?> libraryClass = Class
//					.forName(ServerRequestHandler.serverClass);
//			// ("lib." + programId + version);
//
//			Class<?>[] classes = null;
//			Object[] values = null;
//
//			if (params != null) {
//
//				classes = new Class[params.size() + 1];
//
//				values = new Object[params.size() + 1];
//
//				for (int i = 0; i < params.size(); i++) {
//					Param p = params.get(i);
//
//					if (p.type.contains("[]")) {
//
//						String componentTypeString = p.type.replace("[]", "");
//
//						Matcher m = Pattern.compile("\\[\\]").matcher(p.type);
//						int count = 0;
//						while (m.find()) {
//							count += 1;
//						}
//
//						Class<?> pTypeClass = p2w.get(componentTypeString);
//
//						if (pTypeClass == null) {
//							try {
//								pTypeClass = Class.forName(componentTypeString);
//							} catch (Exception e) {
//								System.out.println(e.getMessage());
//							}
//						}
//
//						classes[i] = Array.newInstance(pTypeClass,
//								new int[count]).getClass();
//						values[i] = classes[i].cast(p.value);
//
//					} else {
//
//						Class<?> pTypeClass = p2w.get(p.type);
//
//						if (pTypeClass == null) {
//							try {
//								pTypeClass = Class.forName(p.type);
//							} catch (Exception e) {
//								System.out.println(e.getMessage());
//							}
//						}
//
//						classes[i] = pTypeClass;
//						values[i] = pTypeClass.cast(p.value);
//
//					}
//				}
//			}
//
//			Method targetMethod;
//			Object retValue;
//
//			Class<?> actualLibraryClass = Class.forName("lib." + programId
//					+ version);
//
//			if (classes != null && values != null) {
//
//				classes[classes.length - 1] = actualLibraryClass;
//				values[values.length - 1] = null;
//
//				targetMethod = libraryClass.getMethod(procId, classes);
//				retValue = targetMethod.invoke(libraryClass.newInstance(),
//						values);
//
//				return new Server2ClientResult(transactionId, new Param(
//						targetMethod.getReturnType().getCanonicalName(),
//						retValue));
//			}
//
//		} catch (ClassNotFoundException | NoSuchMethodException
//				| SecurityException | IllegalAccessException
//				| IllegalArgumentException | InvocationTargetException
//				| InstantiationException e) {
//			System.out.println(e.getMessage());
//		}
//
//		return null;
//	}
//
//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//
//		try {
//
//			InputStream is = socket.getInputStream();
//			ObjectInputStream ois = new ObjectInputStream(is);
//
//			Server2ClientResult result = null;
//			Client2Server cr = (Client2Server) ois.readObject();
//
//			Server2ClientResult cachedResult = null;
//
//			boolean isProc0 = cr.procId.equals("proc0");
//			// avoid unnecessary lookup
//			if (!isProc0) {
//				cachedResult = ServerRequestHandler.cache.get(cr.transactionId);
//			}
//
//			if (cr != null && cachedResult == null && !isProc0) {
//				System.out.println("Receive: " + cr);
//				result = doTask(cr.transactionId, cr.programId, cr.version,
//						cr.procId, cr.params);
//			}
//
//			if (result != null) {
//				if (!isProc0) {
//					ServerRequestHandler.cache.putIfAbsent(cr.transactionId,
//							result);
//					System.out.println("New cache: " + cr.transactionId
//							+ ", Cache size: "
//							+ ServerRequestHandler.cache.size());
//				}
//			} else if (!isProc0) {
//				System.out.println("Cache result found for " + cr.transactionId
//						+ " at " + System.currentTimeMillis());
//				result = cachedResult;
//			} else {
//				result = new Server2ClientResult(cr.transactionId, new Param(
//						"int", 0));
//			}
//
//			ObjectOutputStream oos = new ObjectOutputStream(
//					socket.getOutputStream());
//
//			oos.writeObject(result);
//
//			oos.flush();
//			oos.close();
//			ois.close();
//			socket.close();
//
//			ServerRequestHandler.sec2lastOutTimestamp = ServerRequestHandler.lastOutTimestamp;
//			ServerRequestHandler.lastOutTimestamp = System.currentTimeMillis();
//
//			ServerRequestHandler.taskCount--;
//			// ServerRequestHandler.refresh();
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			ServerRequestHandler.taskCount--;
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			ServerRequestHandler.taskCount--;
//		}
//	}
//
//}
