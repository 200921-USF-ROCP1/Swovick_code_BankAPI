package com.revature.bankAPIWeb.services;

import java.util.List;

import com.revature.bankAPIWeb.dao.implementations.AccountDAOImpl;
import com.revature.bankAPIWeb.dao.implementations.AccountStatusDAOImpl;
import com.revature.bankAPIWeb.dao.implementations.UserDAOImpl;
import com.revature.bankAPIWeb.dao.interfaces.AccountDAO;
import com.revature.bankAPIWeb.dao.interfaces.GenericBankAPIreadDAO;
import com.revature.bankAPIWeb.dao.interfaces.UserDAO;
import com.revature.bankAPIWeb.models.Account;
import com.revature.bankAPIWeb.models.AccountStatus;
import com.revature.bankAPIWeb.models.User;

public class AccountServices {
	private AccountDAO accountDao;
	private GenericBankAPIreadDAO<AccountStatus> statusDao;
	private UserDAO userDao;

	public AccountServices() {
		accountDao = new AccountDAOImpl();
		userDao = new UserDAOImpl();
		statusDao = new AccountStatusDAOImpl();
	}

	public void createAccount(Account accnt) {
		int accntId = accountDao.create(accnt);
		accnt.setAccountId(accntId);
	}

	public List<Account> getAllAccounts() {
		return accountDao.getAllAccounts();
	}

	public Account getAccountById(int accntId) {
		return accountDao.get(accntId);
	}

	public List<Account> getAccountsByUser(int userId) {
		User user = userDao.get(userId);
		return accountDao.getAccounts(user);
	}

	public List<Account> getAccountsByStatus(int statusId) {
		AccountStatus status = statusDao.get(statusId);
		return accountDao.getAccounts(status);
	}

	public void deposit(double amount, Account currAccount) {
		double currBalance = currAccount.getBalance();
		currAccount.setBalance(currBalance + amount);
		updateAccount(currAccount);
	}

	public void withdraw(double amount, Account currAccount) throws Exception {
		double currBalance = currAccount.getBalance();
		if ((currBalance - amount) >= 0) {
			currAccount.setBalance(currBalance - amount);
			updateAccount(currAccount);
		} else {
			throw new Exception("Withdrawal cost larger than the current balance");
		}
	}

	public void transfer(double amount, Account transferFrom, Account transferTo) throws Exception {
		withdraw(amount, transferFrom);
		deposit(amount, transferTo);
	}

	public void updateAccount(Account accnt) {
		accountDao.update(accnt);
	}

	public void deleteAccount(Account accnt) {
		accountDao.delete(accnt);
	}
}
