package hu.mik.utils;

import javax.naming.Name;

public abstract class Converters {

	public static String convertLdapNameToUsername(Name name) {
		return name.get(3).substring(4);
	}

}
