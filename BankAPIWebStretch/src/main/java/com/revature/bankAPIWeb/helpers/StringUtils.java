package com.revature.bankAPIWeb.helpers;

public class StringUtils {
	public static boolean isInteger(String s) {
		for (int i=0; i<s.length(); i++) {
			if(!Character.isDigit(s.charAt(i)) || s==null || s.length()==0) {
				return false;
			}
		}
		return true;
	}
}
