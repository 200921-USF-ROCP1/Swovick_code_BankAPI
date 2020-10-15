package com.revature.bankAPIWeb.dao.interfaces;

import java.util.List;

import com.revature.bankAPIWeb.models.User;

public interface UserDAO {
	public int create(User usr);

	public User get(int id);

	public User get(String username);

	public void update(User usr);

	public void delete(User usr);

	public List<User> getAll();
}
