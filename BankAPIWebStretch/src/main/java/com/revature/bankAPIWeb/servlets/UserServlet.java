package com.revature.bankAPIWeb.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.bankAPIWeb.helpers.ExceptionHandler;
import com.revature.bankAPIWeb.helpers.JsonString;
import com.revature.bankAPIWeb.helpers.StringUtils;
import com.revature.bankAPIWeb.models.User;
import com.revature.bankAPIWeb.services.UserServices;

/**
 * Servlet implementation class UserServlet
 */
public class UserServlet extends HttpServlet {
	private UserServices userServices;
	private JsonString jsonStr;

	public UserServlet() {
		userServices = new UserServices();
		jsonStr = new JsonString();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getRequestURI();
		String[] parts = path.split("/");
		HttpSession session = request.getSession();
		User currUser = (User) session.getAttribute("CurrUser");
		ObjectMapper mapper = new ObjectMapper();
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		try {
			ExceptionHandler.throwNullObject(currUser, response, "There was no user logged into the session");
			boolean isAdmin = userServices.isRole("Admin", currUser);
			boolean isEmployee = userServices.isRole("Employee", currUser);
			if (parts.length == 3) {
				ExceptionHandler.throwNot2Roles("Admin", "Employee", currUser, response);
				List<User> allUsers = userServices.getAllUsers();
				pw.println(mapper.writeValueAsString(allUsers));
				response.setStatus(200);
			} else if (parts.length == 4) {
				String strUserId = parts[3];
				ExceptionHandler.throwNotInteger(strUserId, response);
				int user_id = Integer.parseInt(parts[3]);
				ExceptionHandler.throwNotAdminEmployeeOrCurrUser(currUser, user_id, response);
				User userToGet = userServices.getUser(user_id);
				ExceptionHandler.throwNullObject(userToGet, response, "The user you are looking for does not exist");
				pw.println(mapper.writeValueAsString(userToGet));
				response.setStatus(200);
			} else {
				response.sendError(404);
			}
		} catch (Exception e) {
			jsonStr.printMessage(pw, e.getMessage());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getRequestURI();
		String[] parts = path.split("/");
		HttpSession session = request.getSession();
		User currUser = (User) session.getAttribute("CurrUser");
		ObjectMapper mapper = new ObjectMapper();
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		try {
			ExceptionHandler.throwNullObject(currUser, response, "There was no user logged into the session");
			boolean isAdmin = userServices.isRole("Admin", currUser);

			if (parts.length == 3) {
				User updatedUser = mapper.readValue(request.getReader(), User.class);
				updateUser(updatedUser, currUser, session, response);
				response.setStatus(200);
				pw.println(mapper.writeValueAsString(updatedUser));
			} else if (parts.length == 4) {
				String[] parts2 = parts[3].split("\\?");
				String upgrade = parts2[0];
				if (upgrade.equals("upgrades")) {
					String queryString = request.getQueryString();
					String[] params = queryString.split("=");
					String strAccountId = params[1];
					ExceptionHandler.throwNotInteger(strAccountId, response);
					int accountId = Integer.parseInt(strAccountId);
					User userToUpgrade = mapper.readValue(request.getReader(), User.class);
					upgradeSpecifiedUser(currUser, userToUpgrade, accountId, 10, session, response);
				} else {
					response.sendError(404);
				}
			} else {
				response.sendError(404);
			}
		} catch (Exception e) {
			jsonStr.printMessage(pw, e.getMessage());
		}
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getRequestURI();
		String[] parts = path.split("/");
		HttpSession session = request.getSession();
		User currUser = (User) session.getAttribute("CurrUser");
		ObjectMapper mapper = new ObjectMapper();
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		try {
			ExceptionHandler.throwNullObject(currUser, response, "There was no user logged into the session");
			if (parts.length != 4) {
				response.sendError(404);
			} else {
				String strUserId = parts[3];
				ExceptionHandler.throwNotInteger(strUserId, response);
				ExceptionHandler.throwNotRole("Admin", currUser, response);
				int userId = Integer.parseInt(strUserId);
				User userToDelete = userServices.getUser(userId);
				ExceptionHandler.throwNullObject(userToDelete, response, "The user you are deleting does not exist");
				userServices.deleteUser(userToDelete);
				response.setStatus(200);
				pw.println(mapper.writeValueAsString(userToDelete));
			}
		} catch (Exception e) {
			jsonStr.printMessage(pw, e.getMessage());
		}
	}

	private void updateUser(User updatedUser, User currUser, HttpSession session, HttpServletResponse response)
			throws Exception {
		boolean isAdmin = userServices.isRole("Admin", currUser);
		boolean isCurrUser = userServices.isCurrUser(currUser, updatedUser.getUserId());
		if (userServices.getUser(updatedUser.getUserId()) != null) {
			if (isAdmin) {
				userServices.updateUser(updatedUser);
				if (isCurrUser) {
					session.setAttribute("CurrUser", updatedUser);
				}
			} else if (isCurrUser) {
				User stndrdUpdatedUser = userServices.updateUser(updatedUser, currUser);
				session.setAttribute("CurrUser", stndrdUpdatedUser);
			} else {
				response.setStatus(401);
				throw new Exception("The requested action is not permitted");
			}
		} else {
			response.setStatus(400);
			throw new Exception("The user entered does not exist");
		}
	}

	private void upgradeSpecifiedUser(User currUser, User userToUpgrade, int accountId, double cost,
			HttpSession session, HttpServletResponse response) throws Exception {
		boolean isAdmin = userServices.isRole("Admin", currUser);
		PrintWriter pw = response.getWriter();
		User userInTable = userServices.getUser(userToUpgrade.getUserId());
		if (isAdmin && userServices.isRole("Standard", userInTable)) {
			upgradeUser(userToUpgrade, accountId, cost, pw, response);
		} else if (userServices.isCurrUser(currUser, userToUpgrade.getUserId())
				&& userServices.isRole("Standard", currUser)) {
			upgradeUser(userToUpgrade, accountId, cost, pw, response);
			session.setAttribute("CurrUser", userToUpgrade);
		} else {
			response.setStatus(401);
			throw new Exception("The requested action is not permitted");
		}
	}

	private void upgradeUser(User userToUpgrade, int accountId, double balance, PrintWriter pw,
			HttpServletResponse response) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		try {
			userServices.upgradeToPremium(userToUpgrade, accountId, balance);
			response.setStatus(200);
			pw.println(mapper.writeValueAsString(userToUpgrade));
		} catch (Exception e) {
			if (e.getMessage().equals("401")) {
				response.setStatus(401);
				throw new Exception("The requested action is not permitted");
			} else {
				response.setStatus(400);
				throw e;
			}
		}
	}
}
