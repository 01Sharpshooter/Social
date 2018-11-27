package hu.mik.components;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.views.MessagesView;
import hu.mik.views.ProfileView;

@SuppressWarnings("serial")
public class UserDiv extends CssLayout {
	private User user;

	public UserDiv(User user, boolean withMessageButton) {
		super();
		this.user = user;
		this.createContent(withMessageButton);
	}

	private void createContent(boolean withMessageButton) {
		Image image = this.user.getVaadinImage();
		image.setHeight("100%");
		image.addStyleName(ThemeConstants.BORDERED_IMAGE);
		Label lblName = new Label(this.user.getFullName());
		this.setHeight("60px");
		this.setWidth("25%");
		this.addComponent(image);
		if (withMessageButton) {
			this.createBtnMessage();
		}
		this.createBtnProfile();
		this.addComponent(lblName);
		this.addStyleName(ThemeConstants.BORDERED);
	}

	private void createBtnProfile() {
		Button btnProfile = new Button(VaadinIcons.USER);
		btnProfile.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		btnProfile.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		btnProfile.addStyleName(ValoTheme.BUTTON_LARGE);
		this.addComponent(btnProfile);
		btnProfile.addClickListener(
				e -> this.getUI().getNavigator().navigateTo(ProfileView.NAME + "/" + this.user.getUsername()));
	}

	private void createBtnMessage() {
		Button btnMessage = new Button(VaadinIcons.ENVELOPE_O);
		btnMessage.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		btnMessage.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		btnMessage.addStyleName(ValoTheme.BUTTON_LARGE);
		this.addComponent(btnMessage);
		btnMessage.addClickListener(
				e -> UI.getCurrent().getNavigator().navigateTo(MessagesView.NAME + "/" + this.user.getId()));
	}
}
