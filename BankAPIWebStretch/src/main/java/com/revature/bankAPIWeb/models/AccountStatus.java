package com.revature.bankAPIWeb.models;

public class AccountStatus {
	private int statusId;
	private String status;

	public AccountStatus() {
	}

	public AccountStatus(int statusId, String status) {
		setStatusId(statusId);
		setStatus(status);
	}

	public boolean equals(AccountStatus accntStatus) {
		boolean result = false;
		if (this.statusId == accntStatus.statusId && this.status == accntStatus.status) {
			result = true;
		}
		return result;
	}

	public int getStatusId() {
		return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
