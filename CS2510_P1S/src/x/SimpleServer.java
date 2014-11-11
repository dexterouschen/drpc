package x;
import java.io.*;
import java.net.*;
import java.util.*;

public class SimpleServer {
	
	public static void main(String [] args) throws IOException{
		ServerSocket listener =  new ServerSocket(9090);
		try{
			while(true){
				Socket socket = listener.accept();
				try{
					PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
					out.println(new Date().toString());
				} finally{
					socket.close();
				}
			}
			
		} finally{
			listener.close();
		}
	}
	
}
