package com.revature.bankAPIWeb.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.bankAPIWeb.helpers.AccountTransfer;
import com.revature.bankAPIWeb.helpers.ChangeBalance;
import com.revature.bankAPIWeb.helpers.ExceptionHandler;
import com.revature.bankAPIWeb.helpers.JsonString;
import com.revature.bankAPIWeb.helpers.StringUtils;
import com.revature.bankAPIWeb.models.Account;
import com.revature.bankAPIWeb.models.User;
import com.revature.bankAPIWeb.services.AccountServices;
import com.revature.bankAPIWeb.services.UserServices;

public class AccountServlet extends HttpServlet {
	private UserServices userServices;
	private AccountServices accountServices;
	private JsonString jsonStr;

	public AccountServlet() {
		userServices = new UserServices();
		accountServices = new AccountServices();
		jsonStr = new JsonString();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
			if (parts.length == 3) {
				ExceptionHandler.throwNot2Roles("Admin", "Employee", currUser, response);
				List<Account> accounts = accountServices.getAllAccounts();
				pw.println(mapper.writeValueAsString(accounts));
			} else if (parts.length == 4) {
				String strAccntId = parts[3];
				ExceptionHandler.throwNotInteger(strAccntId, response);
				int accntId = Integer.parseInt(strAccntId);
				Account currAccount = accountServices.getAccountById(accntId);
				List<User> accntUsers = currAccount.getUsers();
				ExceptionHandler.throwNotAdminEmployeeOrContainsCurrUser(currUser, accntUsers, response);
				response.setStatus(200);
				pw.println(mapper.writeValueAsString(currAccount));
			} else if (parts.length == 5) {
				String strId = parts[4];
				ExceptionHandler.throwNotInteger(strId, response);
				switch (parts[3]) {
				case "owner":
					int userId = Integer.parseInt(strId);
					ExceptionHandler.throwNotAdminEmployeeOrCurrUser(currUser, userId, response);
					List<Account> userAccounts = accountServices.getAccountsByUser(userId);
					response.setStatus(200);
					pw.println(mapper.writeValueAsString(userAccounts));
					break;
				case "status":
					int statusId = Integer.parseInt(strId);
					ExceptionHandler.throwNot2Roles("Admin", "Employee", currUser, response);
					List<Account> statusAccounts = accountServices.getAccountsByStatus(statusId);
					response.setStatus(200);
					pw.println(mapper.writeValueAsString(statusAccounts));
					break;
				default:
					response.sendError(404);
					break;
				}
			} else {
				response.sendError(404);
			}
		} catch (Exception e) {
			jsonStr.printMessage(pw, e.getMessage());
		}

	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
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
			if (parts.length == 3) {
				Account account = mapper.readValue(request.getReader(), Account.class);
				List<User> accountUsers = account.getUsers();
				ExceptionHandler.throwNotAdminEmployeeOrContainsCurrUser(currUser, accountUsers, response);
				if (userServices.isRole("Standard", currUser)) {
					accountUsers = new ArrayList<User>();
					accountUsers.add(currUser);
					account.setUsers(accountUsers);
				}
				accountServices.createAccount(account);
				response.setStatus(201);
				pw.println(mapper.writeValueAsString(account));
			} else if (parts.length == 4) {
				switch (parts[3]) {
				case "deposit":
					ChangeBalance depositBalance = mapper.readValue(request.getReader(), ChangeBalance.class);
					Account toDeposit = accountServices.getAccountById(depositBalance.getAccountId());
					ExceptionHandler.throwNullObject(toDeposit, response,
							"The account you are depositing to does not exist");
					List<User> toDepositUsers = toDeposit.getUsers();
					ExceptionHandler.throwNotAdminOrContainsCurrUser(currUser, toDepositUsers, response);
					double depositAmnt = depositBalance.getAmount();
					accountServices.deposit(depositAmnt, toDeposit);
					response.setStatus(200);
					String depositMessage = "$" + depositAmnt + " has been deposited to Account #"
							+ toDeposit.getAccountId();
					jsonStr.printMessage(pw, depositMessage);
					break;
				case "withdraw":
					ChangeBalance withdrawBalance = mapper.readValue(request.getReader(), ChangeBalance.class);
					Account toWithdraw = accountServices.getAccountById(withdrawBalance.getAccountId());
					ExceptionHandler.throwNullObject(toWithdraw, response,
							"The account you are withdrawing from does not exist");
					List<User> toWithdrawUsers = toWithdraw.getUsers();
					ExceptionHandler.throwNotAdminOrContainsCurrUser(currUser, toWithdrawUsers, response);
					double withdrawAmnt = withdrawBalance.getAmount();
					withdrawal(withdrawAmnt, toWithdraw, response);
					response.setStatus(200);
					String withdrawalMessage = "$" + withdrawAmnt + " has been withdrawn from Account #"
							+ toWithdraw.getAccountId();
					jsonStr.printMessage(pw, withdrawalMessage);
					break;
				case "transfer":
					AccountTransfer accountTransfer = mapper.readValue(request.getReader(), AccountTransfer.class);
					Account toTransferFrom = accountServices.getAccountById(accountTransfer.getSourceAccountId());
					Account toTransferTo = accountServices.getAccountById(accountTransfer.getTargetAccountId());
					String nullAccountMessage = "The account(s) you are transferring between to does not exist";
					ExceptionHandler.throwNullObject(toTransferTo, response, nullAccountMessage);
					ExceptionHandler.throwNullObject(toTransferFrom, response, nullAccountMessage);
					List<User> toTransferFromUsers = toTransferFrom.getUsers();
					ExceptionHandler.throwNotAdminOrContainsCurrUser(currUser, toTransferFromUsers, response);
					double transferAmnt = accountTransfer.getAmount();
					transfer(transferAmnt, toTransferFrom, toTransferTo, response);
					response.setStatus(200);
					String message = "$" + transferAmnt + " has been transferred from Account #"
							+ toTransferFrom.getAccountId() + " to Account #" + toTransferTo.getAccountId();
					jsonStr.printMessage(pw, message);
					break;
				}
			}
		} catch (Exception e) {
			jsonStr.printMessage(pw, e.getMessage());
		}
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response)
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
			if (parts.length == 3) {
				String queryString = request.getQueryString();
				if (queryString == null) {
					Account account = mapper.readValue(request.getReader(), Account.class);
					ExceptionHandler.throwNotRole("Admin", currUser, response);
					accountServices.updateAccount(account);
					response.setStatus(200);
					pw.println(mapper.writeValueAsString(account));
				} else {
					String[] queries = queryString.split("=");
					if (queries[0].equals("user_id_to_add") && StringUtils.isInteger(queries[1])) {
						int userIdToAdd = Integer.parseInt(queries[1]);
						Account account = mapper.readValue(request.getReader(), Account.class);
						List<User> accountUsers = account.getUsers();
						addAccountUser(currUser, accountUsers, account, userIdToAdd, response);
						response.setStatus(200);
						pw.println(mapper.writeValueAsString(account));
					} else {
						response.sendError(404);
					}
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
				int accntId = Integer.parseInt(strUserId);
				Account accntToDelete = accountServices.getAccountById(accntId);
				ExceptionHandler.throwNullObject(accntToDelete, response,
						"The account you are deleting does not exist");
				accountServices.deleteAccount(accntToDelete);
				response.setStatus(200);
				pw.println(mapper.writeValueAsString(accntToDelete));
			}
		} catch (Exception e) {
			jsonStr.printMessage(pw, e.getMessage());
		}
	}

	private void withdrawal(double withdrawAmnt, Account toWithdraw, HttpServletResponse response) throws Exception {
		try {
			accountServices.withdraw(withdrawAmnt, toWithdraw);
			response.setStatus(200);
		} catch (Exception e) {
			response.setStatus(400);
			throw e;
		}
	}

	private void transfer(double transferAmnt, Account toTransferFrom, Account toTransferTo,
			HttpServletResponse response) throws Exception {
		try {
			accountServices.transfer(transferAmnt, toTransferFrom, toTransferTo);
		} catch (Exception e) {
			response.setStatus(400);
			throw e;
		}
	}

	private void addAccountUser(User currUser, List<User> accountUsers, Account account, int userIdToAdd,
			HttpServletResponse response) throws Exception {
		boolean isPremium = userServices.isRole("Premium", currUser);
		boolean containsCurrUser = userServices.containsUser(currUser, accountUsers);
		boolean isAdmin = userServices.isRole("Admin", currUser);
		// Technically do not need parentheses below because of && has higher operator
		// precedence than ||.
		// This is done mostly for clarity.
		if ((isPremium && containsCurrUser) || isAdmin) {
			User userToAdd = userServices.getUser(userIdToAdd);
			if (!userServices.containsUser(userToAdd, accountUsers)) {
				accountUsers.add(userToAdd);
				account.setUsers(accountUsers);
				accountServices.updateAccount(account);
			} else {
				response.setStatus(400);
				throw new Exception("The user provided is already an owner for this account");
			}
		} else {
			response.setStatus(401);
			throw new Exception("The requested action is not permitted");
		}
	}
}
