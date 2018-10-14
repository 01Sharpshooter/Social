package hu.mik.components;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;

import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.ui.MainUI;
import hu.mik.views.ProfileView;

@SuppressWarnings("serial")
public class UserDiv extends HorizontalLayout {
	private User user;

	public UserDiv(User user) {
		super();
		this.user = user;
		this.createContent();
	}

	private void createContent() {
		Image image = this.user.getVaadinImage();
		image.setHeight("100%");
		image.addStyleName(ThemeConstants.BORDERED_IMAGE);
		Label lblName = new Label(this.user.getFullName());
		this.setHeight("60px");
		this.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		this.addComponent(image);
		this.addComponent(lblName);
		this.addStyleName(ThemeConstants.BORDERED);
		this.addLayoutClickListener(this::layoutClickListener);

	}

	private void layoutClickListener(LayoutClickEvent event) {
		((MainUI) this.getUI()).getNavigator().navigateTo(ProfileView.NAME + "/" + this.user.getUsername());
	}

}
