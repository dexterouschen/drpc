package main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import spec.Param;

/**
 * Request handler of the client, responsible for handling the sending and
 * receiving
 * 
 * @author dexterchen
 *
 */
public class ClientRequestHandlerV3 {

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
	 * Request a server address
	 * 
	 * @param crtm
	 *            Client2Mapper entity
	 * @return Address entity
	 */
	Address getServerAddress(Client2Mapper crtm) {
		/**
		 * TCP socket to mapper
		 */
		Socket socket2Mapper; // , socket2Server0;
		try {
			/**
			 * Fetch mapper address from public DNS
			 */
			String[] parts = NetTool.getMapperAddress().split(":");
			/**
			 * Initiate TCP connection to mapper
			 */
			socket2Mapper = new Socket(parts[0], Integer.valueOf(parts[1]));
			ObjectOutputStream oosm = new ObjectOutputStream(
					socket2Mapper.getOutputStream());
			/**
			 * Write request
			 */
			oosm.writeObject(crtm);
			oosm.flush();
			ObjectInputStream oism = new ObjectInputStream(
					socket2Mapper.getInputStream());
			/**
			 * Block until an Address entity is received
			 */
			Address serverAddress = (Address) oism.readObject();
			// if (serverAddress != null) {
			// System.out.println("Server address: " + serverAddress);
			// }
			oism.close();
			oosm.close();
			socket2Mapper.close();
			return serverAddress;
		} catch (Exception e) {
			return null;
		}
	}

