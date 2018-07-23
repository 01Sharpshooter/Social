package hu.mik.enums;

public enum Texts {
	//@formatter:off
	DEFAULT_ERROR_MESSAGE("Sorry, something went wrong, please contact the administrator."),
	NO_USER_FOUND_FROM_SEARCH("Sorry, we could not find the person you were looking for."),
	FRIEND_REQUEST_NOTIFICATION("Request has been sent to"),

	BTN_FRIEND_REQUEST("Friend request"),
	BTN_ACCEPT_REQUEST("Accept request"),
	BTN_DECLINE_REQUEST("Decline request"),
	BTN_REMOVE_FRIEND("Remove friend"),
	BTN_CANCEL_REQUEST("Cancel request")
	;
	//@formatter:on

	private final String text;
	private final char space = ' ';

	private Texts(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public String getTextWithParams(String... param) {
		StringBuilder sb = new StringBuilder(this.text);
		for (int i = 0; i < param.length; i++) {
			sb.append(this.space).append(param[i]);
		}
		return sb.toString();
	}

}
