package com.revature.bankAPIWeb.helpers;

public class ChangeBalance {
	private int accountId;
	private double amount;

	public ChangeBalance() {
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
}
