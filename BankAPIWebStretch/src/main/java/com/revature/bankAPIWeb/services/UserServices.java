package com.revature.bankAPIWeb.services;

import java.util.List;

import com.revature.bankAPIWeb.dao.implementations.UserDAOImpl;
import com.revature.bankAPIWeb.dao.interfaces.UserDAO;
import com.revature.bankAPIWeb.models.Account;
import com.revature.bankAPIWeb.models.Role;
import com.revature.bankAPIWeb.models.User;

public class UserServices {
	private UserDAO userDao;

	public UserServices() {
		userDao = new UserDAOImpl();
	}

	public boolean isCurrUser(User user, int user_id) {
		boolean result = false;
		if (user.getUserId() == user_id) {
			result = true;
		}
		return result;
	}

	public User updateUser(User user, User currUser) {
		Role currRole = currUser.getRole();
		user.setRole(currRole);
		userDao.update(user);
		return user;
	}

	public boolean containsUser(User user, List<User> users) {
		boolean result = false;
		if (users.contains(user)) {
			result = true;
		}
		return result;
	}

	public List<User> getAllUsers() {
		return userDao.getAll();
	}

	public User getUser(int id) {
		return userDao.get(id);
	}

	public void registerUser(User toRegister) throws Exception {
		List<User> allUsers = getAllUsers();
		if (!containsUser(toRegister, allUsers)) {
			int userId = userDao.create(toRegister);
			toRegister.setUserId(userId);
		} else {
			throw new Exception("Invalid Fields");
		}
	}

	public void updateUser(User user) {
		userDao.update(user);
	}

	public void upgradeToPremium(User user, int accntId, double cost) throws Exception {
		AccountServices accountService = new AccountServices();
		Account currAccount = accountService.getAccountById(accntId);
		List<Account> userAccounts = accountService.getAccountsByUser(user.getUserId());
		if (userAccounts.contains(currAccount)) {
			accountService.withdraw(cost, currAccount);
			user.setRole(new Role(4, "Premium"));
			updateUser(user);
		} else {
			throw new Exception("401");
		}
	}

	public boolean isRole(String roleName, User user) {
		boolean isRole = false;
		Role usrRole = user.getRole();
		if (usrRole.getRole().equals(roleName)) {
			isRole = true;
		}
		return isRole;
	}

	public void deleteUser(User user) {
		userDao.delete(user);
	}
}
