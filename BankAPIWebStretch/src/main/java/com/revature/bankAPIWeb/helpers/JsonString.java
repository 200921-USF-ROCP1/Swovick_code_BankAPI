package com.revature.bankAPIWeb.helpers;

import java.io.PrintWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonString {
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void printMessage(PrintWriter pw, String message) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			this.setMessage(message);
			pw.println(mapper.writeValueAsString(this));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
