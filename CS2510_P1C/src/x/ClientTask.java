//package x;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.DatagramPacket;
//import java.net.InetAddress;
//import java.net.Socket;
//import java.util.UUID;
//import java.util.concurrent.Callable;
//
//import main.Address;
//import main.Client2Mapper;
//import main.Client2Server;
//import main.NetTool;
//import main.PacketGenerator;
//import main.UnifiedPacket;
//import spec.Param;
//
//public class ClientTask implements Callable<Param> {
//
//	/**
//	 * socket to PortMapper
//	 */
//	Socket socketToMapper;
//	/**
//	 * OOS to PortMapper
//	 */
//	ObjectOutputStream out2Mapper;
//	/**
//	 * OIS from PortMapper
//	 */
//	ObjectInputStream inFromMapper;
//
//	Client2Mapper crtm;
//	Client2Server crts;
//	Address serverAddress;
//	PacketGenerator generator;
//
//	long getServerWaitTime = 1000;
//	long getMapperWaitTime = 1000;
//
//	long waitTime = 3000;
//
//	public ClientTask(Client2Mapper m, Client2Server s) {
//		// System.out.println("1");
//		crtm = m;
//		crts = s;
//		reset();
//
//		// System.out.println("2");
//	}
//
//	void reset() {
//		// try {
//		// ClientRequestHandlerV2.out2Mapper.close();
//		// } catch (Exception e) {
//		// }
//		// try {
//		// ClientRequestHandlerV2.inFromMapper.close();
//		// } catch (Exception e) {
//		// }
//		// try {
//		// ClientRequestHandlerV2.socketToMapper.close();
//		// } catch (Exception e) {
//		// }
//		// ClientRequestHandlerV2.socketToServer.close();
//		try {
//			getOutputStreamToMapper();
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		try {
//			getServerAddress();
//			crts.transactionId.setExtra(NetTool.getPublicAddress()
//					+ ClientRequestHandlerV2.socketToServer.getLocalPort());
//			generator = new PacketGenerator(crts);
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		// try {
//		// initUDP();
//		// } catch (InterruptedException e) {
//		// }
//	}
//
//	void getOutputStreamToMapper() throws InterruptedException {
//		int attempts = 0;
//		try {
//			String[] parts = NetTool.getMapperAddress().split(":");
//			socketToMapper = new Socket(parts[0], Integer.valueOf(parts[1]));
//			out2Mapper = new ObjectOutputStream(
//					socketToMapper.getOutputStream());
//
//		} catch (Exception e) {
//			// TODO: handle exception
//			if (attempts < 3) {
//				Thread.sleep(getMapperWaitTime);
//				getMapperWaitTime *= 2;
//				++attempts;
//				getOutputStreamToMapper();
//			} else {
//				System.out.println("Can't connect to mapper, shutdown");
//				System.exit(0);
//			}
//		}
//	}
//
//	void getServerAddress() throws InterruptedException {
//
//		int attempts = 0;
//		// getOutputStreamToMapper();
//		try {
//			out2Mapper.writeObject(crtm);
//			out2Mapper.flush();
//			inFromMapper = new ObjectInputStream(
//					socketToMapper.getInputStream());
//			serverAddress = (Address) inFromMapper.readObject();
//			if (serverAddress == null) {
//				throw new IOException();
//			}
//
//		} catch (IOException | ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			if (attempts < 3) {
//				Thread.sleep(getServerWaitTime);
//				getServerWaitTime *= 2;
//				++attempts;
//				// getOutputStreamToMapper();
//				getServerAddress();
//			}
//		}
//	}
//
//	// void initUDP() throws InterruptedException {
//	// long waitTime = 1000;
//	// int attempts = 0;
//	// try {
//	// ClientRequestHandlerV2.socketToServer = new DatagramSocket();
//	// } catch (SocketException e) {
//	// // TODO Auto-generated catch block
//	// if (attempts < 3) {
//	// Thread.sleep(waitTime);
//	// waitTime *= 2;
//	// ++attempts;
//	// getOutputStreamToMapper();
//	// getServerAddress();
//	// }
//	// }
//	// }
//
//	@Override
//	public Param call() throws Exception {
//		// TODO Auto-generated method stub
//		long startTime = System.currentTimeMillis();
//		// System.out.println("5");
//		while (ClientRequestHandlerV2.results.get(crts.transactionId) == null) {
//
//			while (generator.hasMoreElements()) {
//
//				// byte[] receiveData = new byte[1024];
//
//				UnifiedPacket packet = generator.nextElement();
//
//				byte[] sendData = NetTool.serialize(packet);
//				DatagramPacket sendPacket = new DatagramPacket(sendData,
//						sendData.length,
//						InetAddress.getByName(serverAddress.ip),
//						serverAddress.port);
//				ClientRequestHandlerV2.packets2Send.offer(sendPacket);
//			}
//			byte[] needed = ClientRequestHandlerV2.neededPackets.get(UUID
//					.nameUUIDFromBytes(NetTool.serialize(crts.transactionId)));
//			if (needed != null) {
//				if (needed.length == 0) {
//					if (System.currentTimeMillis() - startTime > waitTime) {
//						reset();
//						for (int i = 0; i < generator.size(); i++) {
//							UnifiedPacket packet = generator.get(i + 1);
//
//							byte[] sendData = NetTool.serialize(packet);
//							DatagramPacket sendPacket = new DatagramPacket(
//									sendData, sendData.length,
//									InetAddress.getByName(serverAddress.ip),
//									serverAddress.port);
//							ClientRequestHandlerV2.packets2Send
//									.offer(sendPacket);
//						}
//					}
//				} else {
//					for (int i = 0; i < needed.length; i++) {
//						UnifiedPacket packet = generator.get(needed[i]);
//
//						byte[] sendData = NetTool.serialize(packet);
//						DatagramPacket sendPacket = new DatagramPacket(
//								sendData, sendData.length,
//								InetAddress.getByName(serverAddress.ip),
//								serverAddress.port);
//						ClientRequestHandlerV2.packets2Send.offer(sendPacket);
//					}
//				}
//			}
//		}
//		return ClientRequestHandlerV2.results.get(crts.transactionId);
//	}
//
//}
