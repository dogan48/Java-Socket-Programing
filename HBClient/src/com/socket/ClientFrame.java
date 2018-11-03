package com.socket;

import java.awt.EventQueue;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Image;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.awt.Toolkit;
import net.miginfocom.swing.MigLayout;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class ClientFrame extends JFrame {
	
	public SocketClient client;
	public Rehber rehber = new Rehber(this);
    public int port = 61000;
    public String serverAddr, phoneNumber, password;
    public Thread clientThread;
    public DefaultListModel	model = new DefaultListModel();
    public DefaultListModel	chatsModel = new DefaultListModel();
  
	private JPanel contentPane;
	public JPanel msgPanel;
	private JTextField txtServerIp;
	private JTextField txtKullaniciAdi;
	private JTextField txtYeniKisi;
    public StyledDocument doc;
    public SimpleAttributeSet giden;
    public SimpleAttributeSet gelen;
	public JButton btnBaglan;
	public FileOutputStream fos;
	public Boolean newMessage;
	public String newMessageFrom;
	
	public JTextArea txtMesaj;
	public JList listRehber;
	public JList chatsList;
	private JTextField txtTelNo;
	private JLabel lblAd;
	private JLabel label;
	private JScrollPane scrollPane;
	public String selectedPersonNumber;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientFrame frame = new ClientFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClientFrame() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(ClientFrame.class.getResource("/com/media/icon.png")));	
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				disconnect();
			}
		});
		setTitle("HBClient");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 666, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(4, 39, 438, 418);
		contentPane.add(scrollPane);
		
		msgPanel = new JPanel();
		msgPanel.setBorder(null);
		scrollPane.setViewportView(msgPanel);
		msgPanel.setLayout(new MigLayout("wrap"));
		
		JLabel lblNewLabel = new JLabel("Server IP:");
		lblNewLabel.setBounds(10, 11, 56, 14);
		contentPane.add(lblNewLabel);
		
		txtServerIp = new JTextField();
		txtServerIp.setText("localhost");
		txtServerIp.setBounds(86, 8, 180, 20);
		contentPane.add(txtServerIp);
		txtServerIp.setColumns(10);
		
		btnBaglan = new JButton("Ba\u011Flan");
		btnBaglan.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				baglanButton(arg0);
			}
		});
		btnBaglan.setBounds(551, 8, 89, 20);
		contentPane.add(btnBaglan);
