package com.socket;

import java.io.*;
import java.net.*;

class ServerThread extends Thread {
	
	public SocketServer server = null;
	public Socket socket = null;
	public int socketPort = -1;
	public String phoneNumber = "";
	public ObjectInputStream streamIn = null;
	public ObjectOutputStream streamOut = null;
	
	
	public ServerThread(SocketServer _server, Socket _socket) {
		super();
		server = _server;
		socket = _socket;
		socketPort = socket.getPort();
	}
	
	public void send(Message msg){
        try {
            streamOut.writeObject(msg);
            streamOut.flush();
        } 
        catch (IOException ex) {
            System.out.println("Exception [SocketClient : send(...)]");
        }
    }
	
	public int getSocketPort() {
		return socketPort;		
	}
	
	@SuppressWarnings("deprecation")
	public void run() {
		System.out.println("Server çalýþýyor...");
		while(true) {
			try {
				Message msg = (Message) streamIn.readObject();
				server.handle(socketPort, msg);
				
			}catch(Exception ioe) {
				System.err.println("ERROR : "+ ioe.getMessage());
				stop();
			}
		}
	}
	public void open() throws IOException{
		streamOut = new ObjectOutputStream(socket.getOutputStream());
		streamOut.flush();
		streamIn = new ObjectInputStream(socket.getInputStream());
	}
	public void close() throws IOException {
		if(socket != null) socket.close();
		if(streamIn != null) streamIn.close();
		if(streamOut != null) streamOut.close();
	}
	
}

public class SocketServer implements Runnable {
	public ServerThread clients[];
	public ServerSocket server = null;
	public Thread thread = null;
	public int clientCount = 0, port = 61000;
	
	public SocketServer() {
		clients = new ServerThread[10];
		
		try {
			server = new ServerSocket(port);
			port = server.getLocalPort();
			System.out.println("Server IP: "+InetAddress.getLocalHost()+" Port: "+server.getLocalPort());
			
		}catch(IOException ioe){
			System.err.println("Server baþlatýlamadý!");
		}
		
	}

	@Override
	public void run() {
		while(thread != null) {
			try {
				System.out.println("Client Baðlantýsý bekleniyor...");
				addThread(server.accept());
			}catch(IOException ioe){
				System.err.println("ERROR3 : "+ ioe.getMessage());
			}
		}
	}
	public void start() {
		if(thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}
	@SuppressWarnings("deprecation")
	public void stop() {
		if(thread != null) {
			thread.stop();
			thread = null;
		}
	}
	private int findClient(int socketPort) {
		for(int i = 0; i < clientCount; i++) {
			if(clients[i].getSocketPort() == socketPort) {
				return i;
			}
		}
		return -1;
	}

	 public synchronized void handle(int socketPort, Message msg){  
		 if(msg.type.equals("message")){
			 findUserThread(msg.to).send(msg);
         }else if(msg.type.equals("image")){
    			 findUserThread(msg.to).send(msg);
    	 }else if (msg.type.equals("login")) {
            	clients[findClient(socketPort)].phoneNumber = msg.from;
         }else if(msg.type.equals("logout")) {
            	 remove(socketPort);
         }
	 }
	
	 public ServerThread findUserThread(String usr){
	        for(int i = 0; i < clientCount; i++){
	            if(clients[i].phoneNumber.equals(usr)){
	                return clients[i];
	            }
	        }
	        return null;
	    }
	 
	 public synchronized void remove(int ID) {
		 int pos = findClient(ID);
	        if (pos >= 0){  
	            ServerThread toTerminate = clients[pos];
	            System.out.println("\nRemoving client thread " + ID + " at " + pos);
		    if (pos < clientCount-1){
	                for (int i = pos+1; i < clientCount; i++){
	                    clients[i-1] = clients[i];
		        }
		    }
		    clientCount--;
		    try{  
		      	toTerminate.close(); 
		    }
		    catch(IOException ioe){  
		    	System.out.println("\nError closing thread: " + ioe); 
		    }
		    toTerminate.stop(); 
		}
		 
	 }
	private void addThread(Socket socket) {
		if(clientCount < clients.length) {
			System.out.println("Baðlanan Client: "+ socket);
			clients[clientCount] = new ServerThread(this,socket);
			try {
				clients[clientCount].open();
				clients[clientCount].start();
				clientCount++;
			}catch(IOException ioe) {
				System.err.println("ERROR5 : "+ ioe.getMessage());
			}
		}
		else
			System.err.println("Yer yok...");
	}
}
