package com.revature.bankAPIWeb.helpers;

import com.revature.bankAPIWeb.models.User;

public class Login {
	private String username;
	private String password;

	public Login() {
	}

	public Login(User user) {
		this.username = user.getUsername();
		this.password = user.getPassword();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean equals(Object o) {
		boolean result = false;
		Login loginAttempt = (Login) o;
		if (this.username.equals(loginAttempt.username) && this.password.equals(loginAttempt.password)) {
			result = true;
		}
		return result;
	}
}
