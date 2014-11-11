package main;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import spec.Param;

public class ServerTaskV2 implements Runnable {

	/**
	 * Collection of UnifiedPacket(s) to reconstruct the actual message
	 */
	Map<Integer, UnifiedPacket> map;
	/**
	 * UUID generated from the actual message's transactionId
	 */
	UUID id;

	/**
	 * HashMap for Class mapping of primitive types
	 */
	@SuppressWarnings("serial")
	Map<String, Class<?>> p2w = new ConcurrentHashMap<String, Class<?>>() {
		{
			put("byte", byte.class);
			put("short", short.class);
			put("int", int.class);
			put("long", long.class);
			put("float", float.class);
			put("double", double.class);
			put("char", char.class);
			put("boolean", boolean.class);
		}
	};

	/**
	 * Constructor
	 * 
	 * @param i
	 *            UUID
	 * @param l
	 *            corresponding collection of UnifiedPacket(s)
	 */
	public ServerTaskV2(UUID i, Map<Integer, UnifiedPacket> m) {
		id = i;
		map = m;
	}

	/**
	 * Actual handling of a request from a client
	 * 
	 * @param transactionId
	 *            transaction Id
	 * @param programId
	 *            program Id
	 * @param version
	 *            version
	 * @param procId
	 *            procedure Id
	 * @param params
	 *            parameter list
	 * @return Server2ClientResult entity
	 */
	Server2ClientResult doTask(TransactionId transactionId, String programId,
			String version, String procId, List<Param> params) {
		try {
			/**
			 * Locate the class of all the libraries lie
			 */
			Class<?> libraryClass = Class
					.forName(ServerRequestHandlerV2.serverClass);
			// ("lib." + programId + version);

			/**
			 * Classes (Types) of parameters
			 */
			Class<?>[] classes = null;
			/**
			 * Values of parameters
			 */
			Object[] values = null;

			if (params != null) {
				/**
				 * Make extra room for the dummy parameter, see Server for
				 * details
				 */
				classes = new Class[params.size() + 1];
				/**
				 * Make extra room for the dummy parameter, see Server for
				 * details
				 */
				values = new Object[params.size() + 1];
				/**
				 * Construct the Classes and Values
				 */
				for (int i = 0; i < params.size(); i++) {
					Param p = params.get(i);
					/**
					 * For arrays
					 */
					if (p.type.contains("[]")) {
						/**
						 * Extract the component type (e.g., double in
						 * double[][])
						 */
						String componentTypeString = p.type.replace("[]", "");
						/**
						 * Count the dimension of array
						 */
						Matcher m = Pattern.compile("\\[\\]").matcher(p.type);
						int count = 0;
						while (m.find()) {
							count += 1;
						}
						/**
						 * Try to fetch the class of primitive type
						 */
						Class<?> pTypeClass = p2w.get(componentTypeString);

						/**
						 * If it's not a primitive type, then it's a customized
						 * object
						 */
						if (pTypeClass == null) {
							try {
								/**
								 * Try to load it from server library
								 */
								pTypeClass = Class.forName(componentTypeString);
							} catch (Exception e) {
								System.out.println(e.getMessage());
							}
						}
						/**
						 * Use Java Reflection to get the Class of the array
						 * indicated by information above
						 */
						classes[i] = Array.newInstance(pTypeClass,
								new int[count]).getClass();
						/**
						 * Append the value
						 */
						values[i] = classes[i].cast(p.value);

					} else {
						/**
						 * Try to fetch the class of primitive type
						 */
						Class<?> pTypeClass = p2w.get(p.type);
						/**
						 * If it's not a primitive type, then it's a customized
						 * object
						 */
						if (pTypeClass == null) {
							try {
								/**
								 * Try to load it from server library
								 */
								pTypeClass = Class.forName(p.type);
							} catch (Exception e) {
								System.out.println(e.getMessage());
							}
						}
						/**
						 * Use Java Reflection to get the Class of the array
						 * indicated by information above
						 */
						classes[i] = pTypeClass;
						/**
						 * Append the value
						 */
						values[i] = pTypeClass.cast(p.value);

					}
				}
			}

			/**
			 * Java Reflection: Method container
			 */
			Method targetMethod;
			/**
			 * Return value of the method
			 */
			Object retValue;
			/**
			 * program Id+version at the client side is the name of a class, but
			 * at server side, in order to cut down the workload of developer of
			 * server side, we put all procedures in one Server class, so we
			 * will map this programId+version to dummy label which is used in
			 * Server class
			 */
			Class<?> actualLibraryClass = Class.forName("lib." + programId
					+ version);

			if (classes != null && values != null) {
				/**
				 * Set the dummy parameter class
				 */
				classes[classes.length - 1] = actualLibraryClass;
				/**
				 * Set the dummy parameter value, we don't use this
				 */
				values[values.length - 1] = null;
				/**
				 * Get the method entity can load it into method container
				 */

				targetMethod = libraryClass.getMethod(procId, classes);

				/**
				 * Invoke upon this method with parameter values
				 */
				retValue = targetMethod.invoke(libraryClass.newInstance(),
						values);

				/**
				 * Construct a Server2ClientResult entity and return
				 */
				return new Server2ClientResult(transactionId, new Param(
						targetMethod.getReturnType().getCanonicalName(),
						retValue));
			}

		} catch (ClassNotFoundException | NoSuchMethodException
				| SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| InstantiationException e) {
			System.out.println(e.getMessage());
		}

		return null;
	}

