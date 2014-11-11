package x;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.sql.Time;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.Address;
import main.Client2Mapper;
import main.Client2Server;
import main.NetTool;
import main.Server2ClientResult;

public class ClientRequestHandler {

	Timer timer = new Timer();

	int loc = 0;

	public ClientRequestHandler() {
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("stuck at " + loc);
			}
		}, 0, 500);
	}

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

	public Object getResult(Client2Mapper crtm, Client2Server crts) {

		Socket socketToMapper, socketToServer;
		try {
			String[] parts = NetTool.getMapperAddress().split(":");
			loc = 1;
			socketToMapper = new Socket(parts[0], Integer.valueOf(parts[1]));
			
			loc = 2;
			ObjectOutputStream oosm = new ObjectOutputStream(
					socketToMapper.getOutputStream());
			loc = 3;
			System.out.println("New request: " + crtm);
			oosm.writeObject(crtm);
			loc = 4;
			oosm.flush();
			loc = 5;
			ObjectInputStream oism = new ObjectInputStream(
					socketToMapper.getInputStream());
			loc = 6;
			Address serverAddress = (Address) oism.readObject();
			loc = 7;
			// String[] serverAddress = ((String) oism.readObject()).split(":");

			oism.close();
			oosm.close();
			socketToMapper.close();

			if (serverAddress != null) {
				System.out.println("Server address: " + serverAddress);
			}
			// socketToServer = new Socket(serverAddress.ip,
			// serverAddress.port);
			loc = 8;
			socketToServer = new Socket(serverAddress.ip, serverAddress.port);
			loc = 9;
			ObjectOutputStream ooss0 = new ObjectOutputStream(
					socketToServer.getOutputStream());
			loc = 10;
			Client2Server r0 = crts.cloneObj();
			loc = 11;
			r0.procId = "proc0";
			r0.params = null;
			ooss0.writeObject(r0);
			loc = 12;
			ooss0.flush();
			loc = 13;
			ObjectInputStream oiss0 = new ObjectInputStream(
					socketToServer.getInputStream());
			loc = 14;
			Server2ClientResult p0 = (Server2ClientResult) oiss0.readObject();
			loc = 15;
			ooss0.close();
			oiss0.close();
			socketToServer.close();

			/*
			 * if Proc0 return 0, which means the server is reachable, then
			 * continue sending the actual request
			 */
			if (p0 != null && p0.param.type.equals("int")
					&& (int) p0.param.value == 0) {
				System.out.println("Proc0 returns");

				// socketToServer = new Socket(serverAddress.ip,
				// serverAddress.port);
				loc = 16;
				socketToServer = new Socket(serverAddress.ip,
						serverAddress.port);
				loc = 17;
				ObjectOutputStream ooss = new ObjectOutputStream(
						socketToServer.getOutputStream());

				crts.setTransactionIdUid("WALALALALALLA"); // this line here can be
				// used to show how cache works

				ooss.writeObject(crts);
				loc = 18;
				ooss.flush();
				loc = 19;
				ObjectInputStream oiss = new ObjectInputStream(
						socketToServer.getInputStream());
				loc = 20;
				Server2ClientResult result = (Server2ClientResult) oiss.readObject();
				loc = 21;
				ooss.close();
				oiss.close();
				socketToServer.close();

				if (result.param.type.contains("[]")) {
					String componentTypeString = result.param.type.replace(
							"[]", "");
					loc = 22;
					Matcher m = Pattern.compile("\\[\\]").matcher(
							result.param.type);
					loc = 23;
					int count = 0;
					while (m.find()) {
						count += 1;
					}
					loc = 24;
					Class<?> pTypeClass = p2w.get(componentTypeString);

					if (pTypeClass == null) {
						try {
							pTypeClass = Class.forName(componentTypeString);
							return Array
									.newInstance(pTypeClass, new int[count])
									.getClass().cast(result.param.value);
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
					}
					loc = 25;
					return result.param.value;

				} else {
					Class<?> pTypeClass = p2w.get(result.param.type);

					if (pTypeClass == null) {
						try {
							pTypeClass = Class.forName(result.param.type);
							return pTypeClass.cast(result.param.value);
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
					}
					loc = 26;
					return result.param.value;
				}

			} else {
				loc = 27;
				return null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} catch (NullPointerException e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
		return null;
	}
}
