package com.revature.bankAPI.tests;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.revature.bankAPIWeb.dao.implementations.AccountStatusDAOImpl;
import com.revature.bankAPIWeb.dao.implementations.AccountTypeDAOImpl;
import com.revature.bankAPIWeb.dao.implementations.RoleDAOImpl;
import com.revature.bankAPIWeb.dao.interfaces.GenericBankAPIreadDAO;
import com.revature.bankAPIWeb.models.AccountStatus;
import com.revature.bankAPIWeb.models.AccountType;
import com.revature.bankAPIWeb.models.Role;
import com.revature.bankAPIWeb.services.ConnectionService;

public class ReadDAOTesting {
	
	@Test
	public void testRoleDAO() {
		GenericBankAPIreadDAO<Role> roleDao = new RoleDAOImpl();
		Role stdRole = roleDao.get(1); 
		Assertions.assertEquals(stdRole.getRole(), "Standard");
		Role empRole = roleDao.get(2); 
		Assertions.assertEquals(empRole.getRole(), "Employee");
		Role admnRole = roleDao.get(3); 
		Assertions.assertEquals(admnRole.getRole(), "Admin");
		Role premRole = roleDao.get(4); 
		Assertions.assertEquals(premRole.getRole(), "Premium");
		ConnectionService.closeConnection();
	}
	@Test
	public void testStatusDAO() {
		GenericBankAPIreadDAO<AccountStatus> statusDao = new AccountStatusDAOImpl();
		AccountStatus pending = statusDao.get(1); 
		Assertions.assertEquals(pending.getStatus(), "Pending");
		AccountStatus open = statusDao.get(2); 
		Assertions.assertEquals(open.getStatus(), "Open");
		AccountStatus closed = statusDao.get(3); 
		Assertions.assertEquals(closed.getStatus(), "Closed");
		AccountStatus denied = statusDao.get(4); 
		Assertions.assertEquals(denied.getStatus(), "Denied");
		//ConnectionService.closeConnection();
	}
	@Test
	public void testTypeDAO() {
		GenericBankAPIreadDAO<AccountType> typeDao = new AccountTypeDAOImpl();
		AccountType checking = typeDao.get(1); 
		Assertions.assertEquals(checking.getType(), "Checking");
		AccountType savings = typeDao.get(2); 
		Assertions.assertEquals(savings.getType(), "Savings");
	}
	
}
