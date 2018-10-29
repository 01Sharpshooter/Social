package hu.mik.utils;

import javax.naming.Name;

public abstract class Converters {

	public static String convertLdapNameToUsername(Name name) {
		return name.getSuffix(3).toString();
	}

}
