package hu.mik.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserConstants {
	@Value("${SOCIAL_IMAGE_FOLDER}")
	private static String IMAGE_LOCATION;

	public static final String DEFAULT_ROLE = "user";

	public static final String DEFAULT_PROFILE_PICTURE = "images/user.png";

	public static final String[] ALLOWED_PICTURE_TYPES = { "image/jpeg", "image/png" };

	@Value("${SOCIAL_IMAGE_FOLDER}")
	private void setImageLocation(String location) {
		IMAGE_LOCATION = location;
	}

	public static String getImageLocation() {
		return IMAGE_LOCATION;
	}

}
