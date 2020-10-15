package com.revature.bankAPIWeb.models;

import java.util.List;

public class Account {
	private int accountId;
	private double balance;
	private AccountStatus status;
	private AccountType type;
	private List<User> users;

	public Account() {
	}

	public Account(double balance, int StatusId, String status, int TypeId, String type) {
		setBalance(balance);
		setStatus(new AccountStatus(StatusId, status));
		setType(new AccountType(TypeId, type));
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}

	public AccountType getType() {
		return type;
	}

	public void setType(AccountType type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		Account otherAccount = (Account) obj;
		if (this.accountId == otherAccount.accountId) {
			result = true;
		}
		return result;
	}
}