	public Object getResult(Client2Mapper crtm, Client2Server crts) {
		/**
		 * For reception, Queue of UnifiedPacket(s)
		 */
		Queue<UnifiedPacket> packetReceived = new LinkedList<UnifiedPacket>();
		/**
		 * returned result, checked frequently by thread
		 */
		Param result = null;
		/**
		 * UDP socket to server
		 */
		DatagramSocket socket2Server = null;
		/**
		 * All the packets that are still needed by the server (those aren't
		 * received)
		 */
		int[] neededPackets = null;
		/**
		 * Server address
		 */
		Address serverAddress = null;

		try {
			/**
			 * UDP packet for procedure 0
			 */
			DatagramPacket p0r = null;
			/**
			 * Procedure 0 entity
			 */
			P0 p0message = null;

			do {

				try {
					/**
					 * Get a suitable server address
					 */
					serverAddress = getServerAddress(crtm);
					/**
					 * Initiate local UDP socket
					 */
					socket2Server = new DatagramSocket();
				} catch (Exception e) {
					Thread.sleep(3);
				}

				if (serverAddress != null) {
					P0 p0 = new P0("proc0");
					/**
					 * Serialize message
					 */
					byte[] sendP0 = NetTool.serialize(p0);
					/**
					 * Put the serialized byte array in a UDP packet
					 */
					DatagramPacket p0p = new DatagramPacket(sendP0,
							sendP0.length,
							InetAddress.getByName(serverAddress.ip),
							serverAddress.port);
					/**
					 * UDP buffer
					 */
					byte[] p0rData = new byte[1024];
					/**
					 * Packet for receiving
					 */
					p0r = new DatagramPacket(p0rData, p0rData.length);
					/**
					 * Temporally set the socket timeout lower to get quicker
					 * response
					 */
					if (socket2Server != null) {
						socket2Server.setSoTimeout(3000);
						socket2Server.send(p0p);
						try {
							/**
							 * Block until a acknowledgement is received
							 */
							socket2Server.receive(p0r);
						} catch (SocketTimeoutException t) {
							// serverAddress = getServerAddress(crtm);
						}
					}
					try {
						/**
						 * Try to deserialize received data into entity
						 */
						p0message = (P0) NetTool.deserialize(p0r.getData());
					} catch (Exception e) {
						p0message = null;
					}
				}

			} while (p0message == null);

			/**
			 * Reverset the timeout restriction
			 */
			socket2Server.setSoTimeout(0);

			if (p0message != null && p0message.message.equals("SUCCESS")) {
				// System.out.println("P0 succeed");
				/**
				 * Set IP and port into transaction
				 */
				crts.transactionId.setExtra(NetTool.getPublicAddress()
						+ socket2Server.getLocalPort());
				/**
				 * Divide the entity into UnifiedPacket(s), see PacketGenerator
				 * for details
				 */
				PacketGenerator generator = new PacketGenerator(crts);
				/**
				 * Keep going until a result is returned
				 */
				while (result == null) {
					/**
					 * Keep "poping" UnifedPacket and send to designated
					 * destination
					 */
					while (generator.hasMoreElements()) {
						UnifiedPacket uPacket = generator.nextElement();
						/**
						 * Serialize a UnifiedPacket
						 */
						byte[] sendData = NetTool.serialize(uPacket);
						/**
						 * Put it in a UDP packet
						 */
						DatagramPacket sendPacket = new DatagramPacket(
								sendData, sendData.length,
								InetAddress.getByName(serverAddress.ip),
								serverAddress.port);
						/**
						 * Send it over to designated server
						 */
						socket2Server.send(sendPacket);
						Thread.sleep(2);
					}
					/**
					 * Buffer for receiving
					 */
					byte[] receiveData = new byte[40960];
					/**
					 * UDP packet for receiving
					 */
					DatagramPacket receivePacket = new DatagramPacket(
							receiveData, receiveData.length);
					try {
						/**
						 * Block until a UDP packet is received
						 */
						socket2Server.setSoTimeout(15000);
						socket2Server.receive(receivePacket);

						/**
						 * Deserialize it into an entity
						 */
						Object message = NetTool.deserialize(receivePacket
								.getData());
						/**
						 * If it's a UnifiedPacket
						 */

						if (message instanceof UnifiedPacket) {
							// System.out.println("Receive a UnifiedPacket"
							// + System.currentTimeMillis());

							UnifiedPacket p = (UnifiedPacket) message;
							// loc.add(20);
							if (p != null) {
								if (packetReceived == null) {
									packetReceived = new ConcurrentLinkedQueue<UnifiedPacket>();
								}
								/**
								 * Add it to the local collection
								 */
								if (!packetReceived.contains(p))
									packetReceived.add(p);

								// loc.add(21);
								/**
								 * If all the packets are received, See
								 * UnifiedPacket for details
								 */
								if ((int) p.total == packetReceived.size()) {
									// System.out.println("Receive all UPacket"
									// + System.currentTimeMillis());
									int total = p.total;
									byte[] finalData = null;
									// loc.add(22);
									/**
									 * Find the UnifiedPacket at U-index 1
									 */
									UnifiedPacket first = NetTool.find(1,
											packetReceived);
									if (first != null) {
										/**
										 * Merge data
										 */
										finalData = first.partial;
										/**
										 * Keep merging data
										 */
										for (int i = 2; i <= total; i++) {
											UnifiedPacket packet = NetTool
													.find(i, packetReceived);
											if (packet != null) {
												finalData = NetTool.join(
														finalData,
														packet.partial);
											} else {
												/**
												 * If a certain packet cannot be
												 * found, discard all
												 * UnifiedPacket(s)
												 */
												finalData = null;
												packetReceived = new ConcurrentLinkedQueue<UnifiedPacket>();
												generator.current = 1;
												break;
											}

										}
									}
									try {
										if (finalData != null
												&& finalData.length > 0) {
											/**
											 * Try to deserialize the data into
											 * an entity
											 */
											Server2ClientResult re = (Server2ClientResult) NetTool
													.deserialize(finalData);
											/**
											 * Set the result
											 */
											result = re.param;
										}
									} catch (Exception e) {
										// TODO: handle exception
									}

								}
							}
						} else if (message instanceof Server2ClientNeed) {
							/**
							 * If it's a Server2ClientNeed, then merge to
							 * neededPackets
							 */
							neededPackets = ((Server2ClientNeed) message).needed;
						}

					} catch (SocketTimeoutException e1) {
						return getResult(crtm, crts);
					} catch (IOException | ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (neededPackets != null) {
						/**
						 * Go over neededPackets to send again all lost packets
						 */
						for (int i = 0; i < neededPackets.length; i++) {
							UnifiedPacket packet = generator
									.get(neededPackets[i]);
							byte[] sendData = NetTool.serialize(packet);
							DatagramPacket sendPacket = new DatagramPacket(
									sendData, sendData.length,
									InetAddress.getByName(serverAddress.ip),
									serverAddress.port);
							socket2Server.send(sendPacket);
							Thread.sleep(2);
						}
					}

				}
				socket2Server.close();
				return result.value;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		if (p2w.get(result.type) != null)
			return Double.POSITIVE_INFINITY;
		else {
			return null;
		}
	}
}