/*
		gelen = new SimpleAttributeSet();
		StyleConstants.setAlignment(gelen, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(gelen,Color.BLACK);
		StyleConstants.setBackground(gelen, Color.WHITE);
		
		giden = new SimpleAttributeSet();
		StyleConstants.setAlignment(giden, StyleConstants.ALIGN_RIGHT);
		StyleConstants.setForeground(giden,Color.BLACK);
		StyleConstants.setBackground(giden, Color.GREEN);*/
		
		JLabel lblKullancAd = new JLabel("Telefon No:");
		lblKullancAd.setBounds(276, 11, 79, 14);
		contentPane.add(lblKullancAd);
		
		txtKullaniciAdi = new JTextField();
		txtKullaniciAdi.setColumns(10);
		txtKullaniciAdi.setBounds(365, 8, 176, 20);
		contentPane.add(txtKullaniciAdi);
		
		txtMesaj = new JTextArea();
		txtMesaj.setWrapStyleWord(true);
		txtMesaj.setLineWrap(true);
		txtMesaj.setToolTipText("Mesaj");
		txtMesaj.setBounds(10, 468, 322, 83);
		contentPane.add(txtMesaj);
		
		JButton btnGonder = new JButton("G\u00F6nder");
		btnGonder.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				gonderButton(arg0);			
			}
		});
		btnGonder.setBounds(342, 468, 79, 40);
		contentPane.add(btnGonder);
		
		listRehber = new JList();
		listRehber.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if(!listRehber.isSelectionEmpty()) {
					String[] temp = listRehber.getSelectedValue().toString().split(" ");
					selectedPersonNumber = temp[temp.length-1];
					rehber.fillMsgPanelFromHistory(selectedPersonNumber);
					 if(selectedPersonNumber.equals(newMessageFrom)) 
	  						setTitle("HBClient");
					chatsList.clearSelection();
				}
				
			}
		});

		listRehber.setModel(model);
		listRehber.setBounds(447, 293, 193, 164);
		contentPane.add(listRehber);
		
		txtYeniKisi = new JTextField();
		txtYeniKisi.setToolTipText("Eklenecek Ki\u015Fi");
		txtYeniKisi.setBounds(431, 486, 100, 24);
		contentPane.add(txtYeniKisi);
		txtYeniKisi.setColumns(10);
		
		rehber.fillList();
		
		JButton btnEkle = new JButton("Rehbere Ekle");
		btnEkle.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(!txtYeniKisi.getText().isEmpty() && !txtTelNo.getText().isEmpty()) {
					rehber.addPerson(txtYeniKisi.getText(), txtTelNo.getText());
					txtYeniKisi.setText("");
					txtTelNo.setText("");
					rehber.fillList();	
					loadChats();
				}
			}
		});
		btnEkle.setBounds(431, 521, 209, 30);
		contentPane.add(btnEkle);
		
		txtTelNo = new JTextField();
		txtTelNo.setToolTipText("Eklenecek Ki\u015Fi");
		txtTelNo.setColumns(10);
		txtTelNo.setBounds(540, 486, 100, 24);
		contentPane.add(txtTelNo);
		
		lblAd = new JLabel("Ad :");
		lblAd.setBounds(431, 468, 46, 14);
		contentPane.add(lblAd);
		
		label = new JLabel("Telefon No :");
		label.setBounds(540, 468, 66, 14);
		contentPane.add(label);
		
		JButton btnResimGonder = new JButton("Resim G\u00F6nder");
		btnResimGonder.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				ResimGonderButton(arg0);
			}
		});
		btnResimGonder.setBounds(342, 511, 79, 40);
		contentPane.add(btnResimGonder);
		
		JLabel lblRehber = new JLabel("REHBER");
		lblRehber.setBounds(452, 277, 84, 14);
		contentPane.add(lblRehber);
		
		chatsList = new JList();
		chatsList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if(!chatsList.isSelectionEmpty()) {
					String[] temp = chatsList.getSelectedValue().toString().split(" ");
					selectedPersonNumber = temp[temp.length-1];
					 if(selectedPersonNumber.equals(newMessageFrom)) 
	  						setTitle("HBClient");
					rehber.fillMsgPanelFromHistory(selectedPersonNumber);
					listRehber.clearSelection();				
				}				
			}
		});
		chatsList.setBounds(447, 54, 193, 222);
		contentPane.add(chatsList);
		
		JLabel lblSohbetler = new JLabel("SOHBETLER");
		lblSohbetler.setBounds(447, 39, 84, 14);
		contentPane.add(lblSohbetler);
	}
	public void baglanButton(MouseEvent arg0) {
		serverAddr = txtServerIp.getText();	
		if(!serverAddr.isEmpty()) {
			 try{
	                client = new SocketClient(this);
	                clientThread = new Thread(client);
	                clientThread.start();
	                phoneNumber = txtKullaniciAdi.getText();
	                if(!phoneNumber.isEmpty()){
	                	client.send(new Message("login", phoneNumber, "", ""));
	                	btnBaglan.setVisible(false);
	                	loadChats();
	                }
	            }
	            catch(Exception ex){
	            	JLabel hataMsgLabel = new JLabel("Bağlantı Hatası!");
	    			btnBaglan.setVisible(true);
	         		msgPanel.add(hataMsgLabel,"push, growx");
	         		msgPanel.revalidate();
	         		msgPanel.repaint();
	         		}
			 }
		}

	public void gonderButton(MouseEvent arg0) {
		String msgTxt = txtMesaj.getText();
		if(!msgTxt.isEmpty() && !selectedPersonNumber.isEmpty()){
            txtMesaj.setText("");
            try {
            	Message msg = new Message("message", phoneNumber, msgTxt, selectedPersonNumber);
                client.send(msg);
                rehber.addMessageToHistory(msg.content, "null", selectedPersonNumber,"fromMe");
                rehber.fillMsgPanelFromHistory(selectedPersonNumber);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
		
	}
	public void ResimGonderButton(MouseEvent arg0) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.showDialog(this, "Resim Gönder");
		File file = fileChooser.getSelectedFile();
		String text = txtMesaj.getText();
		String imgSrc;
		txtMesaj.setText("");

		try {
			ImageIcon img = new ImageIcon(ImageIO.read(file));
			if(img != null && !selectedPersonNumber.isEmpty()) {
				Message msg = new Message("image", phoneNumber, text, selectedPersonNumber);
				msg.setImage(img);
				client.send(msg);
				
				Date now = new Date();
				imgSrc = now.getTime() + ".png";
				
				saveImage(img.getImage(), new File(this.getClass().getResource("/com/media/Images/").getPath() + imgSrc));
				rehber.addMessageToHistory(msg.content, imgSrc, msg.to, "fromMe");
				//showImage(msg, "fromMe");
				 rehber.fillMsgPanelFromHistory(selectedPersonNumber);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void disconnect() {
		try {
			client.send(new Message("logout", phoneNumber, "", ""));
			
		}catch(Exception e) {
		}
	}
	public void showMessage(Message msg, String toOrFromMe ) {
		String temp,msgFromName;
		if(toOrFromMe.equals("toMe")) {
			try {
	            if(msg.type.equals("message")){
	            	msgFromName = rehber.findPerson(msg.from).toString();
	            	temp = "<html><table style=\"width:310px;\"><tr><td style=\"width:160px;\"><div style=\"width: 160px; padding: 5px; background-color: white; text-align: left; word-wrap: break-word;\"><span style=\"font-weight: bold; color: blue;\">" + msgFromName + " :</span><br>" + msg.content + "</div></td><td style=\"width:60px;\"></td><td style=\"width:60px;\"></td></html>";
	            	JLabel gelenMsgLabel = new JLabel(temp);
	            	msgPanel.add(gelenMsgLabel,"push, growx");
	         		msgPanel.revalidate();
	         		msgPanel.repaint();
	            }
	    	}catch(Exception ex) {
	    		JLabel hataMsgLabel = new JLabel("Bağlantı Hatası!");
				btnBaglan.setVisible(true);
	     		msgPanel.add(hataMsgLabel,"push, growx");
	     		msgPanel.revalidate();
	     		msgPanel.repaint();
	    	}
		}else if(toOrFromMe.equals("fromMe")) {
			 if(msg.type.equals("message")){
				temp = "<html><table style=\"width:310px;\"><tr><td style=\"width:60px;\"></td><td style=\"width:60px;\"></td><td style=\"width:160px;\"><div style=\"width: 160px; padding: 5px; background-color: #94e761; text-align:left; word-wrap: break-word;\">" + msg.content + "</div></td></tr></table></html>";
				JLabel msgLabel = new JLabel(temp);
	    		msgPanel.add(msgLabel,"push, growx");
	    		msgPanel.revalidate();
	    		msgPanel.repaint();
			 }
			
		}
	}
	public void showImage(Message msg, String toOrFromMe) {
		String temp;
		if(toOrFromMe.equals("toMe")) {
			String msgFromName = rehber.findPerson(msg.from).toString();
			temp="<html><div style=\"width: 160px; padding: 5px; background-color: white; text-align: left; word-wrap: break-word;\"><span style=\"font-weight: bold; color: blue;\">" + msgFromName + ": </span><br>" + msg.content + "</div></html>";
			ImageIcon orjImg = msg.img;
			Image image = msg.img.getImage(); 
			image = image.getScaledInstance(200, 200,  java.awt.Image.SCALE_SMOOTH);   
			msg.img = new ImageIcon(image);
			
			JLabel gelenImgLabel = new JLabel(temp,msg.img,SwingConstants.LEFT);
			gelenImgLabel.setVerticalTextPosition(JLabel.BOTTOM);
			gelenImgLabel.setHorizontalTextPosition(JLabel.CENTER);
			gelenImgLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					final JFileChooser fchooser = new JFileChooser(".");
					File saveFile = new File("outputFile.png");
					fchooser.setSelectedFile(saveFile);
		            int retvalue = fchooser.showSaveDialog(gelenImgLabel);
		            if (retvalue == JFileChooser.APPROVE_OPTION)
		            {
		            	saveImage(orjImg.getImage(),fchooser.getSelectedFile());
		             }
				}
			});
			msgPanel.add(gelenImgLabel,"push, growx");
	 		msgPanel.revalidate();
	 		msgPanel.repaint();
		}else if (toOrFromMe.equals("fromMe")) {
			temp="<html><div style=\"width: 160px; padding: 5px; background-color: #94e761; text-align: left; word-wrap: break-word;\">" + msg.content + "</div></html>";
			
			Image image = msg.img.getImage(); // transform it 
			image = image.getScaledInstance(200, 200,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
			msg.img = new ImageIcon(image);
			
			JLabel gidenImgLabel = new JLabel(temp,msg.img,SwingConstants.RIGHT);
			gidenImgLabel.setVerticalTextPosition(JLabel.BOTTOM);
			gidenImgLabel.setHorizontalTextPosition(JLabel.CENTER);
			msgPanel.add(gidenImgLabel,"push, growx");
	 		msgPanel.revalidate();
	 		msgPanel.repaint();
			
		}				
	}
	public static void saveImage(Image img, File file) {
		BufferedImage bimg = toBufferedImage(img);
    	try {
			ImageIO.write(bimg, "PNG", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }

	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    return bimage;
	}
	public void loadChats() {
		File folder = new File(ClientFrame.class.getResource("/com/MessageHistory").getPath());
		File listOfFiles[] = folder.listFiles();
		String tempChatName, tempPersonName ;
		chatsModel.clear();
		chatsList.removeAll();
		chatsList.revalidate();
		chatsList.repaint();		

		for(int i=0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".xml")) {
				tempChatName = listOfFiles[i].getName().substring(0,listOfFiles[i].getName().lastIndexOf("."));
				tempPersonName = rehber.findPerson(tempChatName);
				if(tempPersonName.equals(tempChatName))
					chatsModel.addElement("Bilinmeyen Numara " + tempChatName);
				else {
					chatsModel.addElement(tempPersonName + " " + tempChatName);
				}
			}
		}
		chatsList.setListData(chatsModel.toArray());
		chatsList.revalidate();
		chatsList.repaint();
		
	}
}