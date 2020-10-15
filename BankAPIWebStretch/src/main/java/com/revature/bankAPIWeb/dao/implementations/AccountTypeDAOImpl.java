package com.revature.bankAPIWeb.dao.implementations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.revature.bankAPIWeb.dao.interfaces.GenericBankAPIreadDAO;
import com.revature.bankAPIWeb.models.AccountType;
import com.revature.bankAPIWeb.services.ConnectionService;

public class AccountTypeDAOImpl implements GenericBankAPIreadDAO<AccountType> {
	private Connection connection;

	public AccountTypeDAOImpl() {
		connection = ConnectionService.getConnection();
	}

	public AccountType get(int id) {
		AccountType accntType = null;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM account_types WHERE type_id = ?;");
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				accntType = new AccountType();
				accntType.setTypeId(id);
				accntType.setType(rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return accntType;
	}
}
