package x;
//package main;
//
//import java.io.Serializable;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.concurrent.ConcurrentLinkedQueue;
//
//public class ServerList implements Serializable {
//	private static final long serialVersionUID = -3032154081993667724L;
//	// Integer next;
//	LinkedList<Address> list;
//
//	public ServerList(/* Integer next, */LinkedList<Address> list) {
//		// this.next = next;
//		this.list = list;
//	}
//
//	public Address nextService() {
//		// int size = list.size();
//		Address adr = list.peek();// get(this.next.intValue());
//		list.offer(adr);
//		list.remove();
//		// this.next = (this.next + 1) % size;
//		return adr;
//	}
//
//	public String toString() {
//		String string = "";
//		Iterator<Address> iterator = list.iterator();
//
//		while (iterator.hasNext()) {
//			string += iterator.next().toString();
//		}
//
//		return "Server list: " + "[" + string + "]";
//	}
//
//}
