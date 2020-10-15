package com.revature.bankAPIWeb.dao.implementations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.revature.bankAPIWeb.dao.interfaces.AccountDAO;
import com.revature.bankAPIWeb.dao.interfaces.GenericBankAPIreadDAO;
import com.revature.bankAPIWeb.dao.interfaces.UserDAO;
import com.revature.bankAPIWeb.models.Account;
import com.revature.bankAPIWeb.models.AccountStatus;
import com.revature.bankAPIWeb.models.AccountType;
import com.revature.bankAPIWeb.models.User;
import com.revature.bankAPIWeb.services.ConnectionService;

public class AccountDAOImpl implements AccountDAO {
	private Connection connection;
	private GenericBankAPIreadDAO<AccountStatus> statusDao;
	private GenericBankAPIreadDAO<AccountType> typeDao;
	private UserDAO userDao;

	public AccountDAOImpl() {
		connection = ConnectionService.getConnection();
		statusDao = new AccountStatusDAOImpl();
		typeDao = new AccountTypeDAOImpl();
		userDao = new UserDAOImpl();
	}

	public int create(Account accnt) {
		int accntId = 0;
		try {
			PreparedStatement ps = connection.prepareStatement(
					"INSERT INTO Accounts " + "(balance, status_ID, type_id) VALUES (?, ?, ?);",
					Statement.RETURN_GENERATED_KEYS);
			ps.setDouble(1, accnt.getBalance());
			AccountStatus accntStatus = accnt.getStatus();
			ps.setInt(2, accntStatus.getStatusId());
			AccountType accntType = accnt.getType();
			ps.setInt(3, accntType.getTypeId());
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			rs.next();
			accntId = rs.getInt(1);
			accnt.setAccountId(accntId);
			List<User> accntUsers = accnt.getUsers();
			addAccountOwners(accnt, accntUsers);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return accntId;

	}

	// Helper method for create.
	private void addAccountOwners(Account accnt, List<User> users) {
		try {
			PreparedStatement ps = connection
					.prepareStatement("INSERT INTO users_accounts " + "(account_id, user_id) VALUES (?, ?);");
			for (User user : users) {
				ps.setInt(1, accnt.getAccountId());
				ps.setInt(2, user.getUserId());
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addAccountOwner(Account accnt, User user) {
		try {
			PreparedStatement ps = connection
					.prepareStatement("INSERT INTO users_accounts " + "(account_id, user_id) VALUES (?, ?);");
			ps.setInt(1, accnt.getAccountId());
			ps.setInt(2, user.getUserId());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Account get(int id) {
		Account accnt = null;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM Accounts " + "WHERE account_id = ?;");
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				accnt = new Account();
				accnt.setAccountId(rs.getInt("account_id"));
				accnt.setBalance(rs.getDouble("balance"));
				int statusId = rs.getInt("status_id");
				accnt.setStatus(statusDao.get(statusId));
				int typeId = rs.getInt("type_id");
				accnt.setType(typeDao.get(typeId));
				accnt.setUsers(getAccountOwners(id));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return accnt;
	}

	private List<User> getAccountOwners(int accntId) {
		List<User> users = null;
		try {
			PreparedStatement ps = connection
					.prepareStatement("SELECT * FROM users_accounts " + "WHERE account_id = ?;");
			ps.setInt(1, accntId);
			ResultSet rs = ps.executeQuery();
			users = new ArrayList<User>();
			while (rs.next()) {
				int currUserId = rs.getInt("user_id");
				User currUser = userDao.get(currUserId);
				users.add(currUser);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}

	public void update(Account accnt) {
		try {
			PreparedStatement ps = connection.prepareStatement(
					"UPDATE accounts SET " + "balance = ?, status_id = ?, type_id = ?" + "WHERE account_id = ?;");
			ps.setDouble(1, accnt.getBalance());
			AccountStatus accntStatus = accnt.getStatus();
			ps.setInt(2, accntStatus.getStatusId());
			AccountType accntType = accnt.getType();
			ps.setInt(3, accntType.getTypeId());
			ps.setInt(4, accnt.getAccountId());
			updateAccountOwners(accnt, accnt.getUsers());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// helper method to update
	private void updateAccountOwners(Account accnt, List<User> newUsers) {
		int accntId = accnt.getAccountId();
		List<User> usersInTable = getAccountOwners(accntId);
		List<User> usersToDelete = new ArrayList<User>();
		List<User> usersToAdd = new ArrayList<User>();
		for (User userInTable : usersInTable) {
			if (!newUsers.contains(userInTable)) {
				usersToDelete.add(userInTable);
			}
		}
		for (User newUser : newUsers) {
			if (!usersInTable.contains(newUser)) {
				usersToAdd.add(newUser);
			}
		}
		deleteAccountOwners(accnt, usersToDelete);
		addAccountOwners(accnt, usersToAdd);
	}

	// helper method to updateAccountOwners
	private void deleteAccountOwners(Account accnt, List<User> users) {
		try {
			PreparedStatement ps = connection
					.prepareStatement("DELETE FROM users_accounts " + "WHERE account_id = ? AND user_id = ?;");
			for (User user : users) {
				ps.setInt(1, accnt.getAccountId());
				ps.setInt(2, user.getUserId());
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void delete(Account accnt) {
		try {
			deleteAccountOwners(accnt, accnt.getUsers());
			PreparedStatement ps = connection.prepareStatement("DELETE FROM accounts WHERE account_id =?;");
			ps.setInt(1, accnt.getAccountId());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// helper method for delete in UserDAOImpl
	// It is assumed that UserDAOImpl is in the same package as AccountDAOImpl
	void deleteAccounts(List<Account> accounts) {
		try {

			PreparedStatement ps = connection.prepareStatement("DELETE FROM accounts " + "WHERE account_id = ?;");
			for (Account accnt : accounts) {
				deleteAccountOwners(accnt, accnt.getUsers());
				ps.setInt(1, accnt.getAccountId());
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Account> getAccounts(AccountStatus status) {
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM Accounts " + "WHERE status_id = ?;");
			ps.setInt(1, status.getStatusId());
			ResultSet rs = ps.executeQuery();
			ArrayList<Account> accnts = new ArrayList<Account>();
			while (rs.next()) {
				Account accnt = new Account();
				int accntId = rs.getInt("account_id");
				accnt.setAccountId(accntId);
				accnt.setBalance(rs.getDouble("balance"));
				accnt.setStatus(status);
				int typeId = rs.getInt("type_id");
				accnt.setType(typeDao.get(typeId));
				accnt.setUsers(getAccountOwners(accntId));
				accnts.add(accnt);
			}
			return accnts;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Account> getAccounts(User user) {
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM Accounts "
					+ "JOIN users_accounts ON accounts.account_id = users_accounts.account_id "
					+ "WHERE users_accounts.user_id = ?;");
			ps.setInt(1, user.getUserId());
			ResultSet rs = ps.executeQuery();
			ArrayList<Account> accnts = new ArrayList<Account>();
			while (rs.next()) {
				Account accnt = new Account();
				int accntId = rs.getInt("account_id");
				accnt.setAccountId(accntId);
				accnt.setBalance(rs.getDouble("balance"));
				int statusId = rs.getInt("status_id");
				accnt.setStatus(statusDao.get(statusId));
				int typeId = rs.getInt("type_id");
				accnt.setType(typeDao.get(typeId));
				accnt.setUsers(getAccountOwners(accntId));
				accnts.add(accnt);
			}
			return accnts;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Account> getAllAccounts() {
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM Accounts;");
			ResultSet rs = ps.executeQuery();
			ArrayList<Account> accnts = new ArrayList<Account>();
			while (rs.next()) {
				Account accnt = new Account();
				int accntId = rs.getInt("account_id");
				accnt.setAccountId(accntId);
				accnt.setBalance(rs.getDouble("balance"));
				int statusId = rs.getInt("status_id");
				accnt.setStatus(statusDao.get(statusId));
				int typeId = rs.getInt("type_id");
				accnt.setType(typeDao.get(typeId));
				accnt.setUsers(getAccountOwners(accntId));
				accnts.add(accnt);
			}
			return accnts;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
