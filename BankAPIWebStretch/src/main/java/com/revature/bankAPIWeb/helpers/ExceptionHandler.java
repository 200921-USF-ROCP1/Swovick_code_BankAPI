package com.revature.bankAPIWeb.helpers;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.revature.bankAPIWeb.models.User;
import com.revature.bankAPIWeb.services.UserServices;

public class ExceptionHandler {
	private final static Exception NOT_PERMITTED = new Exception("The requested action is not permitted");

	public static void throwNullObject(Object obj, HttpServletResponse response, String message) throws Exception {
		if (obj == null) {
			response.setStatus(400);
			throw new Exception(message);
		}
	}

	public static void throwNotRole(String roleName, User user, HttpServletResponse response) throws Exception {
		UserServices userServices = new UserServices();
		if (!userServices.isRole(roleName, user)) {
			response.setStatus(401);
			throw NOT_PERMITTED;
		}
	}

	public static void throwNot2Roles(String roleName1, String roleName2, User user, HttpServletResponse response)
			throws Exception {
		UserServices userServices = new UserServices();
		if (!userServices.isRole(roleName1, user) && !userServices.isRole(roleName2, user)) {
			response.setStatus(401);
			throw NOT_PERMITTED;
		}
	}

	public static void throwNotInteger(String strInt, HttpServletResponse response) throws Exception {
		if (!StringUtils.isInteger(strInt)) {
			response.setStatus(400);
			throw new Exception("The id entered was not a number");
		}
	}

	public static void throwNotAdminEmployeeOrCurrUser(User currUser, int currUserId, HttpServletResponse response)
			throws Exception {
		UserServices userServices = new UserServices();
		boolean isAdmin = userServices.isRole("Admin", currUser);
		boolean isEmployee = userServices.isRole("Employee", currUser);
		boolean isCurrUser = userServices.isCurrUser(currUser, currUserId);
		if (!isAdmin && !isEmployee && !isCurrUser) {
			response.setStatus(401);
			throw NOT_PERMITTED;
		}
	}

	public static void throwNotAdminEmployeeOrContainsCurrUser(User currUser, List<User> users,
			HttpServletResponse response) throws Exception {
		UserServices userServices = new UserServices();
		boolean isAdmin = userServices.isRole("Admin", currUser);
		boolean isEmployee = userServices.isRole("Employee", currUser);
		boolean containsCurrUser = userServices.containsUser(currUser, users);
		if (!isAdmin && !isEmployee && !containsCurrUser) {
			response.setStatus(401);
			throw NOT_PERMITTED;
		}
	}

	public static void throwNotAdminOrContainsCurrUser(User currUser, List<User> users, HttpServletResponse response)
			throws Exception {
		UserServices userServices = new UserServices();
		boolean isAdmin = userServices.isRole("Admin", currUser);
		boolean containsCurrUser = userServices.containsUser(currUser, users);
		if (!isAdmin && !containsCurrUser) {
			response.setStatus(401);
			throw NOT_PERMITTED;
		}
	}
}
