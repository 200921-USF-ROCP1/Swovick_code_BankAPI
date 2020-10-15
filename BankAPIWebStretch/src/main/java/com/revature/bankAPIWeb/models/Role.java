package com.revature.bankAPIWeb.models;

public class Role {
	private int RoleId;

	public Role() {
	}

	public Role(int RoleId, String role) {
		setRoleId(RoleId);
		setRole(role);
	}

	public int getRoleId() {
		return RoleId;
	}

	public void setRoleId(int roleId) {
		RoleId = roleId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	private String role;
}
