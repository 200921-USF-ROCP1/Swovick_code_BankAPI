package com.revature.bankAPI.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.revature.bankAPIWeb.dao.implementations.AccountDAOImpl;
import com.revature.bankAPIWeb.dao.implementations.UserDAOImpl;
import com.revature.bankAPIWeb.dao.interfaces.AccountDAO;
import com.revature.bankAPIWeb.dao.interfaces.UserDAO;
import com.revature.bankAPIWeb.models.Account;
import com.revature.bankAPIWeb.models.AccountStatus;
import com.revature.bankAPIWeb.models.User;
import com.revature.bankAPIWeb.services.ConnectionService;

public class UserAccntDAOTesting {
	User admin;
	User employee;
	User general;
	UserDAO usrDao = new UserDAOImpl();

	// It is assumed that the User does not have any duplicate entries for username.
	// Typically the User and account tables and dropped and recreated before each
	// test.
	// Need to have run configuration changed such that
	// com.revature.bankAPI.services is the working directory
	@Test
	public void testUsers() {
		try {
			admin = new User("pswv", "password", "Peter", "Swovick", "pswv@gmail.com", 3, "Admin");
			int adminId = usrDao.create(admin);
			admin.setUserId(adminId);
			User adminFromTable = usrDao.get(adminId);
			User adminFromTable2 = usrDao.get("pswv");
			getUserEquals(admin, adminFromTable);
			getUserEquals(admin, adminFromTable2);
			employee = new User("pswov", "p@ssword", "Peter", "Swovich", "pswov@gmail.com", 2, "Employee");
			employee.setUserId(adminId);
			usrDao.update(employee);
			User employeeFromTable = usrDao.get(adminId);
			User employeeFromTable2 = usrDao.get("pswov");
			getUserEquals(employee, employeeFromTable);
			getUserEquals(employee, employeeFromTable2);
			User premium = new User("jsmith", "Password", "Joe", "Smith", "jsmith@yahoo.com", 4, "Premium");
			int premiumId = usrDao.create(premium);
			premium.setUserId(premiumId);
			User premiumFromTable = usrDao.get(premiumId);
			User premiumFromTable2 = usrDao.get("jsmith");
			getUserEquals(premium, premiumFromTable);
			getUserEquals(premium, premiumFromTable2);
			general = new User("jjsmith", "Pastword", "Joey", "Smithy", "jjsmith@yahoo.com", 1, "Standard");
			general.setUserId(premiumId);
			usrDao.update(general);
			User generalFromTable = usrDao.get(premiumId);
			User generalFromTable2 = usrDao.get("jjsmith");
			// usrDao.delete(premium);
			// usrDao.delete(employee);
			getUserEquals(general, generalFromTable);
			getUserEquals(general, generalFromTable2);
			testAccount1();
			testAccount2();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getUserEquals(User usr1, User usr2) {
		Assertions.assertEquals(usr1.getUserId(), usr2.getUserId());
		Assertions.assertEquals(usr1.getUsername(), usr2.getUsername());
		Assertions.assertEquals(usr1.getFirstName(), usr2.getFirstName());
		Assertions.assertEquals(usr1.getLastName(), usr2.getLastName());
		Assertions.assertEquals(usr1.getEmail(), usr2.getEmail());
		Assertions.assertEquals(usr1.getRole().getRoleId(), usr2.getRole().getRoleId());
		Assertions.assertEquals(usr1.getRole().getRole(), usr2.getRole().getRole());
	}

	public void testAccount1() {
		Account empAccnt = new Account(50.00, 1, "Pending", 1, "Checking");
		List<User> employeeUser = new ArrayList<User>();
		employeeUser.add(employee);
		empAccnt.setUsers(employeeUser);
		AccountDAO accountDao = new AccountDAOImpl();
		int empAccntId = accountDao.create(empAccnt);
		empAccnt.setAccountId(empAccntId);
		// employee.addAccount(empAccnt);
		Account empAccntTable = accountDao.get(empAccntId);
		getAccountEquals(empAccnt, empAccntTable);
		// getAccountEquals(employee.getAccount(empAccntId), empAccntTable);
		Account changeEmpAccnt = new Account(20.00, 2, "Open", 2, "Savings");
		changeEmpAccnt.setUsers(employeeUser);
		changeEmpAccnt.setAccountId(empAccntId);
		accountDao.update(changeEmpAccnt);
		// employee.setAccount(changeEmpAccnt);
		Account changEmpAccntTable = accountDao.get(empAccntId);
		getAccountEquals(changeEmpAccnt, changEmpAccntTable);
		// getAccountEquals(employee.getAccount(empAccntId), changEmpAccntTable);
		Account empAccnt2 = new Account(40.00, 2, "Open", 1, "Checking");
		empAccnt2.setUsers(employeeUser);
		int empAccntId2 = accountDao.create(empAccnt2);
		empAccnt2.setAccountId(empAccntId2);
		/// employee.addAccount(empAccnt2);
		Account empAccntTable2 = accountDao.get(empAccntId2);
		getAccountEquals(empAccnt2, empAccntTable2);
		// getAccountEquals(employee.getAccount(empAccntId2), empAccntTable2);
		AccountStatus open = new AccountStatus(2, "Open");
		Account[] openAccounts = { changeEmpAccnt, empAccnt2 };
		List<Account> openAccountsList = accountDao.getAccounts(open);
		// Account[] openAccountsArray = (Account[]) openAccountsList.toArray();
		getAccountsEqual(openAccounts, openAccountsList);
		Account changeEmpAccnt2 = new Account(30.00, 1, "Pending", 2, "Savings");
		changeEmpAccnt2.setAccountId(empAccntId2);
		changeEmpAccnt2.setUsers(employeeUser);
		accountDao.update(changeEmpAccnt2);
		// employee.setAccount(changeEmpAccnt2);
		Account changEmpAccntTable2 = accountDao.get(empAccntId2);
		AccountStatus pending = new AccountStatus(1, "Pending");
		Account[] pendingAccounts = { changeEmpAccnt2 };
		List<Account> pendingAccountsList = accountDao.getAccounts(pending);
		// Account[] pendingAccountsArray = (Account[]) pendingAccountsList.toArray();
		getAccountsEqual(pendingAccounts, pendingAccountsList);
		getAccountEquals(changeEmpAccnt2, changEmpAccntTable2);
		// getAccountEquals(employee.getAccount(empAccntId2), changEmpAccntTable2);
		Account[] employeeAccounts = { changeEmpAccnt, changeEmpAccnt2 };
		List<Account> employeeAccountsList = accountDao.getAccounts(employee);
		getAccountsEqual(employeeAccounts, employeeAccountsList);
		accountDao.addAccountOwner(changeEmpAccnt, general);
		Account firstGeneralAccount = changeEmpAccnt;
		Account[] generalAccounts = { changeEmpAccnt };
		List<Account> generalAccountsList = accountDao.getAccounts(general);
		getAccountsEqual(generalAccounts, generalAccountsList);
		employeeUser.add(general);
		changeEmpAccnt.setUsers(employeeUser);
		accountDao.delete(changeEmpAccnt);
	}

	public void testAccount2() {
		Account genAccnt = new Account(50.00, 3, "Closed", 1, "Checking");
		AccountDAO accountDao = new AccountDAOImpl();
		List<User> genUser = new ArrayList<User>();
		genUser.add(general);
		genAccnt.setUsers(genUser);
		int genAccntId = accountDao.create(genAccnt);
		genAccnt.setAccountId(genAccntId);
		// general.addAccount(genAccnt);
		Account empAccntTable = accountDao.get(genAccntId);
		getAccountEquals(genAccnt, empAccntTable);
		// getAccountEquals(general.getAccount(genAccntId), empAccntTable);
		Account changeGenAccnt = new Account(20.00, 4, "Denied", 2, "Savings");
		changeGenAccnt.setAccountId(genAccntId);
		changeGenAccnt.setUsers(genUser);
		accountDao.update(changeGenAccnt);
		// general.setAccount(changeGenAccnt);
		Account changEmpAccntTable = accountDao.get(genAccntId);
		getAccountEquals(changeGenAccnt, changEmpAccntTable);
		// getAccountEquals(general.getAccount(genAccntId), changEmpAccntTable);
		Account genAccnt2 = new Account(40.00, 4, "Denied", 1, "Checking");
		genAccnt2.setUsers(genUser);
		int genAccntId2 = accountDao.create(genAccnt2);
		genAccnt2.setAccountId(genAccntId2);
		// general.addAccount(genAccnt2);
		Account genAccntTable2 = accountDao.get(genAccntId2);
		getAccountEquals(genAccnt2, genAccntTable2);
		// getAccountEquals(general.getAccount(genAccntId2), genAccntTable2);
		Account changeGenAccnt2 = new Account(30.00, 3, "Closed", 2, "Savings");
		changeGenAccnt2.setAccountId(genAccntId2);
		changeGenAccnt2.setUsers(genUser);
		accountDao.update(changeGenAccnt2);
		// general.setAccount(changeGenAccnt2);
		Account changGenAccntTable2 = accountDao.get(genAccntId2);
		getAccountEquals(changeGenAccnt2, changGenAccntTable2);
		// getAccountEquals(general.getAccount(genAccntId2), changGenAccntTable2);
		Account[] genAccounts = { changeGenAccnt, changeGenAccnt2 };
		List<Account> genAccountsList = accountDao.getAccounts(general);
		getAccountsEqual(genAccounts, genAccountsList);
		// usrDao.delete(general);
		usrDao.delete(employee);
	}

	public void getAccountEquals(Account accnt1, Account accnt2) {
		Assertions.assertEquals(accnt1.getBalance(), accnt2.getBalance());
		Assertions.assertEquals(accnt1.getStatus().getStatusId(), accnt2.getStatus().getStatusId());
		Assertions.assertEquals(accnt1.getStatus().getStatus(), accnt2.getStatus().getStatus());
		Assertions.assertEquals(accnt1.getType().getTypeId(), accnt2.getType().getTypeId());
		Assertions.assertEquals(accnt1.getType().getType(), accnt2.getType().getType());
	}

	public void getAccountsEqual(Account[] accnts1, List<Account> accnts2) {
		if (accnts1.length == accnts2.size()) {
			for (int i = 0; i < accnts1.length; i++) {
				Account accnt1 = accnts1[i];
				Account accnt2 = accnts2.get(i);
				Assertions.assertEquals(accnt1.getBalance(), accnt2.getBalance());
				Assertions.assertEquals(accnt1.getStatus().getStatusId(), accnt2.getStatus().getStatusId());
				Assertions.assertEquals(accnt1.getStatus().getStatus(), accnt2.getStatus().getStatus());
				Assertions.assertEquals(accnt1.getType().getTypeId(), accnt2.getType().getTypeId());
				Assertions.assertEquals(accnt1.getType().getType(), accnt2.getType().getType());
			}
		} else {
			Assertions.assertEquals(accnts1[accnts1.length], accnts2.get(0)); // expected to be false
		}
	}

	@AfterAll
	public static void closeConnection() {
		ConnectionService.closeConnection();
	}
}
