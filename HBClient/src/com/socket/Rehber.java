package com.socket;

import java.io.File;
import com.socket.ClientFrame;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Rehber {
	
	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	DocumentBuilder docBuilder;
	Document doc;
	File file = new File("rehber.xml");
	Element rootElement;
	Element person;
	Element fName;
	Element phoneNumber;
	Transformer transformer;
	DOMSource source;
	StreamResult result;
	ClientFrame frame;
	
	DocumentBuilderFactory historyDocFactory = DocumentBuilderFactory.newInstance();
	TransformerFactory historyTransformerFactory = TransformerFactory.newInstance();
	DocumentBuilder historyDocBuilder;
	Document historyDoc;
	File msgHistoryFile;
	Element historyRootElement;
	Element message;
	Element content;
	Element image;
	Transformer historyTransformer;
	DOMSource historySource;
	StreamResult historyResult;


	public Rehber(ClientFrame frame) {
		this.frame = frame;
		try {
	        if (!file.exists()) {
				docBuilder = docFactory.newDocumentBuilder();
	        	doc = docBuilder.newDocument();
	        	rootElement = doc.createElement("rehber");
	        	doc.appendChild(rootElement);
				    
				transformer = transformerFactory.newTransformer();
				source = new DOMSource(doc);
				result = new StreamResult(new File("rehber.xml"));
				transformer.transform(source, result);
	        }			   			
		}catch (Exception e) {
		      e.printStackTrace();
		}
	}
	public void addPerson(String name, String pNumber) {
		try {
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse("rehber.xml");
			NodeList list = doc.getElementsByTagName("rehber");
		    rootElement = (Element) list.item(0);
		    
		    person = doc.createElement("person");
		    rootElement.appendChild(person);
		    
		    fName = doc.createElement("ad");
		    fName.appendChild(doc.createTextNode(name));
		    person.appendChild(fName);
		    
		    phoneNumber = doc.createElement("phoneNumber");
		    phoneNumber.appendChild(doc.createTextNode(pNumber));
		    person.appendChild(phoneNumber);
		    
		    transformer = transformerFactory.newTransformer();
		    source = new DOMSource(doc);
		    result = new StreamResult("rehber.xml");
		    transformer.transform(source, result);
		}catch (Exception e) {
		      e.printStackTrace();
		}
	}
	public void fillList() {
		try {

			frame.listRehber.removeAll();
			frame.model.clear();
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse("rehber.xml");
			NodeList list = doc.getElementsByTagName("person");
			for(int i=0;i<list.getLength();i++) {
				frame.model.addElement(list.item(i).getFirstChild().getTextContent()+" "+list.item(i).getLastChild().getTextContent());
			}
			frame.listRehber.setListData(frame.model.toArray());
			frame.listRehber.setSelectedIndex(0);
			frame.listRehber.revalidate();
			frame.listRehber.repaint();
			
		}catch(Exception e){}
	}
	public String findPerson(String personNo) {
		try {
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse("rehber.xml");
			NodeList list = doc.getElementsByTagName("person");
			for(int i=0;i<list.getLength();i++) {
				if(personNo.equals(list.item(i).getLastChild().getTextContent())) {
					return list.item(i).getFirstChild().getTextContent();
				}
			}
		}catch(Exception e){}
		return personNo;
	}
	public void createHistory(String pNumber) {
		try {
			historyDocBuilder = historyDocFactory.newDocumentBuilder();
			historyDoc = historyDocBuilder.newDocument();
			historyRootElement = historyDoc.createElement("history");
			historyDoc.appendChild(historyRootElement);
			    
			historyTransformer = historyTransformerFactory.newTransformer();
			historySource = new DOMSource(historyDoc);
			historyResult = new StreamResult(new File(this.getClass().getResource("/com/MessageHistory/").getPath() + pNumber + ".xml"));
			historyTransformer.transform(historySource, historyResult);				
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	public void addMessageToHistory(String msg,String imgSrc, String targetNumber,String toOrFromMe) {
		String tmp = this.getClass().getResource("/com/MessageHistory/").getPath();
		File msgHistoryFile = new File(tmp + targetNumber + ".xml");
	    if (!msgHistoryFile.exists()) {
			createHistory(targetNumber);
	        }
    	try {
    		historyDocBuilder = historyDocFactory.newDocumentBuilder();
    		historyDoc = historyDocBuilder.parse(msgHistoryFile.getAbsolutePath());
    		
			NodeList hList = historyDoc.getElementsByTagName("history");
			historyRootElement = (Element) hList.item(0);
			
		    message = historyDoc.createElement("message");
		    message.setAttribute("toOrFromMe", toOrFromMe);
		    historyRootElement.appendChild(message);
		    
		    content = historyDoc.createElement("content");
		    content.appendChild(historyDoc.createTextNode(msg));
		    message.appendChild(content);
		    
		    image = historyDoc.createElement("image");
		    image.appendChild(historyDoc.createTextNode(imgSrc));
		    message.appendChild(image);
		    
		    historyTransformer = historyTransformerFactory.newTransformer();
		    historySource = new DOMSource(historyDoc);
		    historyResult = new StreamResult(msgHistoryFile.getAbsolutePath());
		    historyTransformer.transform(historySource, historyResult);
		    
		}catch (Exception e) {
		      e.printStackTrace();
		}
	}
	public void fillMsgPanelFromHistory(String selectedNumber) {
		String tmp = this.getClass().getResource("/com/MessageHistory/").getPath();
		File msgHistoryFile = new File(tmp + selectedNumber + ".xml");
	    if (!msgHistoryFile.exists()) {
			createHistory(selectedNumber);
	        }
		try {
			frame.msgPanel.removeAll();
			frame.msgPanel.validate();
			frame.msgPanel.repaint();
			historyDocBuilder = historyDocFactory.newDocumentBuilder();
			historyDoc = historyDocBuilder.parse(msgHistoryFile.getAbsolutePath());

			NodeList historyList = historyDoc.getElementsByTagName("message");

			for(int i = 0; i < historyList.getLength(); i++) {
				Element e = (Element)historyList.item(i);
				String Attirbute = e.getAttribute("toOrFromMe");
				if(historyList.item(i).getLastChild().getTextContent().toString().equals("null")) {		
					frame.showMessage(new Message("message",selectedNumber,historyList.item(i).getFirstChild().getTextContent().toString(),""), Attirbute);
				}else {
					Message msg = new Message("image",selectedNumber,historyList.item(i).getFirstChild().getTextContent().toString(),"");
					File imageFile = new File(this.getClass().getResource("/com/media/Images/").getPath() + historyList.item(i).getLastChild().getTextContent().toString());
					ImageIcon img = new ImageIcon(ImageIO.read(imageFile));
					msg.setImage(img);
					frame.showImage(msg, Attirbute);
				}
	
			}

			
		}catch(Exception e){}
		
	}
	
}
