package x;
//package main;
//
//import java.lang.reflect.Array;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.concurrent.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import spec.Param;
//
//public class Task implements Callable<Server2Client> {
//
//	String transactionId;
//	String programId;
//	String version;
//	String procId;
//	ArrayList<Param> params;
//
//	public Task(String tr, String pg, String v, String pc, ArrayList<Param> pms) {
//		transactionId = tr;
//		programId = pg;
//		version = v;
//		procId = pc;
//		params = pms;
//	}
//
//	@SuppressWarnings("serial")
//	ConcurrentHashMap<String, Class<?>> p2w = new ConcurrentHashMap<String, Class<?>>() {
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
//	@Override
//	public Server2Client call() throws Exception {
//		// TODO Auto-generated method stub
//
//		try {
//			Class<?> aClass = Class.forName(ServerRequestHandler.serverClass);
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
//					// boolean isPrim = true;
//					// Class<?> aClass2 = null;
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
//						// else {
//						// try {
//						// aClass2 = Class.forName(p.type);
//						// } catch (Exception e) {
//						// System.out.println(e.getMessage());
//						// }
//						// classes[i] = aClass2;
//						// values[i] = p.value;
//						// }
//
//						// switch (p.type) {
//						// case "byte":
//						// classes[i] = byte.class;
//						// values[i] = Byte.valueOf(p.value.toString())
//						// .byteValue();
//						// break;
//						// case "int":
//						// classes[i] = int.class;
//						// values[i] = Integer.valueOf(p.value.toString())
//						// .intValue();
//						// break;
//						// case "char":
//						// classes[i] = char.class;
//						// values[i] = Character.valueOf(p.value.toString()
//						// .charAt(0));
//						// break;
//						// case "double":
//						// classes[i] = double.class;
//						// values[i] = Double.valueOf(p.value.toString())
//						// .doubleValue();
//						// break;
//						// case "float":
//						// classes[i] = float.class;
//						// values[i] = Float.valueOf(p.value.toString())
//						// .floatValue();
//						// break;
//						// case "boolean":
//						// classes[i] = boolean.class;
//						// values[i] = Boolean.valueOf(p.value.toString())
//						// .booleanValue();
//						// break;
//						// case "long":
//						// classes[i] = long.class;
//						// values[i] = Long.valueOf(p.value.toString())
//						// .longValue();
//						// break;
//						// case "short":
//						// classes[i] = short.class;
//						// values[i] = Short.valueOf(p.value.toString())
//						// .shortValue();
//						// break;
//						// default:
//						// try {
//						// aClass2 = Class.forName(p.type);
//						// } catch (Exception e) {
//						// System.out.println(e.getMessage());
//						// }
//						// classes[i] = aClass2;
//						// values[i] = p.value;
//						// break;
//						// }
//					}
//				}
//			}
//
//			Method aMethod;
//			Object retValue;
//
//			// Array.newInstance(componentType, length)
//
//			Class<?> aClass3 = Class.forName("lib." + programId + version);
//
//			if (classes != null && values != null) {
//
//				classes[classes.length - 1] = aClass3;
//				values[values.length - 1] = null;
//
//				aMethod = aClass.getMethod(procId, classes);
//				retValue = aMethod.invoke(aClass.newInstance(), values);
//
//				return new Server2Client(transactionId, new Param(
//						aMethod.getReturnType().getCanonicalName(), retValue));
//			}
//			// else {
//			// aMethod = aClass.getMethod(procId);
//			// retValue = aMethod.invoke(aClass.newInstance());
//			// return new ServerResponseToClient(transactionId, new Param(
//			// aMethod.getReturnType().getCanonicalName(), retValue));
//			// }
//
//		} catch (ClassNotFoundException | NoSuchMethodException
//				| SecurityException | IllegalAccessException
//				| IllegalArgumentException | InvocationTargetException e) {
//			System.out.println(e.getMessage());
//		}
//
//		return null;
//	}
// }
