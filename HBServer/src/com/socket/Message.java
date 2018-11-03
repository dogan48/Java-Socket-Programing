package com.socket;

import java.io.Serializable;

import javax.swing.ImageIcon;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	public String type,from,to,content;
	public ImageIcon img;
	
	public Message (String type,String from,String content,String to) {
		this.type=type;
		this.from=from;
		this.to=to;
		this.content=content;
	}
	public void setImage(ImageIcon img) {
		this.img = img;
	}
	public ImageIcon getImage() {
		return img;
	}
	@Override
	public String toString() {
		return "{type='" + type + "', from='" + from + "', content='" + content + "', to='" + to + "'}"; 
	}
}
