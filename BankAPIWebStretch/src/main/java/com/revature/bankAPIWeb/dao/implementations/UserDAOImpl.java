package com.revature.bankAPIWeb.dao.implementations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.revature.bankAPIWeb.dao.interfaces.GenericBankAPIreadDAO;
import com.revature.bankAPIWeb.dao.interfaces.UserDAO;
import com.revature.bankAPIWeb.models.Account;
import com.revature.bankAPIWeb.models.Role;
import com.revature.bankAPIWeb.models.User;
import com.revature.bankAPIWeb.services.ConnectionService;

public class UserDAOImpl implements UserDAO {
	private Connection connection;
	private GenericBankAPIreadDAO<Role> roleDao;

	public UserDAOImpl() {
		connection = ConnectionService.getConnection();
		roleDao = new RoleDAOImpl();
	}

	public int create(User usr) {
		int usrId = 0;

		PreparedStatement ps;
		try {
			ps = connection.prepareStatement("INSERT INTO users"
					+ "(username,password, first_name, last_name, email, role_id) VALUES" + "(?, ?, ?, ?, ?, ?);",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, usr.getUsername());
			ps.setString(2, usr.getPassword());
			ps.setString(3, usr.getFirstName());
			ps.setString(4, usr.getLastName());
			ps.setString(5, usr.getEmail());
			Role usrRole = usr.getRole();
			ps.setInt(6, usrRole.getRoleId());
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			rs.next();
			usrId = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return usrId;
	}

	public User get(int id) {
		User usr = null;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE user_id = ?;");
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				usr = new User();
				usr.setUserId(id);
				usr.setUsername(rs.getString("username"));
				usr.setPassword(rs.getString("password"));
				usr.setFirstName(rs.getString("first_name"));
				usr.setLastName(rs.getString("last_name"));
				usr.setEmail(rs.getString("email"));
				int roleId = rs.getInt("role_id");
				Role usrRole = roleDao.get(roleId);
				usr.setRole(usrRole);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return usr;
	}

	public User get(String username) {
		User usr = null;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE username = ?;");
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				usr = new User();
				usr.setUserId(rs.getInt("user_id"));
				usr.setUsername(rs.getString("username"));
				usr.setPassword(rs.getString("password"));
				usr.setFirstName(rs.getString("first_name"));
				usr.setLastName(rs.getString("last_name"));
				usr.setEmail(rs.getString("email"));
				int roleId = rs.getInt("role_id");
				Role usrRole = roleDao.get(roleId);
				usr.setRole(usrRole);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return usr;
	}

	public List<Account> getUserAccounts(User usr) {
		List<Account> usrAccounts = null;
		try {
			PreparedStatement ps = connection
					.prepareStatement("SELECT users_accounts.account_id, users.user_id FROM users "
							+ "JOIN users_accounts ON users.user_id = users_accounts.user_id "
							+ "WHERE users.user_id = ?;");
			ps.setInt(1, usr.getUserId());
			ResultSet rs = ps.executeQuery();
			usrAccounts = new ArrayList<Account>();
			while (rs.next()) {
				int accntId = rs.getInt(1);
				AccountDAOImpl accountDao = new AccountDAOImpl();
				Account currAccount = accountDao.get(accntId);
				usrAccounts.add(currAccount);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return usrAccounts;

	}

	public void update(User usr) {
		try {
			PreparedStatement ps = connection.prepareStatement(
					"UPDATE users SET " + "username = ?, password = ?, first_name = ?, last_name = ?, "
							+ "email = ?, role_id = ? WHERE user_id = ?;");
			ps.setString(1, usr.getUsername());
			ps.setString(2, usr.getPassword());
			ps.setString(3, usr.getFirstName());
			ps.setString(4, usr.getLastName());
			ps.setString(5, usr.getEmail());
			Role usrRole = usr.getRole();
			ps.setInt(6, usrRole.getRoleId());
			ps.setInt(7, usr.getUserId());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void delete(User usr) {
		try {
			AccountDAOImpl accountDao = new AccountDAOImpl();
			accountDao.deleteAccounts(getUserAccounts(usr));
			PreparedStatement ps = connection.prepareStatement("DELETE FROM users where user_id =?;");
			ps.setInt(1, usr.getUserId());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public List<User> getAll() {
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM Users;");
			ResultSet rs = ps.executeQuery();
			ArrayList<User> usrs = new ArrayList<User>();
			while (rs.next()) {
				User usr = new User();
				usr.setUserId(rs.getInt("user_id"));
				usr.setUsername(rs.getString("username"));
				usr.setPassword(rs.getString("password"));
				usr.setFirstName(rs.getString("first_name"));
				usr.setLastName(rs.getString("last_name"));
				usr.setEmail(rs.getString("email"));
				int roleId = rs.getInt("role_id");
				Role usrRole = roleDao.get(roleId);
				usr.setRole(usrRole);
				usrs.add(usr);
			}
			return usrs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
