package hu.mik.components;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;

import hu.mik.beans.Message;
import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.utils.ProfileImageHelper;

@SuppressWarnings("serial")
public class UserDiv extends CssLayout {
	private User user;

	public UserDiv(User user, Message lastMessage, Integer loggedId) {
		super();
		this.user = user;

		Image image = new Image(null, new FileResource(ProfileImageHelper.loadUserImage(user.getImageName())));
		image.addStyleName(ThemeConstants.BORDERED_IMAGE);
		this.addComponent(image);
//		this.addLayoutClickListener(this::userDivClickListener);
		Label userDivLbl = new Label(
				user.getFullName() + "</br><span id=\"message\">" + lastMessage.getMessage() + "</span>",
				ContentMode.HTML);
		if (lastMessage.getSender() == null) {
			this.addLabelsToUserDiv(userDivLbl, null);
		} else if (!lastMessage.getConversation().isSeen() && !lastMessage.getSender().getId().equals(loggedId)) {
			this.addLabelsToUserDiv(userDivLbl, null);
			this.addStyleName(ThemeConstants.UNSEEN_MESSAGE);
		} else if (!lastMessage.getConversation().isSeen() && lastMessage.getSender().getId().equals(loggedId)) {
			this.addLabelsToUserDiv(userDivLbl, VaadinIcons.ANGLE_DOUBLE_RIGHT);
		} else if (lastMessage.getConversation().isSeen() && lastMessage.getSender().getId().equals(loggedId)) {
			this.addLabelsToUserDiv(userDivLbl, VaadinIcons.EYE);
		} else {
			this.addLabelsToUserDiv(userDivLbl, null);
		}
	}

	private void addLabelsToUserDiv(Label userDivLbl, VaadinIcons icon) {
		this.addComponent(userDivLbl);
		if (icon != null) {
			this.addComponent(new Label(icon.getHtml(), ContentMode.HTML));
		} else {
			this.addComponent(new Label());
		}
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