	/**
	 * One server thread
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		/**
		 * Get the total number of packets
		 */
		UnifiedPacket p = map.get(1);
		int total = p.total;
		// int packetNum = list.size();

		byte[] finalData = null;
		/**
		 * Fetch the packet with 1 as U-index
		 */
		UnifiedPacket first = map.get(1);// NetTool.find(1, list);
		/**
		 * If not null, then keep fetching other packets
		 */
		if (first != null) {
			finalData = first.partial;

			for (int i = 2; i <= total; i++) {
				UnifiedPacket packet = map.get(i);// NetTool.find(i, list);
				if (packet != null) {
					/**
					 * Join all partial data in each packet together
					 */
					finalData = NetTool.join(finalData, packet.partial);
				} else {
					/**
					 * If a packet with certain U-index is lost here, then the
					 * packet list is somehow invalid, reset everything
					 */
					finalData = null;
					ServerRequestHandlerV2.packetReceived.put(p.id,
							new ConcurrentHashMap<Integer, UnifiedPacket>());
					break;
				}

			}
		}
		try {
			if (finalData != null && finalData.length > 0) {
				/**
				 * Try to convert the received data into Client2Server message
				 */
				Client2Server cr = (Client2Server) NetTool
						.deserialize(finalData);

				/**
				 * Result to return
				 */
				Server2ClientResult result = null;
				/**
				 * Cached result
				 */
				Server2ClientResult cachedResult = null;
				// /**
				// * Used for old implementation of proc0, isProc0 is always
				// false
				// * in new implementation, but for possible future use, we just
				// * leave these code here
				// */
				// boolean isProc0 = cr.procId.equals("proc0");
				// // avoid unnecessary lookup
				// if (!isProc0) {
				// /**
				// * Get the cached result based on transaction Id if any
				// */
				cachedResult = ServerRequestHandlerV2.cache
						.get(cr.transactionId);
				// }

				if (cr != null && cachedResult == null) {// && !isProc0) {
					/**
					 * If no cache, then it's a new request
					 */
					System.out.println("Receive: " + cr);
					/**
					 * Handle this task
					 */
					result = doTask(cr.transactionId, cr.programId, cr.version,
							cr.procId, cr.params);
				}

				if (result != null) {
					// if (!isProc0) {
					/**
					 * Cache this result
					 */
					ServerRequestHandlerV2.cache.putIfAbsent(cr.transactionId,
							result);
					System.out.println("New cache: " + cr.transactionId
							+ ", Cache size: "
							+ ServerRequestHandlerV2.cache.size());
					// }
				} else if (cachedResult != null) {// if (!isProc0) {
					/**
					 * If result is not updated by doTask, then there is a
					 * cached result
					 */
					System.out.println("Cache result found for "
							+ cr.transactionId + " at "
							+ System.currentTimeMillis());
					result = cachedResult;
				}
				// else {
				// result = new Server2ClientResult(cr.transactionId,
				// new Param("int", 0));
				// }

				/**
				 * Locate the destination and send back the result (no matter
				 * cached or not), result is processed by PacketGenerator into
				 * maximum fixed size packets, and get sent one by one to
				 * destination client
				 */
				Address desAddr = ServerRequestHandlerV2.destination.get(p.id);
				PacketGenerator generator = new PacketGenerator(result);
				while (generator.hasMoreElements()) {
					UnifiedPacket uPacket = (UnifiedPacket) generator
							.nextElement();
					byte[] sendData = NetTool.serialize(uPacket);

					DatagramPacket packet = new DatagramPacket(sendData,
							sendData.length, InetAddress.getByName(desAddr.ip),
							desAddr.port);
					ServerRequestHandlerV2.serverSocket.send(packet);
					Thread.sleep(2);
				}

				// ServerRequestHandlerV2.packetReceived.remove(id);
				ServerRequestHandlerV2.completed.put(id, "1");
				ServerRequestHandlerV2.destination.remove(id);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
