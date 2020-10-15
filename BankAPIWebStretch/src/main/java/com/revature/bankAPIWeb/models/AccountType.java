package com.revature.bankAPIWeb.models;

public class AccountType {
	private int typeId;
	private String type;

	public AccountType() {
	}

	public AccountType(int typeId, String type) {
		setTypeId(typeId);
		setType(type);
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
