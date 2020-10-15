package com.revature.bankAPIWeb.models;

import java.util.ArrayList;

public class User {
	private int userId;
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private String email;
	private Role role;

	public User() {
	}

	public User(String username, String password, String firstName, String lastName, String email, int roleId,
			String Role) {
		setUsername(username);
		setPassword(password);
		setFirstName(firstName);
		setLastName(lastName);
		setEmail(email);
		setRole(new Role(roleId, Role));
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public boolean equals(Object other) {
		boolean result = false;
		User otherUsr = (User) other;
		String otherUsrName = otherUsr.username;
		String otherEmail = otherUsr.email;
		if (otherUsrName.equals(this.username) && otherEmail.equals(this.email)) {
			result = true;
		}
		return result;
	}
}
