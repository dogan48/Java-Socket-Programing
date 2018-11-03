package com.socket;

import com.socket.ClientFrame;
import java.io.*;
import java.net.*;
import java.util.Date;
import javax.swing.JLabel;

public class SocketClient implements Runnable {

	 	private int port;
	 	private String serverAddr;
	 	private Socket socket;
	 	private ClientFrame frame;
	 	private ObjectInputStream In;
	 	private ObjectOutputStream Out;
	    
	public SocketClient(ClientFrame frame) throws IOException {
		this.frame = frame;
		this.port = frame.port;
		this.serverAddr = frame.serverAddr;
		socket = new Socket(InetAddress.getByName(serverAddr), port);
        Out = new ObjectOutputStream(socket.getOutputStream());
       // Out.flush();
        In = new ObjectInputStream(socket.getInputStream());
	}
	@Override
	public void run() {
		 boolean keepRunning = true;
	        while(keepRunning){
	        	 try {
	                 Message msg = (Message) In.readObject();
	                 if(msg.type.equals("message")) {
	                	 frame.rehber.addMessageToHistory(msg.content, "null", msg.from, "toMe"); 	
	                 }else if(msg.type.equals("image")) {
	     					Date now = new Date();
	     					String imgSrc = now.getTime() + ".png";
	     					File file = new File(this.getClass().getResource("/com/media/Images/").getPath() + imgSrc);
	     					frame.saveImage(msg.img.getImage(), file);
	     					frame.rehber.addMessageToHistory(msg.content, imgSrc, msg.from, "toMe");
	                }
  					 frame.newMessageFrom = msg.from;
  					 if(!frame.selectedPersonNumber.equals(msg.from))
  						 frame.setTitle("HBClient - " + frame.rehber.findPerson(msg.from) + " size mesaj yolladý!");
  					 else
  						 frame.rehber.fillMsgPanelFromHistory(frame.selectedPersonNumber);
	        	 }catch(Exception ex) {
	        		keepRunning = false;
	        		JLabel msgLabel = new JLabel("Baðlantý Hatasý!");
	     			frame.btnBaglan.setVisible(true);
	          		frame.msgPanel.add(msgLabel,"push, growx");
	          		frame.msgPanel.revalidate();
	          		frame.msgPanel.repaint();
	        		frame.clientThread.stop();
				}
	        }
	}
	public void send(Message msg) {
		try {
			Out.writeObject(msg);
			Out.flush();
		}catch(IOException ioe) {
			System.out.println("Exception SocketClient send()");
		}
	}
	 public void closeThread(Thread t){
	        t = null;
	    }
}