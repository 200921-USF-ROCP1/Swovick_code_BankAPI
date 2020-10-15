package com.revature.bankAPIWeb.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.bankAPIWeb.helpers.ExceptionHandler;
import com.revature.bankAPIWeb.helpers.JsonString;
import com.revature.bankAPIWeb.helpers.Login;
import com.revature.bankAPIWeb.models.User;
import com.revature.bankAPIWeb.services.LoginService;
import com.revature.bankAPIWeb.services.UserServices;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getRequestURI();
		String[] parts = path.split("/");
		PrintWriter pw = response.getWriter();
		ObjectMapper mapper = new ObjectMapper();
		HttpSession session = request.getSession();
		JsonString jsonStr = new JsonString();
		User currUser = (User) session.getAttribute("CurrUser");
		response.setContentType("application/json");
		try {
			switch (parts[2]) {
			case "login":
				Login loginAttempt = mapper.readValue(request.getReader(), Login.class);
				logInUser(loginAttempt, currUser, pw, session, response);
				break;
			case "logout":
				ExceptionHandler.throwNullObject(currUser, response, "There was no user logged into the session");
				session.removeAttribute("CurrUser");
				response.setStatus(200);
				String message = "You have sucessfully logged out " + currUser.getUsername();
				jsonStr.printMessage(pw, message);
				break;
			case "register":
				ExceptionHandler.throwNullObject(currUser, response, "There was no user logged into the session");
				ExceptionHandler.throwNotRole("Admin", currUser, response);
				User registerUser = mapper.readValue(request.getReader(), User.class);
				registerUser(registerUser, response);
				pw.println(mapper.writeValueAsString(registerUser));
				break;
			default:
				response.sendError(404);
				break;
			}
		} catch (Exception e) {
			jsonStr.printMessage(pw, e.getMessage());
		}
	}

	private void registerUser(User registerUser, HttpServletResponse response) throws Exception {
		UserServices userServices = new UserServices();
		try {
			userServices.registerUser(registerUser);
			response.setStatus(201);
		} catch (Exception e) {
			response.setStatus(400);
			throw e;
		}
	}

	private void logInUser(Login loginAttempt, User currUser, PrintWriter pw, HttpSession session,
			HttpServletResponse response) throws Exception {
		LoginService loginService = new LoginService();
		ObjectMapper mapper = new ObjectMapper();
		if (currUser != null) {
			response.setStatus(400);
			throw new Exception("There is a user already logged in");
		}
		User loggedInUsr = loginService.logIn(loginAttempt);
		if (loggedInUsr != null) {
			session.setAttribute("CurrUser", loggedInUsr);
			response.setStatus(200);
			pw.println(mapper.writeValueAsString(loggedInUsr));
		} else {
			response.setStatus(400);
			throw new Exception("Invalid Credentials");
		}
	}

}
