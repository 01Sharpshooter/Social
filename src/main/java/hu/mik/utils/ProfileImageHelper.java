package hu.mik.utils;

import java.io.File;

import hu.mik.constants.UserConstants;

public class ProfileImageHelper {

	public static File loadUserImage(String imageName) {
		File file = new File(UserConstants.PROFILE_PICTURE_LOCATION + imageName);
		if (file.exists()) {
			return file;
		} else {
			file = new File(UserConstants.PROFILE_PICTURE_LOCATION + UserConstants.DEFAULT_PROFILE_PICTURE_NAME);
			return file;
		}
	}

}
