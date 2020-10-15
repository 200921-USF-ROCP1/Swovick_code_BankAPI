package com.revature.bankAPIWeb.dao.interfaces;

import java.util.List;

import com.revature.bankAPIWeb.models.Account;
import com.revature.bankAPIWeb.models.AccountStatus;
import com.revature.bankAPIWeb.models.User;

public interface AccountDAO {

	public int create(Account accnt);

	public void addAccountOwner(Account accnt, User user);

	public Account get(int id);

	public List<Account> getAccounts(AccountStatus status);

	public List<Account> getAccounts(User user);

	public void update(Account accnt);

	public void delete(Account accnt);

	public List<Account> getAllAccounts();
}
