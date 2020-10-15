package com.revature.bankAPIWeb.services;

import com.revature.bankAPIWeb.dao.implementations.UserDAOImpl;
import com.revature.bankAPIWeb.dao.interfaces.UserDAO;
import com.revature.bankAPIWeb.helpers.Login;
import com.revature.bankAPIWeb.models.User;

public class LoginService {
	public User logIn(Login loginAttempt) {
		String loginUsername = loginAttempt.getUsername();
		UserDAO userDao = new UserDAOImpl();
		User userFromTable = userDao.get(loginUsername);
		if (userFromTable != null) {
			Login loginInfo = new Login(userFromTable);
			if (loginAttempt.equals(loginInfo)) {
				return userFromTable;
			}
		}

		return null;
	}
}
